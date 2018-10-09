package com.bm.wb.ui.zoo;

import android.os.Bundle;
import android.view.View;

import com.bm.wb.R;

import zoo.hymn.base.ui.BaseFragment;

/**
 * ClassName: MeFragment
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/17
 */

public class MeFragment extends BaseFragment {


    @Override
    protected int setBodyLayout() {
        return R.layout.zoo_me_fg;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitleMode(GONE_TITLE);
    }


    @Override
    public void onClick(View view) {

    }
}
