package zoo.hymn.views.pickSinglePhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wb.R;

import java.io.File;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.BitmapUtil;
import zoo.hymn.utils.PhotoUtils;
import zoo.hymn.views.EaseTitleBar;


/**
 * ClassName: PickSinglePhotoSampleActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/6/19
 */

public class PickSinglePhotoSampleActivity extends BaseActivity {

    private TextView tv_add;
    private ImageView iv, iv2;
    private Bitmap bitmap = null;
    private File imageFile = null;
    private Uri imageUri;
    private Uri cropImageUri;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addChildView(R.layout._zoo_select_photos_sample_ac);
    }

    @Override
    protected void initView() {

        ((EaseTitleBar) defaultTitleView).setTitle("选择照片");
        tv_add = (TextView) findViewById(R.id.tv_add);
        iv = (ImageView) findViewById(R.id.iv);
        iv2 = (ImageView) findViewById(R.id.iv2);
        tv_add.setOnClickListener(this);
//        Glide.with(this).load("http://www.wellbright.com:9004/images/avata/2248.png").into(iv);
//        Glide.with(this).load("http://www.wellbright.com:9004/images/avata/2248.png").into(iv2);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        cropImageUri = PickSinglePhotoDialog.getCropImageUri(this);
        imageFile = PickSinglePhotoDialog.getCurrentImageFile(this);
        PickSinglePhotoDialog dialog = new PickSinglePhotoDialog(this, imageFile);
        dialog.showClearDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //兼容7.0以上
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.REQUEST_CAMERA://拍照完成回调
                    imageUri = Uri.fromFile(imageFile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(this, PhotoUtils.PACKAGENAME_FILEPROVIDER, imageFile);//通过FileProvider创建一个content类型的Uri
                    }
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri);
                    break;
                case PhotoUtils.REQUEST_GALLERY://访问相册完成回调
                    Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(this, PhotoUtils.PACKAGENAME_FILEPROVIDER, new File(newUri.getPath()));
                    }
                    PhotoUtils.cropImageUri(this, newUri, cropImageUri);
                    break;
                case PhotoUtils.REQUEST_CROP_IMAGE://裁剪完图片后的操作
                    imageFile = null;
                    bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        imageFile = BitmapUtil.saveBitmapToFile(this, bitmap);
                    }
                    if (imageFile != null) {
                        // 调取上传头像接口。。
                        //设置图片。。
                        tv_add.setBackground(new BitmapDrawable(bitmap));
                    }
                    break;
            }
        }

    }



}
