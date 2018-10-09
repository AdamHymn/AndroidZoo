package zoo.hymn.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import java.io.File;
import java.util.List;

import zoo.hymn.views.CommonDialog;

public class IntentUtil {

    /**
     * 去应用市场给软件评分
     *
     * @return void
     * @throws
     * @Title: marketScore
     * @Description: TODO
     * @paramc
     */
    public static void marketScore(Context context) {
        String uriStr = "market://details?id=" + context.getPackageName();
        Uri uri = Uri.parse(uriStr);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 分享文本到第三方软件
     *
     * @param //c
     * @param content
     * @return void
     * @throws
     * @Title: shareText
     * @Description: TODO
     */
    public static void shareText(Context context, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/*");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(sendIntent);
    }


    /**
     * 启动页面跳转
     *
     * @param mContext
     * @param cls      目的类
     * @param data     传递数据 void
     */
    public static void startActivity(Context mContext, Class<?> cls, Bundle data) {
        Intent intent = new Intent(mContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (data != null) {
            intent.putExtras(data);
        }

        mContext.startActivity(intent);
    }

    /**
     * 启动页面跳转(有返回处理)
     *
     * @param cls         目的类
     * @param data        传递数据
     * @param requestCode 请求编码 void
     */
    public static void startActivityForResult(Activity mActivity, Class<?> cls,
                                              Bundle data, int requestCode) {
        Intent intent = new Intent(mActivity, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (data != null) {
            intent.putExtras(data);
        }
        // 这个方法，在Activity范围下，不再Context范围下
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * 安装APK程序代码
     *
     * @param context
     * @param apkPath
     */
    public static void ApkInstall(Context context, String apkPath) {
        File fileAPK = new File(apkPath);
        if (fileAPK.exists()
                && fileAPK.getName().toLowerCase().endsWith(".apk")) {
            Intent install = new Intent();
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setAction(android.content.Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(fileAPK),
                    "application/vnd.android.package-archive");
            context.startActivity(install);// 安装
        }
    }

    public static boolean isBackground(Context context) {


        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    static CommonDialog commonDialog;
    /**
     * 打电话
     *
     * @param content
     */
    public static void tell(final Context context, final String content) {
        commonDialog = new CommonDialog("提示", "是否拨打" + content + "?", "取消", "拨打", new CommonDialog.DialogClick() {
            @Override
            public void cancel(Object tag) {
                commonDialog.closeClearDialog();
            }

            @Override
            public void confirm(Object tag) {
                commonDialog.closeClearDialog();
                try {
                    Intent intent = new Intent("android.intent.action.CALL",
                            Uri.parse("tel:" + content));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        commonDialog.twoButtonDialog(context);
        commonDialog.showClearDialog();

    }


    public static Intent openFileByIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        if (param.endsWith("ppt")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }
        if (param.endsWith("xls") || param.endsWith("xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        if (param.endsWith("doc") || param.endsWith("docx")) {
            intent.setDataAndType(uri, "application/vnd.ms-word");
        }
        if (param.endsWith("chm")) {
            intent.setDataAndType(uri, "application/x-chm");
        }
        if (param.endsWith("txt")) {
            intent.setDataAndType(uri, "text/plain");
        }
        if (param.endsWith("pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        }
        return intent;
    }
    /**
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
