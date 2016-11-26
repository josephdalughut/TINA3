package ng.edu.aun.tina3.gui.fragment;

import android.os.Bundle;

import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.android.gui.view.editText.EditText;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/25/16.
 */

public class OutletAddNameFragment extends Fragtivity {

    public static OutletAddNameFragment getInstance(){
        return new OutletAddNameFragment();
    }

    public EditText name;

    @Override
    public int layoutId() {
        return R.layout.fragment_outlet_add_name;
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
        name = (EditText) findViewById(R.id.name);
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
