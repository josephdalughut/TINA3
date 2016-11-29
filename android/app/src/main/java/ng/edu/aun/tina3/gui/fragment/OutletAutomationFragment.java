package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.litigy.lib.android.gui.adapter.GenericRecyclerViewCursorAdapter;
import com.litigy.lib.android.gui.view.progress.PigressBar;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.Consumer;
import com.litigy.lib.java.generic.DoubleConsumer;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.generic.QuatroReceiver;
import com.litigy.lib.java.generic.Receiver;
import com.litigy.lib.java.util.Value;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.data.EventTable;
import ng.edu.aun.tina3.data.Preferences;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.gui.activity.Activity;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.service.PredictionService;
import ng.edu.aun.tina3.util.Log;
import ng.edu.aun.tina3.util.Time;

/**
 * Created by joeyblack on 11/24/16.
 */

public class OutletAutomationFragment extends BroadcastFragtivity implements SwipeRefreshLayout.OnRefreshListener, DoubleReceiver<Cursor, LitigyException> {

    private TextView dateTextView, infoTextView;
    private FloatingActionButton addButton;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ImageView cover;
    GenericRecyclerViewCursorAdapter<EventHolder> adapter;
    private DateTime now;

    private View error, empty;

    public static OutletAutomationFragment getInstance(){
        return new OutletAutomationFragment();
    }

    @Override
    public String[] getIntentActions() {
        return new String[]{EventTable.Constants.UPDATE_INTENT};
    }

