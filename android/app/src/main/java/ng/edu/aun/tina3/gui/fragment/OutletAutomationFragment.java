package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.litigy.lib.android.gui.view.textView.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.util.Time;

/**
 * Created by joeyblack on 11/24/16.
 */

public class OutletAutomationFragment extends BroadcastFragtivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView dateTextView;
    private FloatingActionButton addButton;
    private SwipeRefreshLayout refreshLayout;
    private ImageView cover;

    public static OutletAutomationFragment getInstance(){
        return new OutletAutomationFragment();
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
        return R.layout.fragment_outlet_automation;
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
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        cover = (ImageView) findViewById(R.id.cover);
    }

    @Override
    public void setupViews() {
        refreshLayout.setColorSchemeResources(R.color.tina_green);
        refreshLayout.setOnRefreshListener(this);
        refreshDate();
    }

    private void refreshDate(){
        StringBuilder builder = new StringBuilder();
        DateTime dateTime = DateTime.now(DateTimeZone.getDefault());
        String day = dateTime.dayOfWeek().getAsText();
        builder.append(day);
        builder.append(" ");
        builder.append(dateTime.getDayOfMonth());
        builder.append(", ");
        builder.append(dateTime.getYear());
        builder.append(".");
        dateTextView.setText(builder.toString());
        setCoverImage(R.drawable.stub);
    }

    private void setCoverImage(int res){
        Glide.with(this).load(res).centerCrop().into(cover);
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
    public void onRefresh() {

    }
}
