package ng.edu.aun.tina3.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {

    public static class Accounts {
        public static final String INTENT_SUCCESS = "com.joeydalu.wordit.android.util.PermissionUtils.ACCOUNTS.SUCCESS";
        public static final String INTENT_FAILURE = "com.joeydalu.wordit.android.util.PermissionUtils.ACCOUNTS.FAILURE";
        public static final int REQUEST_CODE = 101;

        public static boolean isPermissionRequired(Context context){
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                    != PackageManager.PERMISSION_GRANTED;
        }

        public static void requestPermissions(Activity activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_CODE);
            }
        }
    }

    public static class Files {
        public static final String INTENT_SUCCESS = "com.joeydalu.wordit.android.util.PermissionUtils.FILES.SUCCESS";
        public static final String INTENT_FAILURE = "com.joeydalu.wordit.android.util.PermissionUtils.FILES.FAILURE";
        public static final int REQUEST_CODE = 103;

        public static boolean isPermissionRequired(Context context){
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
        }

        public static void requestPermissions(Activity activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }


}
