package ng.edu.aun.tina3.gui.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ng.edu.aun.tina3.R;
import ng.edu.aun.tina3.gui.fragment.OutletActionsFragment;
import ng.edu.aun.tina3.gui.fragment.OutletAddTestFragment;
import ng.edu.aun.tina3.gui.fragment.SplashFragment;
import ng.edu.aun.tina3.gui.fragment.WelcomeFragment;

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

}
