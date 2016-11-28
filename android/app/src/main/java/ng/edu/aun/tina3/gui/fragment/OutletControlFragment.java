package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.security.Crypto;
import com.litigy.lib.java.util.Value;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.data.EventTable;
import ng.edu.aun.tina3.gui.custom.LightSwitch;
import ng.edu.aun.tina3.gui.custom.LightSwitchStub;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/24/16.
 */

public class OutletControlFragment extends BroadcastFragtivity implements LightSwitch.LightSwitchListener {

    public static OutletControlFragment getInstance(){
        return new OutletControlFragment();
    }

    private SmartPlug smartPlug;
    private LightSwitch lightSwitch;
    private Event event;

    public SmartPlug getSmartPlug() {
        return smartPlug;
    }

    public OutletControlFragment setSmartPlug(SmartPlug smartPlug) {
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
        return R.layout.fragment_outlet_control;
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
        lightSwitch = (LightSwitch) findViewById(R.id.lightSwitch);
    }

    @Override
    public void setupViews() {
        smartPlug = ((OutletActionsFragment)getParentFragment()).getSmartPlug();
        lightSwitch.setLightSwitchListener(this);
        lightSwitch.showSettings(true);
        refreshSmartPlug();
        refreshSmartPlugAutomation();
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
    public void onDeleteRequested() {

    }

    @Override
    public void onUIStateChange(LightSwitchStub.UIState uiState) {

    }

    @Override
    public void onStateChange(LightSwitchStub.Status status) {
        DateTime now = DateTime.now(DateTimeZone.getDefault());
        String date = Value.TO.stringValue(now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth());
        switch (status){
            case SWITCHING_OFF:
                event = new Event().setStart(now.getMinuteOfDay()).setDate(date);
                switchOffSmartPlug();
                break;
            case SWITCHING_ON:
                event = new Event().setStart(now.getMinuteOfDay()).setDate(date);
                switchOnSmartPlug();
                break;
            case ON:
                ((OutletActionsFragment)getParentFragment()).getSmartPlugTable().updateSmartPlugAsync(smartPlug.setState("ON"), true, null);
                openEvent();
                break;
            case OFF:
                ((OutletActionsFragment)getParentFragment()).getSmartPlugTable().updateSmartPlugAsync(smartPlug.setState("OFF"), true, null);
                closeEvent();
                break;
        }
    }

    private void openEvent(){
        if(smartPlug.getAutomated()==0)
            return;
        try {
            new EventTable().openEventAsync(new Event()
                    .setStart(this.event.getStart())
                    .setDate(this.event.getDate())
                    .setId(Crypto.Random.uuidClear().replaceAll("-", ""))
                    .setSmartPlugId(smartPlug.getId())
                    .setUserId(Authenticator.getInstance().getUser(false).getId())
                    .setStatus(Event.Status.BUILDING.ordinal()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeEvent(){
        if(smartPlug.getAutomated()==0)
            return;
        try {
            new EventTable().closeEventAsync( new Event()
                    .setEnd(this.event.getStart())
                    .setDate(this.event.getDate())
                    .setSmartPlugId(smartPlug.getId())
                    .setUserId(Authenticator.getInstance().getUser(false).getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshSmartPlug(){
        try {
            if (smartPlug.getState().equalsIgnoreCase("on")) {
                lightSwitch.switchOn();
            } else {
                lightSwitch.switchOff();
            }
            lightSwitch.stateTextView.setText(smartPlug.getName());
            lightSwitch.setName(smartPlug.getName());
            lightSwitch.setSwitchId(smartPlug.getId());
        }catch (Exception ignored){

        }
    }

    private void refreshSmartPlugAutomation(){
        lightSwitch.setAutomated(smartPlug.getAutomated() == 1);
    }

    private void switchOnSmartPlug(){
        SmartPlugApi.on(smartPlug.getId(), new DoubleReceiver<SmartPlug, LitigyException>() {
            @Override
            public void onReceive(SmartPlug smartPlug, LitigyException e) {

            }

            @Override
            public void onReceive1(SmartPlug smartPlug) {
                Log.d("Smart plug switched on");
                lightSwitch.switchOn();
            }

            @Override
            public void onReceive2(LitigyException e) {
                lightSwitch.failSwitchOn();
                Snackbar.showLong(OutletControlFragment.this, R.string.smart_plug_error_switch_on);
            }
        });
    }

    private void switchOffSmartPlug(){
        SmartPlugApi.off(smartPlug.getId(), new DoubleReceiver<SmartPlug, LitigyException>() {
            @Override
            public void onReceive(SmartPlug smartPlug, LitigyException e) {

            }

            @Override
            public void onReceive1(SmartPlug smartPlug) {
                Log.d("Smart plug switched off");
                lightSwitch.switchOff();
            }

            @Override
            public void onReceive2(LitigyException e) {
                lightSwitch.failSwitchOff();
                Snackbar.showLong(OutletControlFragment.this, R.string.smart_plug_error_switch_off);
            }
        });
    }
    @Override
    public void onAutomationChanged(boolean enabled) {
        ((OutletActionsFragment)getParentFragment()).getSmartPlugTable().updateSmartPlugAsync(smartPlug.setAutomated(enabled ? 1 : 0), true, null);
        refreshSmartPlugAutomation();
    }
}
