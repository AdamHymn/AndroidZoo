package zoo.hymn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class Tools {


    public static void toastShort(Context mContext, String msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

    }

    public static void toastLong(Context mContext, String msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 获取版本
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "未能获取到版本号";
        }
    }


    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 显示软键盘
     * @param activity
     */
    public static void showSoftInput(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (!isOpen) {
                manager.showSoftInput(v,InputMethodManager.SHOW_FORCED);
            }
        }
    }

    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (isOpen) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s) {
        if (s == null) {
            return null;
        }
        try {
            byte[] b = android.util.Base64.decode(s, android.util.Base64.DEFAULT);
            return new String(b, "utf-8");
        } catch (Exception e) {
            return null;
        }
    }

}
