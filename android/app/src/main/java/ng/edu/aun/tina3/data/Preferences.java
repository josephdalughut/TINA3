package ng.edu.aun.tina3.data;

import android.content.Context;

import com.litigy.lib.android.cache.SecureSharedPreferences;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.Application;

/**
 * Created by joeyblack on 11/23/16.
 */

public class Preferences extends SecureSharedPreferences {

    private static Preferences instance;

    public static Preferences getInstance(){
        return Value.IS.nullValue(instance) ? new Preferences(Application.getInstance()) : instance;
    }

    {
        instance = this;
    }

    public Preferences(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return "tinaprefs";
    }

    @Override
    public String getPassword() {
        return "80WXXFOip192roZJ2Gul9CYPGIlIq6F4";
    }
}
