package ng.edu.aun.tina3.gui.misc;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.android.gui.util.FontUtils;

/**
 * Created by Joey Dalughut on 8/13/16 at 12:29 PM,
 * Project: Skout.
 * Copyright (c) 2016 LITIGY. All rights reserved.
 * http://www.litigy.com
 */
public class Snackbar {

    public static void showLong(View parent, String message){
        setup(android.support.design.widget.Snackbar.make(parent, message, android.support.design.widget.Snackbar.LENGTH_LONG)).show();
    }

    public static void showLong(View parent, String message, String action, View.OnClickListener onClickListener, Integer textColor){
        setup(android.support.design.widget.Snackbar.make(parent, message, android.support.design.widget.Snackbar.LENGTH_LONG)).setAction(action, onClickListener).setActionTextColor(textColor).show();
    }

    public static void showLong(View parent, int message){
        showLong(parent, parent.getResources().getString(message));
    }

    public static void showLong(Fragtivity fragtivity, int message){
        showLong(fragtivity.getRootView(), message);
    }

    public static void showLong(Fragtivity fragtivity, String message){
        showLong(fragtivity.getRootView(), message);
    }

    public static void showShort(View parent, int message){
        showShort(parent, parent.getResources().getString(message));
    }

    public static void showShort(View parent, String message){
        setup(android.support.design.widget.Snackbar.make(parent, message, android.support.design.widget.Snackbar.LENGTH_SHORT)).show();
    }


    private static android.support.design.widget.Snackbar setup(android.support.design.widget.Snackbar snackbar){
        TextView messageView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        messageView.setGravity(Gravity.CENTER);
        messageView.setTextColor(0xFFFFFFFF);
        FontUtils.applyFontAppearance(FontUtils.FontSize.body2, messageView);
        snackbar.getView().setBackgroundColor(Color.BLACK);
        return snackbar;
    }

}