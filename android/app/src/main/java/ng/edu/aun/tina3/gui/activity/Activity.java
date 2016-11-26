package ng.edu.aun.tina3.gui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.gui.fragment.OutletActionsFragment;
import ng.edu.aun.tina3.gui.fragment.OutletAddTestFragment;
import ng.edu.aun.tina3.gui.fragment.SplashFragment;
import ng.edu.aun.tina3.gui.fragment.WelcomeFragment;
import ng.edu.aun.tina3.util.PermissionUtils;

public class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        //replaceFragment(WelcomeFragment.getInstance());
        replaceFragment(SplashFragment.getInstance());
    }

    public void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment)
                .addToBackStack(null).commitAllowingStateLoss();
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean success = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case PermissionUtils.Accounts.REQUEST_CODE:
                getApplicationContext().sendBroadcast(new Intent(success ? PermissionUtils.Accounts.INTENT_SUCCESS : PermissionUtils.Accounts.INTENT_FAILURE));
                break;
            case PermissionUtils.Files.REQUEST_CODE:
                getApplicationContext().sendBroadcast(new Intent(success ? PermissionUtils.Files.INTENT_SUCCESS : PermissionUtils.Files.INTENT_FAILURE));
                break;
        }
    }

}
