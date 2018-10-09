package com.bm.wb.ui.zoo;

import android.os.Bundle;
import android.view.View;

import zoo.hymn.base.ui.WebBaseActivity;
import zoo.hymn.views.EaseTitleBar;


/**
 * ClassName: WebActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/6/20
 */

public class WebActivity extends WebBaseActivity {

    @Override
    protected void initData() {
        ((EaseTitleBar)defaultTitleView).setTitle(title);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        title = getIntent().getStringExtra("Title");
        requestUrl = getIntent().getStringExtra("requestUrl");
        htmlData = getIntent().getStringExtra("htmlData");
        super.onCreate(savedInstanceState);
    }


}
