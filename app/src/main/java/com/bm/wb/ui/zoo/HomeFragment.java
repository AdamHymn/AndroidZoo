package com.bm.wb.ui.zoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bm.wb.R;

import butterknife.OnClick;
import zoo.hymn.base.ui.BaseFragment;

/**
 * ClassName: HomeFragment
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/17
 */

public class HomeFragment extends BaseFragment {


    @Override
    protected int setBodyLayout() {
        return R.layout.zoo_home_fg;
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


    @OnClick(R.id.rl_sw)
    public void onViewClicked() {
        startActivity(new Intent(mContext,SlidingMainActivity.class));
    }
}
