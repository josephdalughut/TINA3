package ng.edu.aun.tina3.gui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.litigy.lib.android.gui.view.progress.PigressBar;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/24/16.
 */

public class LightSwitchStub extends FrameLayout {

    public View shadowTop, shadowBottom, buttonIsOn, buttonIsOff, middleGradient;
    public PigressBar statePigressBar, automationPigressBar;
    public TextView stateTextView, idTextView, nameTextView;
    public boolean automated = false, showSettings = false, showDelete = false;
    public Status status = Status.OFF;

    public LightSwitchStub(Context context) {
        super(context);
        init(null);
    }

    public LightSwitchStub(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LightSwitchStub(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LightSwitchStub(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * 1.5f);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        setMeasuredDimension(width, height);
    }

    @CallSuper
    public void init(AttributeSet attrs){
        inflate(getContext(), R.layout.layout_light_switch, this);
        shadowTop = findViewById(R.id.shadowTop);
        shadowBottom = findViewById(R.id.shadowBottom);
        buttonIsOn = findViewById(R.id.buttonIsOn);
        buttonIsOff = findViewById(R.id.buttonIsOff);
        statePigressBar = (PigressBar) findViewById(R.id.statePigressBar);
        automationPigressBar = (PigressBar) findViewById(R.id.automationPigressBar);
        stateTextView  = (TextView) findViewById(R.id.stateTextView);
        middleGradient = findViewById(R.id.middleGradient);
        showSettings(false);
        showDelete(false);
        setupAttrs(attrs);
    }

    public boolean isAutomated() {
        return automated;
    }

    public LightSwitchStub setAutomated(boolean automated) {
        this.automated = automated;
        automationPigressBar.setProgress(100);
        automationPigressBar.setMax(100);
        automationPigressBar.setIndeterminate(false);
        automationPigressBar.setColor(isAutomated() ? getResources().getColor(R.color.flat_belize_hole) : getResources().getColor(R.color.ccc));
        return this;
    }


    private void setupAttrs(AttributeSet attributeSet){
        if(Value.IS.nullValue(attributeSet))
            return;
        TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.LightSwitch);
        switch (Status.values()[array.getInt(R.styleable.LightSwitch_lightSwitchState, 0)]){
            case ON:
                switchOn();
                break;
            case OFF:
                switchOff();
                break;
        }
    }


    public static enum Status {
        OFF, ON, SWITCHING_OFF, SWITCHING_ON
    }

    public static enum UIState {
        SWITCH, FLIPPING_TO_SETTINGS, FLIPPING_TO_SWITCH, SETTINGS
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public void showSettings(boolean showSettings){
        this.showSettings = showSettings;
        try{
            findViewById(R.id.settingsButton).setVisibility(showSettings ? View.VISIBLE : View.GONE);
        }catch (Exception ignored){

        }
    }

    public void showDelete(boolean showDelete){
        this.showDelete = showDelete;
        try{
            findViewById(R.id.deleteButton).setVisibility(showDelete ? View.VISIBLE : View.GONE);
        }catch (Exception ignored){

        }
    }


    public void switchOff(){
        off();
        setStatus(Status.OFF);
    }

    private void off(){
        setShadowHeight(shadowBottom, getResources().getDimensionPixelSize(R.dimen.light_switch_shadow_height));
        setShadowHeight(shadowTop, 0);
        buttonIsOn.setBackgroundColor(getResources().getColor(R.color.light_switch_off_shade));
        buttonIsOff.setBackgroundColor(getResources().getColor(R.color.light_switch_on_shade));
        statePigressBar.setColor(getResources().getColor(R.color.ccc));
        middleGradient.setBackgroundResource(R.drawable.light_switch_mid_gradient_off);
        statePigressBar.setIndeterminate(false);
        statePigressBar.setProgress(100);
        //stateTextView.setText(getResources().getString(R.string.off));
    }

    public void setSwitchingOff(){
        setShadowHeight(shadowBottom, getResources().getDimensionPixelSize(R.dimen.light_switch_shadow_height));
        setShadowHeight(shadowTop, 0);
        buttonIsOn.setBackgroundColor(getResources().getColor(R.color.light_switch_off_shade));
        buttonIsOff.setBackgroundColor(getResources().getColor(R.color.light_switch_on_shade));
        middleGradient.setBackgroundResource(R.drawable.light_switch_mid_gradient_off);
        statePigressBar.setColor(getResources().getColor(R.color.ccc));
        statePigressBar.setIndeterminate(true);
        //stateTextView.setText(getResources().getString(R.string.off));
        setStatus(Status.SWITCHING_OFF);
    }

    private void on(){
        setShadowHeight(shadowTop, getResources().getDimensionPixelSize(R.dimen.light_switch_shadow_height));
        setShadowHeight(shadowBottom, 0);
        buttonIsOff.setBackgroundColor(getResources().getColor(R.color.light_switch_off_shade));
        buttonIsOn.setBackgroundColor(getResources().getColor(R.color.light_switch_on_shade));
        middleGradient.setBackgroundResource(R.drawable.light_switch_mid_gradient_on);
        statePigressBar.setColor(getResources().getColor(R.color.tina_green));
        statePigressBar.setIndeterminate(false);
        statePigressBar.setProgress(100);
        //stateTextView.setText(getResources().getString(R.string.on));
    }

    public void switchOn(){
        on();
        setStatus(Status.ON);
    }

    public void setSwitchingOn(){
        setShadowHeight(shadowTop, getResources().getDimensionPixelSize(R.dimen.light_switch_shadow_height));
        setShadowHeight(shadowBottom, 0);
        buttonIsOff.setBackgroundColor(getResources().getColor(R.color.light_switch_off_shade));
        buttonIsOn.setBackgroundColor(getResources().getColor(R.color.light_switch_on_shade));
        middleGradient.setBackgroundResource(R.drawable.light_switch_mid_gradient_on);
        statePigressBar.setColor(getResources().getColor(R.color.tina_green));
        statePigressBar.setIndeterminate(true);
        //stateTextView.setText(getResources().getString(R.string.on));
        setStatus(Status.SWITCHING_ON);
    }

    public void failSwitchOn(){
        status = Status.OFF;
        off();
    }

    public void failSwitchOff(){
        status = Status.ON;
        on();
    }


    public void setShadowHeight(View shadowView, int height){
        LayoutParams layoutParams = (LayoutParams) shadowView.getLayoutParams();
        layoutParams.height = height;
        shadowView.setLayoutParams(layoutParams);
    }

    public Status getStatus() {
        return status;
    }



}
