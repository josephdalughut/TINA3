package ng.edu.aun.tina3.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.WakefulBroadcastReceiver;

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
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/29/16.
 */

public class PredictionService extends IntentService {

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
        Log.d("Prediction service started.");
        Preferences.getInstance().setIsPredicting(true);
        DateTime now = DateTime.now(DateTimeZone.getDefault());
        String date = Value.TO.stringValue(now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth());
        DateTime yesterday = now.minusDays(1);
        String yesterdayDate = Value.TO.stringValue(yesterday.getYear() + "_" + yesterday.getMonthOfYear() + "_" + yesterday.getDayOfMonth());
        try {
            startPrediction(date, yesterdayDate);
            onSuccess(date);
            ActionService.setAlarm(this);
        } catch (LitigyException e) {
            e.printStackTrace();
            onError(e);
        }
        AlarmScheduler.setAlarm(this);
        stopSelf();
    }

    private void startPrediction(final String date, final String yesterday) throws LitigyException{
        getApplicationContext().sendBroadcast(new Intent(Constants.INTENT_PREDICTION_STARTED));
        User u;
        try {
            u = Authenticator.getInstance().getUser(false);
        } catch (Exception e) {
            onSuccess(date);
            return;
        }
        final User user = u;
        SmartPlugTable smartPlugTable = new SmartPlugTable();
        SmartPlug.SmartPlugList smartPlugs = smartPlugTable.getSmartPlugsForUser(user.getId());
        if(smartPlugs.isEmpty()){
            onSuccess(date);
        }else{
            predict(user, date, yesterday, smartPlugs);
        }
    }

    private void predict(User user, final String date, String yesterday, SmartPlug.SmartPlugList smartPlugList){
        final EventTable eventTable = new EventTable();
        for(final SmartPlug smartPlug: smartPlugList){
            final Event.EventList yesterdaysEvents = eventTable.getAllDoneEvents(user.getId(), smartPlug.getId(), yesterday);
            EventApi.predict(date,  smartPlug.getId(), yesterdaysEvents, new DoubleReceiver<Event.EventList, LitigyException>() {
                @Override
                public void onReceive(Event.EventList events, LitigyException e) {

                }

                @Override
                public void onReceive1(Event.EventList events) {
                    for(Event event: events){
                        try {
                            eventTable.addEvent(event, false, false);
                        } catch (ConflictException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
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

        @Override
        public void onReceive(Context context, Intent receivedIntent) {
            switch (receivedIntent.getAction()){
                case Intent.ACTION_BOOT_COMPLETED:
                    setAlarm(context);
                    break;
            }
        }

        public static void setAlarm(Context context){
            try {
                User user = Authenticator.getInstance().getUser(false);
            } catch (Exception e) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmTask.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }

        public static void cancelAlarm(Context context){
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmTask.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.cancel(alarmIntent);
        }


    }


    public static class AlarmTask extends WakefulBroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            startWakefulService(context, new Intent(context, PredictionService.class));
        }
    }
}
