package zoo.hymn.views.pickSinglePhoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bm.wb.R;

import java.io.File;

import zoo.hymn.utils.FileUtil;
import zoo.hymn.utils.PhotoUtils;

/**
 * 选择照片对话框
 */
public class PickSinglePhotoDialog {
    /**
     * @param activity
     * @param imageFile 选择拍照需要的文件对象
     */
    public PickSinglePhotoDialog(final Activity activity, final File imageFile) {
        View clearView = LayoutInflater.from(activity).inflate(
                R.layout.xdg_image, null);
        clearBuilder = new Dialog(activity, R.style.common_dialog_bg);
        Button btn_select_gallery = (Button) clearView
                .findViewById(R.id.btn_select_gallery);
        Button btn_take_photo = (Button) clearView
                .findViewById(R.id.btn_take_photo);
        Button btn_cancel = (Button) clearView.findViewById(R.id.btn_cancel);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int i = v.getId();
                //从相册选照片
                if (i == R.id.btn_select_gallery) {
                    if (PhotoUtils.verifyStoragePermissions(activity)) {
                        PhotoUtils.openPic(activity);
                    }
                    closeClearDialog();
                } else if (i == R.id.btn_take_photo) {//拍照
                    if (PhotoUtils.verifyCameraPermissions(activity)) {
                        Uri imageUri;
                        if (PhotoUtils.hasSdcard()) {
                            Log.i("imageFile", imageFile.getPath());
                            imageUri = Uri.fromFile(imageFile);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                imageUri = FileProvider.getUriForFile(activity, PhotoUtils.PACKAGENAME_FILEPROVIDER, imageFile);//通过FileProvider创建一个content类型的Uri
                            }
                            PhotoUtils.takePicture(activity, imageUri, PhotoUtils.REQUEST_CAMERA);
                        } else {
                            Toast.makeText(activity, "无SD卡", Toast.LENGTH_SHORT).show();
                        }
                    }
                    closeClearDialog();
                } else if (i == R.id.btn_cancel) {//取消
                    closeClearDialog();
                }
            }
        };
        btn_select_gallery.setOnClickListener(clickListener);
        btn_take_photo.setOnClickListener(clickListener);
        btn_cancel.setOnClickListener(clickListener);

        Window window = clearBuilder.getWindow();
        window.setGravity(Gravity.BOTTOM);
        clearBuilder.setContentView(clearView);

    }

    private Dialog clearBuilder;

    public void closeClearDialog() {
        if (clearBuilder != null) {
            clearBuilder.dismiss();
        }
    }

    public void showClearDialog() {
        if (clearBuilder != null && !clearBuilder.isShowing()) {
            clearBuilder.show();
        }
    }

    public boolean isShowingDialogs() {
        return clearBuilder.isShowing();
    }

    public void recycleClearDialog() {
        if (clearBuilder != null) {
            if (clearBuilder.isShowing()) {
                clearBuilder.dismiss();
            }
            clearBuilder = null;
        }
    }

    public void setCancel(boolean flag) {
        if (clearBuilder != null) {
            clearBuilder.setCancelable(flag);
        }
    }

    private static File getSelectPhotosFile(Context context){
        File file = new File(FileUtil.getCacheFile(context),"selectPhotos");
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }
    public static Uri getCropImageUri(Context context){
        return Uri.fromFile(new File(getSelectPhotosFile(context)+"/crop_temp.jpg"));
    }
    public static File getCurrentImageFile(Context context){
        return new File(getSelectPhotosFile(context), System.currentTimeMillis() + ".jpg");
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        activity.finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivity(intent);
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }
}
