package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.litigy.lib.android.gui.fragment.Fragtivity;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.gui.activity.Activity;
import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.util.PermissionUtils;
import ng.edu.aun.tina3.util.Value;

/**
 * Created by joeyblack on 11/19/16.
 */

public class SplashFragment extends BroadcastFragtivity {

    public static SplashFragment getInstance(){
        return new SplashFragment();
    }
    private AsyncTask task;

    @Override
    public int layoutId() {
        return R.layout.fragment_splash;
    }

    @Override
    public void destroy() {
        if(!Value.IS.nullValue(task)&& !task.isCancelled()){
            task.cancel(true);
        }
        task = null;
    }

    @Override
    public void destroyView() {

    }

    @Override
    public void bundle(Bundle bundle) {

    }

    @Override
    public void findViews() {

    }

    @Override
    public void setupViews() {
        if(PermissionUtils.Accounts.isPermissionRequired(getContext())){
            PermissionUtils.Accounts.requestPermissions(getActivity());
            return;
        }
        loadUser();
    }

    private void loadUser(){
        task = new AsyncTask<Void, Void, Object>(){
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    return Authenticator.getInstance().getUser(true);
                } catch (Exception e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if(o instanceof User){
                    ((Activity)getActivity()).replaceFragment(OutletListFragment.getInstance());
                    return;
                }
                ((Activity)getActivity()).replaceFragment(WelcomeFragment.getInstance());
            }
        }.execute();
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
    public String[] getIntentActions() {
        return new String[]{PermissionUtils.Accounts.INTENT};
    }

    @Override
    public void onIntent(Intent intent) {
        try{
            setupViews();
        }catch (Exception ignored){

        }
    }
}
