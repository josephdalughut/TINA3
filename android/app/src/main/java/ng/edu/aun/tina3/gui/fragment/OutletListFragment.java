package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litigy.lib.android.gui.adapter.GenericRecyclerViewCursorAdapter;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.Consumer;
import com.litigy.lib.java.generic.DoubleConsumer;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.generic.QuatroReceiver;
import com.litigy.lib.java.generic.Receiver;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.gui.activity.Activity;
import ng.edu.aun.tina3.gui.custom.LightSwitchStub;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.model.SmartPlug;

/**
 * Created by joeyblack on 11/19/16.
 */

public class OutletListFragment extends BroadcastFragtivity implements DrawerLayout.DrawerListener, DoubleReceiver<Cursor, LitigyException>, SwipeRefreshLayout.OnRefreshListener {

    public static OutletListFragment getInstance(){
        return new OutletListFragment();
    }

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View empty, error, menuButton;
    private FloatingActionButton addButton;
    private GenericRecyclerViewCursorAdapter<SmartPlugHolder> adapter;

    @Override
    public String[] getIntentActions() {
        return new String[]{SmartPlugTable.Constants.UPDATE_INTENT};
    }

    @Override
    public void onIntent(Intent intent) {
        onRefresh(true);
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        error = findViewById(R.id.error);
        empty = findViewById(R.id.empty);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        menuButton = findViewById(R.id.menuButton);
    }

    @Override
    public void setupViews() {
        getChildFragmentManager().beginTransaction().replace(R.id.navigationContainer, OutletListNavigationFragment.getInstance(new Receiver<Integer>() {
            @Override
            public void onReceive(Integer integer) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        })).commitAllowingStateLoss();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        refreshLayout.setColorSchemeResources(R.color.tina_green);
        drawerLayout.addDrawerListener(this);
        refreshLayout.setOnRefreshListener(this);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getActivity()).addFragment(OutletAddFragment.getInstance());
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
        adapter = GenericRecyclerViewCursorAdapter.<SmartPlugHolder>getInstance()
                .setViewReceiver(new QuatroReceiver<SmartPlugHolder, Cursor, Integer, Boolean>() {
                    @Override
                    public void onReceive(SmartPlugHolder smartPlugHolder, final Cursor cursor, final Integer integer, Boolean aBoolean) {
                        String name = cursor.getString(cursor.getColumnIndex(SmartPlugTable.Constants.Columns.NAME));
                        String type = cursor.getString(cursor.getColumnIndex(SmartPlugTable.Constants.Columns.TYPE));
                        String state = cursor.getString(cursor.getColumnIndex(SmartPlugTable.Constants.Columns.STATE));
                        smartPlugHolder.lightSwitch.stateTextView.setText(name);
                        if(state.equalsIgnoreCase("on")){
                            smartPlugHolder.lightSwitch.setStatus(LightSwitchStub.Status.ON);
                        }else{
                            smartPlugHolder.lightSwitch.setStatus(LightSwitchStub.Status.OFF);
                        }
                        smartPlugHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(cursor.moveToPosition(integer))
                                    onClickSmartPlug(SmartPlugTable.from(cursor));
                            }
                        });
                    }

                    @Override
                    public void onReceive1(SmartPlugHolder smartPlugHolder) {

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
                }).setViewTypeConsumer(new Consumer<Integer, Cursor>() {
                    @Override
                    public Integer onConsume(Cursor cursor) {
                        return 0;
                    }
                }).setViewConsumer(new DoubleConsumer<SmartPlugHolder, ViewGroup, Integer>() {
                    @Override
                    public SmartPlugHolder onConsume(ViewGroup viewGroup, Integer integer) {
                        return new SmartPlugHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_smart_plug, viewGroup, false));
                    }

                    @Override
                    public SmartPlugHolder onConsume1(ViewGroup viewGroup) {
                        return null;
                    }

                    @Override
                    public SmartPlugHolder onConsume2(Integer integer) {
                        return null;
                    }
                }).setIdConsumer(new DoubleConsumer<Long, Cursor, Integer>() {
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
                });
        recyclerView.setAdapter(adapter);
        onRefresh(true);
    }

    private void onClickSmartPlug(SmartPlug smartPlug){
        ((Activity)getActivity()).addFragment(OutletActionsFragment.getInstance(smartPlug));
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
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        addButton.hide();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        addButton.show();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    private void onRefresh(final boolean refresh){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refresh);
            }
        });
        if(!refresh)
            return;
        SmartPlugTable.SmartPlugLoader.getInstance(this).load();
    }

    @Override
    public void onReceive(Cursor cursor, LitigyException e) {

    }

    @Override
    public void onReceive1(Cursor cursor) {
        onRefresh(false);
        adapter.setCursor(cursor);
        error.setVisibility(View.GONE);
        empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onReceive2(LitigyException e) {
        onRefresh(false);
        error.setVisibility(adapter.isEmpty() ? View.VISIBLE: View.GONE);
        empty.setVisibility(View.GONE);
        switch (e.toServiceException()){
            case InternetUnavailableException:
                Snackbar.showLong(OutletListFragment.this, R.string.error_internet_unavailable);
                break;
            default:
                Snackbar.showLong(OutletListFragment.this, R.string.error_service_unavailable);
                break;
        }
    }

    @Override
    public void onRefresh() {
        onRefresh(true);
    }

    private static class SmartPlugHolder extends RecyclerView.ViewHolder {

        private LightSwitchStub lightSwitch;

        public SmartPlugHolder(View itemView) {
            super(itemView);
            lightSwitch = (LightSwitchStub) findViewById(R.id.lightSwitch);
        }

        private View findViewById(int res){
            return itemView.findViewById(res);
        }
    }
}
