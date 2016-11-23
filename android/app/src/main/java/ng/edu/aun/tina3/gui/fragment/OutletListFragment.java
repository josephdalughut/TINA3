package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;

import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.java.generic.Receiver;

import ng.edu.aun.tina3.R;

/**
 * Created by joeyblack on 11/19/16.
 */

public class OutletListFragment extends BroadcastFragtivity implements DrawerLayout.DrawerListener {

    public static OutletListFragment getInstance(){
        return new OutletListFragment();
    }

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View empty, error;
    private FloatingActionButton addButton;

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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        error = findViewById(R.id.error);
        empty = findViewById(R.id.empty);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
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
}
