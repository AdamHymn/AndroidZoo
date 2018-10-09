package com.bm.wb.ui.zoo;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bm.wb.R;

import java.io.File;
import java.io.FileNotFoundException;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.BitmapUtil;
import zoo.hymn.views.EaseTitleBar;

/**
 * ClassName: CompressingActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/31
 */

public class CompressingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addChildView(R.layout.compressing_ac);
    }

    @Override
    protected void initView() {
        ((EaseTitleBar) defaultTitleView).setTitle("压缩图片");
    }

    @Override
    protected void initData() {

        try {
            File file = BitmapUtil.compressImage(this,(Uri)getIntent().getParcelableExtra("orgUri"),getIntent().getStringExtra("desUri"));
            if(file != null && file.length() > 0){
                setResult(RESULT_OK);
                finish();
            }else{
                showToast("压缩失败");
                finish();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    public void onClick(View view) {

    }


}
