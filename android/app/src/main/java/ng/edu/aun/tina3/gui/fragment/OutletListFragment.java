package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.litigy.lib.android.gui.fragment.Fragtivity;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/19/16.
 */

public class OutletListFragment extends BroadcastFragtivity {


    public static OutletListFragment getInstance(){
        return new OutletListFragment();
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
        return R.layout.fragment_outlet_list;
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

    }

    @Override
    public void setupViews() {

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
