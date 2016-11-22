package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.generic.Receiver;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.rest.model.User;

/**
 * Created by joeyblack on 11/22/16.
 */

public class OutletListNavigationFragment extends BroadcastFragtivity  {

    public static OutletListNavigationFragment getInstance(Receiver<Integer> optionSelectListener){
        return new OutletListNavigationFragment().setOptionSelectListener(optionSelectListener);
    }

    private Receiver<Integer> optionSelectListener;

    public Receiver<Integer> getOptionSelectListener() {
        return optionSelectListener;
    }

    public OutletListNavigationFragment setOptionSelectListener(Receiver<Integer> optionSelectListener) {
        this.optionSelectListener = optionSelectListener;
        return this;
    }

    private View settingsButton, aboutButton, logoutButton;
    private TextView aunId;

    @Override
    public String[] getIntentActions() {
        return new String[0];
    }

    @Override
    public void onIntent(Intent intent) {

    }

    @Override
    public int layoutId() {
        return R.layout.fragment_outlet_list_navigation;
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
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        logoutButton = findViewById(R.id.logoutButton);
        aunId = (TextView) findViewById(R.id.aunId);
    }

    @Override
    public void setupViews() {
        onRefreshUserInfo();
    }

    private void onRefreshUserInfo(){
        try {
            User user = Authenticator.getInstance().getUser(false);
            aunId.setText(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
