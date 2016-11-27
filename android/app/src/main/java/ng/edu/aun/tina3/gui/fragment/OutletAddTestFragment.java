package ng.edu.aun.tina3.gui.fragment;

import android.os.Bundle;

import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import org.w3c.dom.Text;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.gui.custom.LightSwitch;
import ng.edu.aun.tina3.gui.custom.LightSwitchStub;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/25/16.
 */

public class OutletAddTestFragment extends Fragtivity implements LightSwitch.LightSwitchListener{

    private LearningStage learningStage = LearningStage.LEARN_SWITCH_ON;
    private SmartPlug smartPlug;

    private TextView infoTextView;
    private LightSwitch lightSwitch;

    public static OutletAddTestFragment getInstance(){
        return new OutletAddTestFragment();
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_outlet_add_test;
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
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        lightSwitch = (LightSwitch) findViewById(R.id.lightSwitch);
    }

    @Override
    public void setupViews() {
        lightSwitch.setLightSwitchListener(this);
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
        switch (uiState){
            case SETTINGS:
                if(learningStage.equals(LearningStage.LEARN_SETTINGS))
                    setLearningStage(LearningStage.LEARN_AUTOMATION);
                break;
            case SWITCH:
                if(learningStage.equals(LearningStage.LEARN_AUTOMATION))
                    setLearningStage(LearningStage.LEARNED);
                break;
        }
    }

    @Override
    public void onStateChange(LightSwitchStub.Status status) {
        switch (status){
            case SWITCHING_ON:
                Log.d("Switching on smart plug");
                switchOnSmartPlug();
                break;
            case SWITCHING_OFF:
                Log.d("Switching off smart plug");
                switchOffSmartPlug();
                break;
            case ON:
                if(learningStage.equals(LearningStage.LEARN_SWITCH_ON)){
                    setLearningStage(LearningStage.LEARN_SWITCH_OFF);
                }
                break;
            case OFF:
                if(learningStage.equals(LearningStage.LEARN_SWITCH_OFF)){
                    setLearningStage(LearningStage.LEARN_SETTINGS);
                }
                break;
        }
    }

    @Override
    public void onAutomationChanged(boolean enabled) {
        smartPlug.setAutomated(enabled ? 1 : 0);
        lightSwitch.flip();
    }

    public SmartPlug getSmartPlug() {
        return smartPlug;
    }

    public OutletAddTestFragment setSmartPlug(SmartPlug smartPlug) {
        this.smartPlug = smartPlug;
        setLearningStage(LearningStage.LEARN_SWITCH_ON);
        try{
            refreshSmartPlug();
        }catch (Exception ignored){
            ignored.printStackTrace();
        }
        return this;
    }

    private void refreshSmartPlug(){
        lightSwitch.setName(smartPlug.getName());
        lightSwitch.setSwitchId(smartPlug.getId());
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
                Snackbar.showLong(OutletAddTestFragment.this, R.string.smart_plug_error_switch_on);
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
                Snackbar.showLong(OutletAddTestFragment.this, R.string.smart_plug_error_switch_off);
            }
        });
    }

    private void setLearningStage(LearningStage learningStage){
        this.learningStage = learningStage;
        switch (learningStage){
            case LEARN_SWITCH_ON:
                infoTextView.setText(getString(R.string.smart_plug_add_learn_switch_on));
                break;
            case LEARN_SWITCH_OFF:
                infoTextView.setText(getString(R.string.smart_plug_add_learn_switch_off));
                break;
            case LEARN_SETTINGS:
                lightSwitch.showSettings(true);
                infoTextView.setText(getString(R.string.smart_plug_add_learn_switch_settings));
                break;
            case LEARN_AUTOMATION:
                infoTextView.setText(getString(R.string.smart_plug_add_learn_switch_automation));
                break;
            case LEARNED:
                infoTextView.setText(getString(R.string.smart_plug_add_learn_switch_learned));
                break;
        }
    }

    enum LearningStage {
        LEARN_SWITCH_ON, LEARN_SWITCH_OFF, LEARN_SETTINGS, LEARN_AUTOMATION, LEARNED
    }

}
