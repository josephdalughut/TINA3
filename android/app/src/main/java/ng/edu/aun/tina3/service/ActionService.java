package ng.edu.aun.tina3.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.util.Value;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.data.EventTable;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.error.ConflictException;
import ng.edu.aun.tina3.gui.activity.Activity;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.rest.model.User;

/**
 * Created by joeyblack on 11/29/16.
 */

public class ActionService extends IntentService {

    public static class Constants {
        public static int NOTIFICATION_ID = 14943;
        //public static final String ACTION_ALARM = "ng.edu.aun.tina3.service.ActionService.ALARM";
    }

    public ActionService(){
        super("ActionService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        User user = null;
        try {
            user = Authenticator.getInstance().getUser(false);
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
            return;
        }
        EventTable eventTable = new EventTable();
        SmartPlugTable smartPlugTable = new SmartPlugTable();
        Event event = eventTable.getTopSignificantEvent(user.getId());
        SmartPlug smartPlug = smartPlugTable.getSmartPlug(event);
        DateTime now = DateTime.now(DateTimeZone.UTC);
        if(event.getStart() > now.getMinuteOfDay()){
            notifySoon(smartPlug, event, now);
        }else{
            notifyStarting(smartPlug, event, now);
            startEvent(eventTable, smartPlugTable, smartPlug, event);
        }
    }

    private void notifyStarting(SmartPlug smartPlug, Event event, DateTime now){
        if(!Value.IS.ANY.nullValue(smartPlug, event)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            String toWhat;
            switch (Event.Status.values()[event.getStatus()]) {
                case SCHEDULED:
                    toWhat = "on";
                    break;
                case ONGOING:
                    toWhat = "off";
                    break;
                default:
                    setAlarm(this);
                    return;
            }
            builder.setSmallIcon(R.drawable.ic_stat_tina3)
                    .setContentTitle(Value.IS.emptyValue(smartPlug.getName()) ? "Smart-plug" : smartPlug.getName())
                    .setOngoing(true)
                    .setLights(getResources().getColor(R.color.flat_belize_hole), 1000, 5000)
                    .setContentText("Switching "+ toWhat + " your " +
                            (Value.IS.nullValue(smartPlug.getName()) ? "Smart-plug " : "("+smartPlug.getName()+") " ));
            Intent resultIntent = new Intent(this, Activity.class);
            resultIntent.putExtra("smartPlug", smartPlug.getId());
            resultIntent.putExtra("event", event.getId());
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(Constants.NOTIFICATION_ID, builder.build());
        }
    }

    private void startEvent(final EventTable eventTable, final SmartPlugTable smartPlugTable, final SmartPlug smartPlug, final Event event){
        if(!Value.IS.ANY.nullValue(smartPlug, event)) {
            switch (Event.Status.values()[event.getStatus()]) {
                case SCHEDULED:
                    try {
                        eventTable.updateEvent(event.setStatus(Event.Status.ONGOING.ordinal()), false, true);
                    } catch (ConflictException e) {
                        e.printStackTrace();//won't happen
                    }
                    SmartPlugApi.on(smartPlug.getId(), new DoubleReceiver<SmartPlug, LitigyException>() {
                        @Override
                        public void onReceive(SmartPlug smartPlug, LitigyException e) {

                        }

                        @Override
                        public void onReceive1(SmartPlug smartPlug) {
                            try {
                                eventTable.updateEvent(event.setStatus(Event.Status.ONGOING.ordinal()), false, true);
                            } catch (ConflictException e1) {
                                e1.printStackTrace(); //won't be called
                            }
                            smartPlugTable.updateSmartPlug(smartPlug, true);
                            endEvent(smartPlug, event);
                            setAlarm(ActionService.this);
                        }

                        @Override
                        public void onReceive2(LitigyException e) {
                            try {
                                eventTable.updateEvent(event.setStatus(Event.Status.FAILED.ordinal()), false, true);
                            } catch (ConflictException e1) {
                                e1.printStackTrace(); //won't be called
                            }
                            notifyFailOn(smartPlug, event);
                            setAlarm(ActionService.this);
                        }
                    });
                    break;
                case ONGOING:
                    SmartPlugApi.off(smartPlug.getId(), new DoubleReceiver<SmartPlug, LitigyException>() {
                        @Override
                        public void onReceive(SmartPlug smartPlug, LitigyException e) {

                        }

                        @Override
                        public void onReceive1(SmartPlug smartPlug) {
                            try {
                                eventTable.updateEvent(event.setStatus(Event.Status.DONE.ordinal()), false, true);
                            } catch (ConflictException e1) {
                                e1.printStackTrace(); //won't be called
                            }
                            smartPlugTable.updateSmartPlug(smartPlug, true);
                            endEvent(smartPlug, event);
                            setAlarm(ActionService.this);
                        }

                        @Override
                        public void onReceive2(LitigyException e) {
                            try {
                                eventTable.updateEvent(event.setStatus(Event.Status.FAILED.ordinal()), false, true);
                            } catch (ConflictException e1) {
                                e1.printStackTrace(); //won't be called
                            }
                            notifyFailOff(smartPlug, event);
                            setAlarm(ActionService.this);
                        }
                    });
                    break;
            }
        }
    }

    private void notifyFailOn(SmartPlug smartPlug, Event event){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_tina3)
                .setContentTitle(Value.IS.emptyValue(smartPlug.getName()) ? "Smart-plug" : smartPlug.getName())
                .setOngoing(false)
                .setLights(getResources().getColor(R.color.flat_alizarin), 1000, 5000)
                .setContentText("Your " +
                        (Value.IS.nullValue(smartPlug.getName()) ? "Smart-plug " : "("+smartPlug.getName()+") couldn't be switched on automatically." ));
        Intent resultIntent = new Intent(this, Activity.class);
        resultIntent.putExtra("smartPlug", smartPlug.getId());
        resultIntent.putExtra("event", event.getId());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Constants.NOTIFICATION_ID, builder.build());
    }

    private void notifyFailOff(SmartPlug smartPlug, Event event){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_tina3)
                .setContentTitle(Value.IS.emptyValue(smartPlug.getName()) ? "Smart-plug" : smartPlug.getName())
                .setOngoing(false)
                .setLights(getResources().getColor(R.color.flat_alizarin), 1000, 5000)
                .setContentText("Your " +
                        (Value.IS.nullValue(smartPlug.getName()) ? "Smart-plug " : "("+smartPlug.getName()+") couldn't be switched off automatically." ));
        Intent resultIntent = new Intent(this, Activity.class);
        resultIntent.putExtra("smartPlug", smartPlug.getId());
        resultIntent.putExtra("event", event.getId());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Constants.NOTIFICATION_ID, builder.build());
    }

    private void endEvent(SmartPlug smartPlug, Event event){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String what;
        switch (Event.Status.values()[event.getStatus()]){
            case ONGOING:
                what = "on";
                break;
            case DONE:
                what  = "off";
                break;
            default:
                return;
        }
        builder.setSmallIcon(R.drawable.ic_stat_tina3)
                .setContentTitle(Value.IS.emptyValue(smartPlug.getName()) ? "Smart-plug" : smartPlug.getName())
                .setOngoing(false)
                .setLights(getResources().getColor(R.color.tina_green), 1000, 5000)
                .setAutoCancel(true)
                .setContentText("Your " +
                        (Value.IS.nullValue(smartPlug.getName()) ? "Smart-plug " : "("+smartPlug.getName()+") has been switched "+what+"." ));
        Intent resultIntent = new Intent(this, Activity.class);
        resultIntent.putExtra("smartPlug", smartPlug.getId());
        resultIntent.putExtra("event", event.getId());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Constants.NOTIFICATION_ID, builder.build());
    }

    private void notifySoon(SmartPlug smartPlug, Event event, DateTime now){
        if(!Value.IS.ANY.nullValue(smartPlug, event)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            int mins;
            String toWhat;
            switch (Event.Status.values()[event.getStatus()]) {
                case SCHEDULED:
                    mins = event.getStart() - now.getMinuteOfDay();
                    toWhat = "on";
                    break;
                case ONGOING:
                    mins = event.getEnd() - now.getMinuteOfDay();
                    toWhat = "off";
                    break;
                default:
                    setAlarm(this);
                    return;
            }
            builder.setSmallIcon(R.drawable.ic_stat_tina3)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setLights(getResources().getColor(R.color.flat_belize_hole), 1000, 5000)
                    .setContentTitle(Value.IS.emptyValue(smartPlug.getName()) ? "Smart-plug" : smartPlug.getName())
                    .setContentText("Your "+ (Value.IS.nullValue(smartPlug.getName()) ? "Smart-plug " : "("+smartPlug.getName()+") " )
                            + "would be switched "+toWhat+" " + (mins < 1 ? "soon." : mins > 1 ? ""+mins+" minutes." : "a minute."));
            Intent resultIntent = new Intent(this, Activity.class);
            resultIntent.putExtra("smartPlug", smartPlug.getId());
            resultIntent.putExtra("event", event.getId());
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(Constants.NOTIFICATION_ID, builder.build());
        }
        setAlarm(this);
    }

    public static void cancelAlarm(Context context){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ActionReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    public static void setAlarm(Context context) {
        User user = null;
        try {
            user = Authenticator.getInstance().getUser(false);
        } catch (Exception e) {
            return;
        }
        Integer userId = user.getId();
        EventTable eventTable = new EventTable();
        Event event = eventTable.getTopSignificantEvent(userId);
        if(Value.IS.nullValue(event))
            return;
        DateTime now = DateTime.now(DateTimeZone.UTC);
        int eventOccurrenctTime;
        switch (Event.Status.values()[event.getStatus()]){
            case SCHEDULED:
                eventOccurrenctTime = event.getStart();
                break;
            case ONGOING:
                eventOccurrenctTime = event.getEnd();
                break;
            default:
                return;
        }
        int minuteOfHour = eventOccurrenctTime % 60;
        int hourOfDay = (eventOccurrenctTime - minuteOfHour) / 60;
        DateTime reminderTime = now.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).minusMinutes(2);

        if(now.getMinuteOfDay() == eventOccurrenctTime){
            //holy shit, it's already time for this event? Run it quickly!
            context.startService(new Intent(context, ActionService.class));
            return;
        }else if(now.getMinuteOfDay() > eventOccurrenctTime){
            //holy double shit, this event passed already. How is this possible?
            //yeah I'mma just ignore this event and fail it, then recall this to set to next event
            try {
                eventTable.updateEvent(event.setStatus(Event.Status.FAILED.ordinal()), false, true);
            } catch (ConflictException e) {
                //won't be called
                e.printStackTrace();
            }
            setAlarm(context);
            return;
        }else{
            //we're fine. schedule an alarm here;
            if(reminderTime.getMinuteOfDay() < now.getMinuteOfDay()){
                //problem here again. we should just set the alarm to the time this event would start,
                reminderTime = now.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour);
            }
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ActionReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, reminderTime.getMillis(), alarmIntent);
        }
    }

    public static class ActionReceiver extends WakefulBroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, ActionService.class));
        }

    }


}
