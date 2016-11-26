package ng.edu.aun.tina3.gui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.Application;

/**
 * Created by joeyblack on 11/19/16.
 */

public abstract class BroadcastFragtivity extends Fragtivity {

    public abstract String[] getIntentActions();
    public abstract void onIntent(Intent intent);

    private BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] intentActions = getIntentActions();
        if(!Value.IS.emptyValue(intentActions)){
            IntentFilter filter = new IntentFilter();
            for(String action: intentActions){
                filter.addAction(action);
            }
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onIntent(intent);
                }
            };
            Application.getInstance().registerReceiver(receiver, filter);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if(!Value.IS.nullValue(receiver)){
            try{
                Application.getInstance().unregisterReceiver(receiver);
            }catch (Exception ignored){

            }
        }
        super.onDestroy();
    }
}
