package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.gui.custom.LightSwitch;
import ng.edu.aun.tina3.gui.custom.LightSwitchStub;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
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
        switch (status){
            case SWITCHING_OFF:
                switchOffSmartPlug();
                break;
            case SWITCHING_ON:
                switchOnSmartPlug();
                break;
            case ON:
                ((OutletActionsFragment)getParentFragment()).getSmartPlugTable().updateSmartPlugAsync(smartPlug.setState("ON"), true, null);
                break;
            case OFF:
                ((OutletActionsFragment)getParentFragment()).getSmartPlugTable().updateSmartPlugAsync(smartPlug.setState("OFF"), true, null);
                break;
        }
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
    }
}
