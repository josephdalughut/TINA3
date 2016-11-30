package ng.edu.aun.tina3.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.util.Value;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;

import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.data.EventTable;
import ng.edu.aun.tina3.data.Preferences;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.error.ConflictException;
import ng.edu.aun.tina3.rest.api.EventApi;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.rest.model.User;

/**
 * Created by joeyblack on 11/29/16.
 */

public class PredictionService extends IntentService {

    public static volatile boolean PREDICTING = false;

    public static String LOG_TAG = "PredictionService";

    public static class Constants {
        public static final String INTENT_PREDICTION_STARTED = "ng.edu.aun.tina3.service.PredictionService.STARTED";
        public static final String INTENT_PREDICTION_SUCCESS = "ng.edu.aun.tina3.service.PredictionService.SUCCESS";
        public static final String INTENT_PREDICTION_FAIL = "ng.edu.aun.tina3.service.PredictionService.FAIL";
    }

    public PredictionService(){
        super("PredictionService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PredictionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Prediction service called.");
        DateTime now = DateTime.now(DateTimeZone.getDefault());
        String date = Value.TO.stringValue(now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth());
        if(Value.IS.SAME.stringValue(Preferences.getInstance().lastPredicted(), date)){
            Log.d(LOG_TAG, "Today has already been predicted ("+date+"), returning");
            stopSelf();
            return;
        }
        PREDICTING = true;
        Preferences.getInstance().setIsPredicting(true);
        DateTime yesterday = now.minusDays(1);
        String yesterdayDate = Value.TO.stringValue(yesterday.getYear() + "_" + yesterday.getMonthOfYear() + "_" + yesterday.getDayOfMonth());
        Log.d(LOG_TAG, "Today? "+date+", yesterday: "+yesterdayDate);
        try {
            startPrediction(date, yesterdayDate);
        } catch (LitigyException e) {
            e.printStackTrace();
            onError(e);
        }
        AlarmScheduler.setAlarm(this);
        stopSelf();
    }

    private void startPrediction(final String date, final String yesterday) throws LitigyException{
        Log.d(LOG_TAG, "Prediction started");
        getApplicationContext().sendBroadcast(new Intent(Constants.INTENT_PREDICTION_STARTED));
        User u;
        try {
            u = Authenticator.getInstance().getUser(false);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Prediction returned prematurely, no user!");
            onSuccess(date);
            return;
        }
        final User user = u;
        SmartPlugTable smartPlugTable = new SmartPlugTable();
        SmartPlug.SmartPlugList smartPlugs = smartPlugTable.getSmartPlugsForUser(user.getId());
        if(smartPlugs.isEmpty()){
            Log.d(LOG_TAG, "No smart plugs found, returning");
            onSuccess(date);
            smartPlugTable.release();
        }else{
            predict(user, date, yesterday, smartPlugs);
        }
        smartPlugTable.release();
    }

    private void predict(final User user, final String date, String yesterday, SmartPlug.SmartPlugList smartPlugList){
        Log.d(LOG_TAG, "Going to predict for "+smartPlugList.size()+" smart plug(s)");
        final EventTable eventTable = new EventTable();
        for(final SmartPlug smartPlug: smartPlugList){
            final Event.EventList yesterdaysEvents = eventTable.getAllDoneEvents(user.getId(), smartPlug.getId(), yesterday);
            Log.d(LOG_TAG, "Going to predict for "+yesterdaysEvents.size()+" events, calling Prediction API");
            EventApi.predict(date,  smartPlug.getId(), yesterdaysEvents, new DoubleReceiver<Event.EventList, LitigyException>() {
                @Override
                public void onReceive(Event.EventList events, LitigyException e) {

                }

                @Override
                public void onReceive1(Event.EventList events) {
                    Log.d(LOG_TAG, "Successfully predicted "+events.size() + " events, caching");
                    eventTable.closeAllPreviousPredictions(user.getId(), smartPlug.getId(), date);
                    for(Event event: events){
                        try {
                            eventTable.addEvent(event, false, false);
                        } catch (ConflictException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!events.isEmpty()){
                        ActionService.setAlarm(PredictionService.this);
                    }
                    eventTable.release();
                    onSuccess(date);
                }

                @Override
                public void onReceive2(LitigyException e) {
                    Log.d(LOG_TAG, "Error predicting events, is: "+e.getMessage());
                    eventTable.release();
                    onError(e);
                }
            });
        }
    }

    private void onSuccess(String date){
        Preferences.getInstance().setIsPredicting(false).setLastPredicted(date);
        getApplicationContext().sendBroadcast(new Intent(Constants.INTENT_PREDICTION_SUCCESS));
    }

    private void onError(LitigyException e){
        Preferences.getInstance().setIsPredicting(false);
        Intent intent = new Intent(Constants.INTENT_PREDICTION_FAIL);
        intent.putExtra("statusCode", e.getStatusCode());
        intent.putExtra("message", e.getMessage());
        getApplicationContext().sendBroadcast(intent);
    }

    public static class AlarmScheduler extends BroadcastReceiver {

        private static String LOG_TAG = "AlarmScheduler";

        @Override
        public void onReceive(Context context, Intent receivedIntent) {
            switch (receivedIntent.getAction()){
                case Intent.ACTION_BOOT_COMPLETED:
                    Log.d(LOG_TAG, "Boot completed, setting prediction alarm");
                    setAlarm(context);
                    break;
            }
        }

        public static void setAlarm(Context context){
            try {
                User user = Authenticator.getInstance().getUser(false);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Couldn't set prediction alarm, user is null");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);

            Intent intent = new Intent(context, AlarmTask.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(alarmIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

            //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            //        AlarmManager.INTERVAL_DAY, alarmIntent);
            Log.d(LOG_TAG, "Alarm set for "+calendar.getTimeInMillis() + " (millis)");
        }

        public static void cancelAlarm(Context context){
            Log.d(LOG_TAG, "Cancelling Prediction alarm");
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmTask.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.cancel(alarmIntent);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PREDICTING = false;
    }

    public static class AlarmTask extends WakefulBroadcastReceiver{

        private static String LOG_TAG = "AlarmTask";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Prediction Alarm Task called, starting Prediction Service at "+System.currentTimeMillis());
            startWakefulService(context, new Intent(context, PredictionService.class));
        }
    }
}
