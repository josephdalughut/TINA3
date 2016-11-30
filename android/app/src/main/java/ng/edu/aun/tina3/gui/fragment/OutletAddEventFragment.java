package ng.edu.aun.tina3.gui.fragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TimePicker;

import com.litigy.lib.android.gui.dialog.ProgressDialog;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.security.Crypto;
import com.litigy.lib.java.util.Value;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.data.EventTable;
import ng.edu.aun.tina3.error.ConflictException;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.service.ActionService;

/**
 * Created by joeyblack on 11/28/16.
 */

public class OutletAddEventFragment extends BroadcastFragtivity implements TimePickerDialog.OnTimeSetListener {


    public static OutletAddEventFragment getInstance(SmartPlug smartPlug){
        return new OutletAddEventFragment().setSmartPlug(smartPlug);
    }

    private SmartPlug smartPlug;

    private View closeButton;
    private FloatingActionButton saveButton;
    private TextView smartPlugName;
    private TextView onTime;
    private TextView offTime;
    private TextView durationHint;

    private int on;
    private int off;
    private boolean settingOn = false;

    private ProgressDialog dialog;

    public SmartPlug getSmartPlug() {
        return smartPlug;
    }

    public OutletAddEventFragment setSmartPlug(SmartPlug smartPlug) {
        this.smartPlug = smartPlug;
        return this;
    }

    @Override
    public String[] getIntentActions() {
        return new String[0];
    }

    @Override
    public void onIntent(Intent intent) {

    }

    @Override
    public int layoutId() {
        return R.layout.fragment_outlet_add_event;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void destroyView() {

    }

    @Override
    public void bundle(Bundle bundle) {

    }

    @Override
    public void findViews() {
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        closeButton = findViewById(R.id.closeButton);
        smartPlugName = (TextView) findViewById(R.id.smartPlugName);
        onTime = (TextView) findViewById(R.id.onTime);
        offTime = (TextView) findViewById(R.id.offTime);
        durationHint = (TextView) findViewById(R.id.durationHint);
    }

    @Override
    public void setupViews() {
        if(!Value.IS.emptyValue(smartPlug.getName())){
            smartPlugName.setText(smartPlug.getName());
        }
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (Exception e) {
                    getActivity().onBackPressed();
                }
            }
        });
        onTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingOn = true;
                pickTime(on);
            }
        });
        offTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingOn = false;
                pickTime(off);
            }
        });
        try {
            initialize();
        } catch (ConflictException e) {
            Snackbar.showLong(this, R.string.error_almost_event_epoch);
        }
    }

    private void pickTime(int minute){
        int minuteOfHour = minute % 60;
        int hourOfDay = (minute - minuteOfHour) / 60;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), this, hourOfDay, minuteOfHour, false);
        timePickerDialog.show();
    }

    private void initialize() throws ConflictException {
        DateTime now = DateTime.now(DateTimeZone.getDefault());
        int currentMinute = now.getMinuteOfDay();
        if(currentMinute>1435){
            throw new ConflictException("");
        }
        int rem = 1440 - currentMinute;
        boolean has30Mins = rem > 30;
        if(has30Mins){
            on = currentMinute + 10;
            off = on + 10;
        }else{
            on = currentMinute + 2;
            off = on + 1;
        }
        refreshTime();
    }

    private void refreshTime(){
        int onMinHour = on % 60;
        int onHour = (on - onMinHour) / 60;
        int offMinHour = off % 60;
        int offHour = (off - offMinHour) / 60;
        String onHourText = onHour <= 12 ? Value.TO.stringValue(onHour) : Value.TO.stringValue(onHour - 12);
        String onMinText = onMinHour > 9 ? Value.TO.stringValue(onMinHour) : "0"+Value.TO.stringValue(onMinHour);
        String onMer = onHour < 12 ? "AM" : "PM";

        String offHourText = offHour <= 12 ? Value.TO.stringValue(offHour) : Value.TO.stringValue(offHour - 12);
        String offMinText = offMinHour > 9 ? Value.TO.stringValue(offMinHour) : "0"+Value.TO.stringValue(offMinHour);
        String offMer = offHour < 12 ? "AM" : "PM";

        onTime.setText(onHourText + ":" + onMinText + " "+ onMer);
        offTime.setText(offHourText + ":" + offMinText + " "+ offMer);

        int duration = off - on;
        if(duration < 60) {
            durationHint.setText(duration + (duration == 1 ? " minute." : " minutes."));
        }else if (duration == 60){
            durationHint.setText("1 hour");
        }else{
            int mins = duration % 60;
            int hour = (duration - mins) / 60;
            durationHint.setText(hour + (hour == 1 ? " hour, " : " hours, ") + mins + (mins == 1 ? " minute." : " minutes."));
        }
    }

    private void padSetTime(int newTime){
        if(settingOn){
            if(off <= newTime){
                int lastDifference = off - on;
                off = newTime + lastDifference;
            }
            on = newTime;
        }else{
            if(on >= newTime){
                int lastDifference = off - on;
                on = newTime - lastDifference;
            }
            off = newTime;
        }
        refreshTime();
    }

    private void save() throws Exception {
        DateTime now = new DateTime(DateTimeZone.getDefault());
        int currentMinute = now.getMinuteOfDay();
        if(on < currentMinute){
            Snackbar.showLong(this, R.string.error_event_past);
            return;
        }
        String date = Value.TO.stringValue(now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth());
        Event event = new Event()
                .setId(Crypto.Random.uuidClear().replaceAll("-", ""))
                .setStart(on)
                .setEnd(off)
                .setUserId(Authenticator.getInstance().getUser(false).getId())
                .setStatus(Event.Status.SCHEDULED.ordinal())
                .setSmartPlugId(smartPlug.getId())
                .setPredicted(0)
                .setDate(date);
        closeProgressDialog();
        dialog = ProgressDialog.getInstance(getString(R.string.please_wait), getString(R.string.addding_event), getColor(R.color.tina_green), false);
        dialog.show(getChildFragmentManager(), null);
        new EventTable().addEventAsync(event, true, new DoubleReceiver<Event, LitigyException>() {
            @Override
            public void onReceive(Event event, LitigyException e) {

            }

            @Override
            public void onReceive1(Event event) {
                closeProgressDialog();
                ActionService.setAlarm(getContext().getApplicationContext());
                getActivity().onBackPressed();
            }

            @Override
            public void onReceive2(LitigyException e) {
                closeProgressDialog();
                Snackbar.showLong(OutletAddEventFragment.this, R.string.error_conflicting_events);
            }
        });
    }

    private void closeProgressDialog(){
        if(!Value.IS.nullValue(dialog) && dialog.isVisible())
            dialog.dismissAllowingStateLoss();
        dialog = null;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onKeyboardShown(int i) {

    }

    @Override
    public void onKeyboardHidden() {

    }

    @Override
    public boolean shouldWatchKeyboard() {
        return false;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int minuteOfDay = (hourOfDay * 60) + minute;
        padSetTime(minuteOfDay);
    }
}
