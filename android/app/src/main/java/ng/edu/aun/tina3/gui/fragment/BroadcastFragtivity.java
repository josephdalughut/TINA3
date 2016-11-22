package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;

import com.litigy.lib.android.gui.fragment.Fragtivity;

/**
 * Created by joeyblack on 11/19/16.
 */

public abstract class BroadcastFragtivity extends Fragtivity {

    public abstract String[] getIntentActions();
    public abstract void onIntent(Intent intent);

}
