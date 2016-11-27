package ng.edu.aun.tina3.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.litigy.lib.android.gui.dialog.DialogFragtivity;
import com.litigy.lib.android.gui.dialog.InfoDialog;
import com.litigy.lib.android.gui.dialog.ProgressDialog;
import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.android.gui.pager.NaughtyPager;
import com.litigy.lib.android.gui.view.button.Button;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.util.Value;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.data.SmartPlugTable;
import ng.edu.aun.tina3.gui.misc.KeyboardUtils;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
import ng.edu.aun.tina3.rest.model.SmartPlug;

/**
 * Created by joeyblack on 11/25/16.
 */

public class OutletAddFragment extends Fragtivity {

    public static OutletAddFragment getInstance(){
        return new OutletAddFragment();
    }

    private View closeButton;
    private Button nextButton;
    private CirclePageIndicator indicator;
    private NaughtyPager pager;
    private ProgressDialog progressDialog;

    private SmartPlug smartPlug;

    private List<Fragment> fragments;

    @Override
    public int layoutId() {
        return R.layout.fragment_outlet_add;
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
        pager = (NaughtyPager) findViewById(R.id.pager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        nextButton = (Button) findViewById(R.id.nextButton);
        closeButton = findViewById(R.id.closeButton);
    }

    @Override
    public void setupViews() {
        fragments = new ArrayList<>();
        fragments.add(OutletAddInfoFragment.getInstance());
        fragments.add(OutletAddIDFragment.getInstance());
        fragments.add(OutletAddNameFragment.getInstance());
        fragments.add(OutletAddTestFragment.getInstance());
        pager.setPagingEnabled(false);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = pager.getCurrentItem();
                switch (currentItem){
                    case 0:
                        pager.setCurrentItem(currentItem+1);
                        break;
                    case 1:
                        onIdProvided();
                        break;
                    case 2:
                        onNameProvided();
                        break;
                    case 3:
                        onSmartPlugAdded();
                        break;
                }
            }
        });
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
        pager.setOffscreenPageLimit(fragments.size());
        indicator.setViewPager(pager);
    }

    private void onIdProvided(){
        hideProgress();
        KeyboardUtils.hide(this);
        OutletAddIDFragment outletAddIDFragment = (OutletAddIDFragment) fragments.get(1);
        String id = outletAddIDFragment.id.getText().toString();
        if(Value.IS.emptyValue(id)){
            Snackbar.showLong(this, getString(R.string.error_empty_id));
            return;
        }
        if(id.length()<22){
            Snackbar.showLong(this, getString(R.string.error_invalid_smartplug_id));
            return;
        }
        progressDialog = ProgressDialog.getInstance(getString(R.string.please_wait), getString(R.string.adding_smart_plug), getColor(R.color.tina_green), false);
        progressDialog.show(getChildFragmentManager(), null);
        SmartPlugApi.create(id, new DoubleReceiver<SmartPlug, LitigyException>() {
            @Override
            public void onReceive(SmartPlug smartPlug, LitigyException e) {

            }

            @Override
            public void onReceive1(SmartPlug smartPlug) {
                OutletAddFragment.this.smartPlug = smartPlug;
                hideProgress();
                onSmartPlugCreated();
            }

            @Override
            public void onReceive2(LitigyException e) {
                hideProgress();
                switch (e.toServiceException()){
                    case InternetUnavailableException:
                        Snackbar.showShort(getRootView(), R.string.error_internet_unavailable);
                        break;
                    case NotFoundException:
                        //plug could not be communicated to
                        Snackbar.showLong(OutletAddFragment.this, R.string.error_smartplug_not_found);
                        break;
                    case ConflictException:
                        //if plug has been added by someone else
                        Snackbar.showLong(OutletAddFragment.this, R.string.error_smartplug_conflict);
                        break;
                    default:
                        Snackbar.showLong(OutletAddFragment.this, R.string.error_service_unavailable);
                        break;
                }
            }
        });
    }

    private void onSmartPlugAdded(){
        hideProgress();
        KeyboardUtils.hide(this);
        new SmartPlugTable().addSmartPlug(((OutletAddTestFragment)fragments.get(3)).getSmartPlug(), true);
        getActivity().onBackPressed();
    }

    private void onNameProvided(){
        hideProgress();
        KeyboardUtils.hide(this);
        OutletAddNameFragment outletAddNameFragment = (OutletAddNameFragment) fragments.get(2);
        String name = outletAddNameFragment.name.getText().toString();
        if(Value.IS.emptyValue(name)){
            pager.setCurrentItem(3);
            return;
        }
        progressDialog = ProgressDialog.getInstance(getString(R.string.please_wait), getString(R.string.naming_smart_plug), getColor(R.color.tina_green), false);
        progressDialog.show(getChildFragmentManager(), null);
        SmartPlugApi.rename(smartPlug.getId(), name, new DoubleReceiver<SmartPlug, LitigyException>() {
            @Override
            public void onReceive(SmartPlug smartPlug, LitigyException e) {

            }

            @Override
            public void onReceive1(SmartPlug smartPlug) {
                hideProgress();
                OutletAddFragment.this.smartPlug = smartPlug;
                onSmartPlugRenamed();
            }

            @Override
            public void onReceive2(LitigyException e) {
                hideProgress();
                switch (e.toServiceException()){
                    case InternetUnavailableException:
                        Snackbar.showShort(getRootView(), R.string.error_internet_unavailable);
                        break;
                    case NotFoundException:
                        //plug does not belong to
                        Snackbar.showLong(OutletAddFragment.this, R.string.error_smartplug_rename_not_found);
                        break;
                    default:
                        Snackbar.showLong(OutletAddFragment.this, R.string.error_service_unavailable);
                        break;
                }
            }
        });
    }

    private void onSmartPlugCreated(){
        pager.setCurrentItem(2);
        InfoDialog.getInstance(getString(R.string.smart_plug), getString(R.string.smart_plug_created_now_name), true)
                .withPositiveButton(getString(R.string.okay), getColor(R.color.tina_green), new InfoDialog.OnClickListener() {
                    @Override
                    public void onClick(View view, DialogFragtivity dialogFragtivity) {
                        dialogFragtivity.dismissAllowingStateLoss();
                    }
                })
                .show(getChildFragmentManager(), null);
    }

    private void onSmartPlugRenamed(){
        pager.setCurrentItem(3);
        ((OutletAddTestFragment)fragments.get(3)).setSmartPlug(smartPlug);
    }

    private void hideProgress(){
        if(progressDialog!=null){
            try{
                progressDialog.dismissAllowingStateLoss();
                progressDialog = null;
            }catch (Exception ignored){

            }
        }
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
