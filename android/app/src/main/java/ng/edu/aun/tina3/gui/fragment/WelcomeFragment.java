package ng.edu.aun.tina3.gui.fragment;

import android.os.Bundle;
import android.view.View;

import com.litigy.lib.android.gui.fragment.Fragtivity;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.gui.activity.Activity;

/**
 * Created by joeyblack on 11/19/16.
 */

public class WelcomeFragment extends Fragtivity {

    public static WelcomeFragment getInstance(){
        return new WelcomeFragment();
    }

    private View signupButton, loginButton;

    @Override
    public int layoutId() {
        return R.layout.fragment_welcome;
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
        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);
    }

    @Override
    public void setupViews() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getActivity()).addFragment(LoginFragment.getInstance());
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getActivity()).addFragment(SignupFragment.getInstance());
            }
        });
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
}
