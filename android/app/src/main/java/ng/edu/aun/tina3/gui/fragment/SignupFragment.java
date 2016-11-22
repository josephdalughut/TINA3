package ng.edu.aun.tina3.gui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.litigy.lib.android.gui.dialog.ProgressDialog;
import com.litigy.lib.android.gui.fragment.Fragtivity;
import com.litigy.lib.android.gui.view.editText.EditText;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import java.util.Map;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.gui.activity.Activity;
import ng.edu.aun.tina3.gui.misc.Snackbar;
import ng.edu.aun.tina3.rest.api.AuthApi;
import ng.edu.aun.tina3.rest.api.UserApi;
import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.util.JsonUtils;
import ng.edu.aun.tina3.util.Log;
import ng.edu.aun.tina3.util.Value;

/**
 * Created by joeyblack on 11/19/16.
 */

public class SignupFragment extends Fragtivity implements DoubleReceiver<User, LitigyException> {

    private View backButton, nextButton;
    private EditText id, password;
    private AsyncTask task;

    private ProgressDialog progressDialog;

    public static SignupFragment getInstance(){
        return new SignupFragment();
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_signup;
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
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
    }

    @Override
    public void setupViews() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validCredentials())
                    onSignup();
            }
        });
    }

    private boolean validCredentials(){
        if(Value.IS.emptyValue(id.getText().toString())){
            Snackbar.showShort(getRootView(), getString(R.string.error_empty_aun_id));
            id.requestFocus();
            return false;
        }
        if(!Value.IS.VALID.aunId(id.getText().toString())){
            Snackbar.showLong(this, R.string.error_invalid_aun_id);
            id.requestFocus();
            return false;
        }
        if(Value.IS.emptyValue(password.getText().toString())){
            Snackbar.showShort(getRootView(), R.string.error_empty_password);
            password.requestFocus();
            return false;
        }
        if(!Value.IS.VALID.Patterns.PASSWORD_DEFAULT.matcher(password.getText().toString()).matches()){
            Snackbar.showLong(this, R.string.error_invalid_password);
            password.requestFocus();
            return false;
        }
        return true;
    }


    private void onSignup(){
        progressDialog = ProgressDialog.getInstance(getString(R.string.please_wait), getString(R.string.signing_up), getColor(R.color.tina_green), false);
        progressDialog.show(getChildFragmentManager(), null);
        UserApi.signup(id.getText().toString(), password.getText().toString(), this);
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
    public void onReceive(User user, LitigyException e) {

    }

    @Override
    public void onReceive1(User user) {
        try{
            task = Authenticator.getInstance().setUserAsync(user, new DoubleReceiver<User, LitigyException>() {
                        @Override
                        public void onReceive(User user, LitigyException e) {

                        }

                        @Override
                        public void onReceive1(User user) {
                            progressDialog.dismissAllowingStateLoss();
                            ((Activity)getActivity()).replaceFragment(OutletListFragment.getInstance());
                        }

                        @Override
                        public void onReceive2(LitigyException e) {
                            progressDialog.dismissAllowingStateLoss();
                            Snackbar.showLong(SignupFragment.this, R.string.error_service_unavailable);
                        }
                    });
        }catch (Exception ignored){

        }
    }

    @Override
    public void onReceive2(LitigyException e) {
        Log.d("Exception from login: "+e.getMessage());
        try{
            progressDialog.dismissAllowingStateLoss();
            switch (e.toServiceException()){
                case InternetUnavailableException:
                    Snackbar.showLong(this, R.string.error_internet_unavailable);
                    break;
                case ConflictException:
                    Snackbar.showLong(this, R.string.error_signup_conflict);
                    break;
                default:
                    Snackbar.showLong(this, R.string.error_service_unavailable);
                    break;
            }
        }catch (Exception ignored){

        }
    }

}
