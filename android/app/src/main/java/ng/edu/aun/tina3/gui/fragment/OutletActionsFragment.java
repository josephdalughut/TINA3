package ng.edu.aun.tina3.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.litigy.lib.android.gui.pager.NaughtyPager;
import com.litigy.lib.android.gui.view.textView.TextView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.rest.model.SmartPlug;

/**
 * Created by joeyblack on 11/24/16.
 */

public class OutletActionsFragment extends BroadcastFragtivity implements ViewPager.OnPageChangeListener {

    private SmartPlug smartPlug;
    private TextView automationTextView, controlTextView;
    private NaughtyPager pager;
    private UnderlinePageIndicator indicator;
    private List<Fragment> fragments;
    private SmartPlugTable smartPlugTable;

    public static OutletActionsFragment getInstance(SmartPlug smartPlug){
        return new OutletActionsFragment().setSmartPlug(smartPlug);
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
        return R.layout.fragment_outlet_actions;
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
        automationTextView = (TextView) findViewById(R.id.automationTextView);
        controlTextView = (TextView) findViewById(R.id.controlTextView);
        pager = (NaughtyPager) findViewById(R.id.pager);
        indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
    }

    @Override
    public void setupViews() {
        smartPlugTable = new SmartPlugTable();
        fragments = new ArrayList<>();
        fragments.add(OutletControlFragment.getInstance());
        fragments.add(OutletAutomationFragment.getInstance());
        pager.setPagingEnabled(true);
        pager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        automationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
            }
        });
        controlTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
            }
        });
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(this);
    }

    public SmartPlugTable getSmartPlugTable() {
        return smartPlugTable;
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

    public SmartPlug getSmartPlug() {
        return smartPlug;
    }

    public OutletActionsFragment setSmartPlug(SmartPlug smartPlug) {
        this.smartPlug = smartPlug;
        return this;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                controlTextView.setTextColor(getColor(R.color.tina_green));
                automationTextView.setTextColor(getColor(R.color.ccc));
                break;
            case 1:
                controlTextView.setTextColor(getColor(R.color.ccc));
                automationTextView.setTextColor(getColor(R.color.tina_green));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
