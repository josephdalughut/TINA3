package ng.edu.aun.tina3.util;

/**
 * Created by joeyblack on 11/9/16.
 */

public class Log {

    public static final String LOG_TAG = "TinaDebug";

    public static void d(String message){
        android.util.Log.d(LOG_TAG, message);
    }

}