    @Override
    public void onIntent(Intent intent) {
        switch(intent.getAction()){
            case EventTable.Constants.UPDATE_INTENT:
                onRefresh(true);
                break;
            case PredictionService.Constants.INTENT_PREDICTION_STARTED:
                onPredictionStarted();
                break;
            case PredictionService.Constants.INTENT_PREDICTION_SUCCESS:
                onPredictionSuccess();
                break;
            case PredictionService.Constants.INTENT_PREDICTION_FAIL:
                onPredictionError(intent.getIntExtra("statusCode", LitigyException.Mappings.ServiceUnvailableException), intent.getStringExtra("message"));
                break;
        }
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
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        cover = (ImageView) findViewById(R.id.cover);
        error = findViewById(R.id.error);
        empty = findViewById(R.id.empty);
        recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    public void setupViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        final SmartPlug smartPlug = ((OutletActionsFragment)getParentFragment()).getSmartPlug();
        refreshLayout.setColorSchemeResources(R.color.tina_green);
        refreshLayout.setOnRefreshListener(this);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getActivity()).addFragment(OutletAddEventFragment.getInstance(((OutletActionsFragment)getParentFragment()).getSmartPlug()));
            }
        });
        adapter = GenericRecyclerViewCursorAdapter.<EventHolder>getInstance()
                .setIdConsumer(new DoubleConsumer<Long, Cursor, Integer>() {
                    @Override
                    public Long onConsume(Cursor cursor, Integer integer) {
                        return integer.longValue();
                    }

                    @Override
                    public Long onConsume1(Cursor cursor) {
                        return null;
                    }

                    @Override
                    public Long onConsume2(Integer integer) {
                        return integer.longValue();
                    }
                }).setViewTypeConsumer(new Consumer<Integer, Cursor>() {
                    @Override
                    public Integer onConsume(Cursor cursor) {
                        return 0;
                    }
                }).setViewConsumer(new DoubleConsumer<EventHolder, ViewGroup, Integer>() {
                    @Override
                    public EventHolder onConsume(ViewGroup viewGroup, Integer integer) {
                        return new EventHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false));
                    }

                    @Override
                    public EventHolder onConsume1(ViewGroup viewGroup) {
                        return null;
                    }

                    @Override
                    public EventHolder onConsume2(Integer integer) {
                        return null;
                    }
                }).setViewReceiver(new QuatroReceiver<EventHolder, Cursor, Integer, Boolean>() {
                    @Override
                    public void onReceive(EventHolder eventHolder, Cursor cursor, Integer integer, Boolean aBoolean) {
                        eventHolder.name.setText(smartPlug.getName());
                        int on = cursor.getInt(cursor.getColumnIndex(EventTable.Constants.Columns.START));
                        int off = cursor.getInt(cursor.getColumnIndex(EventTable.Constants.Columns.END));

                        boolean predicted = cursor.getInt(cursor.getColumnIndex(EventTable.Constants.Columns.PREDICTED)) == 1;

                        int onMinHour = on % 60;
                        int onHour = (on - onMinHour) / 60;
                        int offMinHour = off % 60;
                        int offHour = (off - offMinHour) / 60;
                        String onHourText = onHour < 12 ? Value.TO.stringValue(onHour) : Value.TO.stringValue(onHour - 12);
                        String onMinText = onMinHour > 9 ? Value.TO.stringValue(onMinHour) : "0"+Value.TO.stringValue(onMinHour);
                        String onMer = onHour < 12 ? "AM" : "PM";

                        String offHourText = offHour < 12 ? Value.TO.stringValue(offHour) : Value.TO.stringValue(offHour - 12);
                        String offMinText = offMinHour > 9 ? Value.TO.stringValue(offMinHour) : "0"+Value.TO.stringValue(offMinHour);
                        String offMer = offHour < 12 ? "AM" : "PM";

                        String when = "from "+ (onHourText + ":" + onMinText + " "+ onMer) + " to "+(offHourText + ":" + offMinText + " "+ offMer);
                        String durationText;
                        int duration = off - on;
                        if(duration < 60) {
                            durationText = (duration + (duration == 1 ? " minute." : " minutes."));
                        }else if (duration == 60){
                            durationText = ("1 hour");
                        }else{
                            int mins = duration % 60;
                            int hour = (duration - mins) / 60;
                            durationText = (hour + (hour == 1 ? " hour, " : " hours, ") + mins + (mins == 1 ? " minute." : " minutes."));
                        }
                        eventHolder.when.setText(when);
                        eventHolder.duration.setText(durationText);
                        eventHolder.status.setVisibility(View.VISIBLE);
                        int status = cursor.getInt(cursor.getColumnIndex(EventTable.Constants.Columns.STATUS));
                        switch (Event.Status.values()[status]){
                            case SCHEDULED:
                                eventHolder.status.setColor( predicted ? getColor(R.color.flat_belize_hole) : getColor(R.color.ccc));
                                eventHolder.what.setTextColor(getColor(R.color.nine));
                                eventHolder.what.setText(getString(R.string.auto_control_smart_plug));
                                eventHolder.icon.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ccc)));
                                break;
                            case ONGOING: case DONE:
                                eventHolder.status.setColor(getColor(R.color.tina_green));
                                eventHolder.what.setTextColor(getColor(R.color.tina_green));
                                eventHolder.what.setText(getString(R.string.auto_controlled_smart_plug));
                                eventHolder.icon.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.tina_green)));
                                break;
                        }
                    }

                    @Override
                    public void onReceive1(EventHolder eventHolder) {

                    }

                    @Override
                    public void onReceive2(Cursor cursor) {

                    }

                    @Override
                    public void onReceive3(Integer integer) {

                    }

                    @Override
                    public void onReceive4(Boolean aBoolean) {

                    }
                });
        recyclerView.setAdapter(adapter);
        refreshDate();
        if(Preferences.getInstance().isPredicting()){
            onPredictionStarted();
        }else{
            onRefresh(true);
        }
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

    private void onPredictionStarted(){
        refreshDate();
        adapter.setCursor(null);
        empty.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        infoTextView.setText(getString(R.string.hint_predicting));
    }

    private void onPredictionSuccess(){
        onRefresh(true);
    }

    private void onPredictionError(int statusCode, String message){
        onRefresh(false);
        LitigyException e = new LitigyException(statusCode, message);
        Log.d("Error from prediction: "+message);
        switch (e.toServiceException()){
            case InternetUnavailableException:
                Snackbar.showLong(OutletAutomationFragment.this, R.string.error_internet_unavailable);
                break;
            default:
                Snackbar.showLong(OutletAutomationFragment.this, R.string.error_service_unavailable);
                break;
        }
        error.setVisibility(View.VISIBLE);
        refreshLayout.setEnabled(true);
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
        onRefresh(true);
    }

    @Override
    public void onReceive(Cursor cursor, LitigyException e) {
    }

    @Override
    public void onReceive1(Cursor cursor) {
        if(Preferences.getInstance().isPredicting())
            return;
        now = DateTime.now(DateTimeZone.getDefault());
        onRefresh(false);
        adapter.setCursor(cursor);
        error.setVisibility(View.GONE);
        infoTextView.setText(adapter.isEmpty()? getString(R.string.no_today_predictions) :
                (adapter.getCount() == 1 ? getString(R.string.one_predicted) : ""+adapter.getCount()
                        + " " + getString(R.string.predictions)));
        empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        refreshLayout.setEnabled(false);
    }

    @Override
    public void onReceive2(LitigyException e) {
        onRefresh(false);
        error.setVisibility(adapter.isEmpty() ? View.VISIBLE: View.GONE);
        empty.setVisibility(View.GONE);
        infoTextView.setText("");
        switch (e.toServiceException()){
            case InternetUnavailableException:
                Snackbar.showLong(OutletAutomationFragment.this, R.string.error_internet_unavailable);
                break;
            default:
                Snackbar.showLong(OutletAutomationFragment.this, R.string.error_service_unavailable);
                break;
        }
    }


    private class EventHolder extends RecyclerView.ViewHolder{

        PigressBar status;
        TextView what, when, duration, name;
        CardView card;
        FloatingActionButton icon;

        public EventHolder(View itemView) {
            super(itemView);
            status = (PigressBar) findViewById(R.id.status);
            what = (TextView) findViewById(R.id.what);
            duration = (TextView) findViewById(R.id.duration);
            when = (TextView) findViewById(R.id.when);
            name = (TextView) findViewById(R.id.name);
            card = (CardView) findViewById(R.id.card);
            icon = (FloatingActionButton) findViewById(R.id.icon);
        }

        private View findViewById(int res){
            return itemView.findViewById(res);
        }
    }

    private void onRefresh(final boolean refresh){
        now = DateTime.now(DateTimeZone.getDefault());
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refresh);
            }
        });
        if(!refresh)
            return;
        infoTextView.setText(getString(R.string.hint_predicting));
        try {
            String date = Value.TO.stringValue(now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth());
            if(!Preferences.getInstance().lastPredicted().contentEquals(date)){
                if(Preferences.getInstance().isPredicting()){
                    onPredictionStarted();
                }else{
                    getActivity().getApplicationContext().startService(new Intent(getContext(), PredictionService.class));
                }
            }else {
                EventTable.EventLoader.getInstance(date, Authenticator.getInstance().getUser(false), ((OutletActionsFragment) getParentFragment()).getSmartPlug().getId(), this).load();
            }
        } catch (Exception e) {
            onRefresh(false);
            Snackbar.showLong(this, getString(R.string.error_no_accounts));
        }
    }

}
