package ng.edu.aun.tina3;


import android.content.Context;
import android.support.multidex.MultiDex;

public class Application extends android.app.Application {

    /**
     * Singleton instance of the {@link Application} class.
     */
    private static Application instance;

    {
        instance = this;
    }

    /**
     * @return the singleton instance of the {@link Application} class.
     */
    public static Application getInstance(){
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
