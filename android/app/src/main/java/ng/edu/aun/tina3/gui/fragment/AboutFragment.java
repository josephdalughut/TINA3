package ng.edu.aun.tina3.gui.fragment;

import android.os.Bundle;
import android.view.View;

import com.litigy.lib.android.gui.fragment.Fragtivity;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/22/16.
 */

public class AboutFragment extends Fragtivity {

    public static AboutFragment getInstance(){
        return new AboutFragment();
    }

    private View closeButton;


    @Override
    public int layoutId() {
        return R.layout.fragment_about;
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
        closeButton = findViewById(R.id.closeButton);
    }

    @Override
    public void setupViews() {
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
