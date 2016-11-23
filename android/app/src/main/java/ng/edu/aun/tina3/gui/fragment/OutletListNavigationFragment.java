package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.litigy.lib.android.gui.dialog.DialogFragtivity;
import com.litigy.lib.android.gui.dialog.InfoDialog;
import com.litigy.lib.android.gui.dialog.ProgressDialog;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.generic.Receiver;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.gui.activity.Activity;
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Value.IS.nullValue(optionSelectListener))
                    getOptionSelectListener().onReceive(1);
                ((Activity)getActivity()).addFragment(SettingsFragment.getInstance());
            }
        });
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Value.IS.nullValue(optionSelectListener))
                    getOptionSelectListener().onReceive(2);
                ((Activity)getActivity()).addFragment(AboutFragment.getInstance());
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Value.IS.nullValue(optionSelectListener))
                    getOptionSelectListener().onReceive(3);
                onLogoutRequest();
            }
        });
    }

    private void onRefreshUserInfo(){
        try {
            User user = Authenticator.getInstance().getUser(false);
            aunId.setText(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onLogoutRequest(){
        InfoDialog.getInstance(getString(R.string.logout), getString(R.string.query_logout), true)
                .withPositiveButton(getString(R.string.logout), new InfoDialog.OnClickListener() {
                    @Override
                    public void onClick(View view, DialogFragtivity dialogFragtivity) {
                        dialogFragtivity.dismissAllowingStateLoss();
                        onLogoutConfirm();
                    }
                }).withNegativeButton(getString(R.string.back), new InfoDialog.OnClickListener() {
            @Override
            public void onClick(View view, DialogFragtivity dialogFragtivity) {
                dialogFragtivity.dismissAllowingStateLoss();
            }
        }).show(getChildFragmentManager(), null);
    }

    ProgressDialog dialog;

    private void onLogoutConfirm(){
        dialog = ProgressDialog.getInstance(getString(R.string.please_wait),
                getString(R.string.logging_out), getColor(R.color.tina_green), false);
        dialog.show(getChildFragmentManager(), null);
        Authenticator.getInstance().logoutAsync(new Receiver<Void>() {
            @Override
            public void onReceive(Void aVoid) {
                onLogout();
            }
        });
    }

    private void onLogout(){
        if(!Value.IS.nullValue(dialog)&& dialog.isVisible()){
            dialog.dismissAllowingStateLoss();
            dialog = null;
        }
        ((Activity)getActivity()).replaceFragment(WelcomeFragment.getInstance());
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
