package ng.edu.aun.tina3.gui.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;


import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/24/16.
 */

public class LightSwitch extends LightSwitchStub implements View.OnTouchListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener, CompoundButton.OnCheckedChangeListener {


    private String name, switchId;
;
    private View switchUI, settingsUI, deleteButton;
    private LightSwitchListener lightSwitchListener;
    private UIState uiStateState = UIState.SWITCH;
    private SwitchCompat automatedSwitch;

    public LightSwitch(Context context) {
        super(context);
    }

    public LightSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LightSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init(AttributeSet attrs){
        super.init(attrs);
        findViewById(R.id.selector).setOnTouchListener(this);
        switchUI = findViewById(R.id.switchUI);
        settingsUI = findViewById(R.id.settingsUI);
        automatedSwitch = (SwitchCompat) findViewById(R.id.automatedSwitch);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Value.IS.nullValue(lightSwitchListener))
                    lightSwitchListener.onDeleteRequested();
            }
        });
        OnClickListener flipListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        };
        automatedSwitch.setOnCheckedChangeListener(this);
        findViewById(R.id.backButton).setOnClickListener(flipListener);
        findViewById(R.id.settingsButton).setOnClickListener(flipListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                switch (getStatus()){
                    case OFF:
                        setSwitchingOn();
                        break;
                    case ON:
                        setSwitchingOff();
                        break;
                }
                break;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public LightSwitchStub setName(String name) {
        this.name = name;
        if(!Value.IS.nullValue(nameTextView))
            nameTextView.setText(name);
        return this;
    }


    public String getSwitchId() {
        return switchId;
    }

    public LightSwitchStub setSwitchId(String id) {
        this.switchId = id;
        if(!Value.IS.nullValue(idTextView)){
            idTextView.setText(id);
        }
        return this;
    }

    public void flip(){
        if(uiStateState.equals(UIState.FLIPPING_TO_SETTINGS) || uiStateState.equals(UIState.FLIPPING_TO_SWITCH))
            return;
        uiStateState = uiStateState.equals(UIState.SWITCH) ? UIState.FLIPPING_TO_SETTINGS : UIState.FLIPPING_TO_SWITCH;
        ValueAnimator flipAnimator = ValueAnimator.ofFloat(0f, 1f);
        flipAnimator.setDuration(1000);
        flipAnimator.addUpdateListener(this);
        flipAnimator.addListener(this);
        flipAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        final float value = animation.getAnimatedFraction();
        if(value <= 0.5f){
            float scaleValue = 1 * (1f - value);
            setRotationY(180 * value);
            setScaleX(scaleValue);
            setScaleY(scaleValue);
            //setCameraDistance((getScaleBounds().bottom - getScaleBounds().top) * scaleValue);
        } else {
            if(uiStateState.equals(UIState.FLIPPING_TO_SETTINGS) && settingsUI.getVisibility()!=VISIBLE){
                switchUI.setVisibility(View.INVISIBLE);
                settingsUI.setVisibility(View.VISIBLE);
            }else if(uiStateState.equals(UIState.FLIPPING_TO_SWITCH) && switchUI.getVisibility()!=VISIBLE){
                switchUI.setVisibility(View.VISIBLE);
                settingsUI.setVisibility(View.INVISIBLE);
            }
            float scaleValue = 1 * (value);
            setRotationY(-180 * (1f- value));
            setScaleX(scaleValue);
            setScaleY(scaleValue);
            //Log.d("Flipped");
            //setCameraDistance((getScaleBounds().bottom - getScaleBounds().top) * scaleValue);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        setDrawingCacheEnabled(true);
        setChildrenDrawingCacheEnabled(true);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        setUiStateState(switchUI.getVisibility()==View.VISIBLE ? UIState.SWITCH : UIState.SETTINGS);
        setDrawingCacheEnabled(false);
        setChildrenDrawingCacheEnabled(false);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public LightSwitchListener getLightSwitchListener() {
        return lightSwitchListener;
    }

    public LightSwitchStub setLightSwitchListener(LightSwitchListener lightSwitchListener) {
        this.lightSwitchListener = lightSwitchListener;
        return this;
    }

    public interface LightSwitchListener {
        public void onDeleteRequested();
        public void onUIStateChange(UIState uiState);
        public void onStateChange(Status status);
        public void onAutomationChanged(boolean enabled);
    }

    public UIState getUiStateState() {
        return uiStateState;
    }

    public LightSwitchStub setUiStateState(UIState uiStateState) {
        this.uiStateState = uiStateState;
        if(!Value.IS.nullValue(lightSwitchListener))
            lightSwitchListener.onUIStateChange(uiStateState);
        return this;
    }


    @Override
    public LightSwitchStub setAutomated(boolean automated) {
        super.setAutomated(automated);
        automatedSwitch.setOnCheckedChangeListener(null);
        automatedSwitch.setChecked(automated);
        automatedSwitch.setOnCheckedChangeListener(this);
        return this;
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!Value.IS.nullValue(lightSwitchListener))
            lightSwitchListener.onAutomationChanged(isChecked);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        if(!Value.IS.nullValue(lightSwitchListener))
            lightSwitchListener.onStateChange(status);
    }


}
