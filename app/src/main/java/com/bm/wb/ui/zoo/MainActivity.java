package com.bm.wb.ui.zoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bm.wb.R;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.views.EaseTitleBar;


public class MainActivity extends BaseActivity {

    private static final String CURRENT_TAB_INDEX = "CURRENT_TAB_INDEX";
    public static final String LOGOUT_BROADCAST = "com.bm.logout";
    private int currentTabIndex = -1;// 当前fragment的index
    private int index = 0;//点击底部按钮记录的index
    private Button[] mTabs;
    private Fragment[] fragments;
    private HomeFragment homeFragment;
    private MeFragment meFragment;
    private boolean isGoHome = false;
    private BroadcastReceiver logoutBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("Action:" + action);
            if (LOGOUT_BROADCAST.equals(action)) {
                isGoHome = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitleMode(GONE_TITLE);
        addChildView(R.layout.zoo_main);
        if (savedInstanceState != null) {
            currentTabIndex = savedInstanceState.getInt(CURRENT_TAB_INDEX, 0);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutBroadcastReceiver,new IntentFilter(LOGOUT_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutBroadcastReceiver);
    }

    @Override
    protected void initView() {

        ((EaseTitleBar)defaultTitleView).setLeftLayoutVisibility(View.GONE);
        ((EaseTitleBar)defaultTitleView).setTitle(R.string.fg_home);
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_staff);
        mTabs[1] = (Button) findViewById(R.id.btn_assets);
        mTabs[2] = (Button) findViewById(R.id.btn_wbd);
        mTabs[3] = (Button) findViewById(R.id.btn_my);
        mTabs[0].setSelected(true);// 把第一个tab设为选中状态
        mTabs[0].setOnClickListener(this);
        mTabs[1].setOnClickListener(this);
        mTabs[2].setOnClickListener(this);
        mTabs[3].setOnClickListener(this);
    }


    @Override
    protected void initData() {

        homeFragment = new HomeFragment();
        meFragment = new MeFragment();
        fragments = new Fragment[]{homeFragment, meFragment,homeFragment, meFragment,homeFragment};
        // 添加显示第一个或上次退出时的fragment
        if (currentTabIndex != -1) {
            goFragment();
        } else {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, homeFragment, 0 + "")
                    .show(homeFragment)
                    .commit();
        }
    }

    @Override
    public void reLoad(View view) {
        super.reLoad(view);
    }


    private void loadFragmentData() {
        switch (index) {
            case 0:
                HomeFragment homeFragment = (HomeFragment) fragments[index];
                homeFragment.initData();
                break;
            case 1:
                MeFragment meFragment = (MeFragment) fragments[index];
                meFragment.initData();
                break;
            case 2:
                HomeFragment homeFragment1 = (HomeFragment) fragments[index];
                homeFragment1.initData();
                break;
            case 3:
                MeFragment meFragment1 = (MeFragment) fragments[index];
                meFragment1.initData();
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_staff) {
            index = 0;
            ((EaseTitleBar)defaultTitleView).setLeftLayoutVisibility(View.GONE);
            ((EaseTitleBar)defaultTitleView).setTitle(R.string.fg_home);
            goFragment();
            return;
        }

        if (view.getId() == R.id.btn_assets) {
            ((EaseTitleBar)defaultTitleView).setLeftLayoutVisibility(View.GONE);
            ((EaseTitleBar)defaultTitleView).setTitle(R.string.fg_assets);
            index = 1;
            goFragment();
            return;
        }

        if (view.getId() == R.id.btn_wbd) {
//            if (!SharedPreferencesUtil.get(mContext, ZooConstant.IS_LOGIN, false)) {
//                startActivity(new Intent(mContext, LoginActivity.class));
//                return;
//            }
            index = 2;
            ((EaseTitleBar)defaultTitleView).setLeftLayoutVisibility(View.GONE);
            ((EaseTitleBar)defaultTitleView).setTitle(R.string.fg_wj);
            goFragment();
            return;
        }
        if (view.getId() == R.id.btn_my) {
//            if (!SharedPreferencesUtil.get(mContext, ZooConstant.IS_LOGIN, false)) {
//                startActivity(new Intent(mContext, LoginActivity.class));
//                return;
//            }
            ((EaseTitleBar)defaultTitleView).setLeftLayoutVisibility(View.GONE);
            ((EaseTitleBar)defaultTitleView).setTitle(R.string.fg_me);
            index = 3;
            goFragment();
            return;
        }
    }

    /**
     * 跳转到你选择的界面Fragment
     */
    private void goFragment() {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            //设置切换动画
            if (currentTabIndex < index) {
                trx.setCustomAnimations(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            } else {
                trx.setCustomAnimations(R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }

            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index], index + "");
            }
            trx.show(fragments[index]);
            trx.commit();

            mTabs[currentTabIndex].setSelected(false);
            // 把当前tab设为选中状态
            mTabs[index].setSelected(true);
            currentTabIndex = index;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TAB_INDEX, currentTabIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainAc-onResume", currentTabIndex + "");
        if (currentTabIndex == -1) {
            currentTabIndex = 0;
        } else {
            loadFragmentData();
        }
        if(isGoHome){
            goHome();
        }
    }

    public void goHome() {
        index = 0;
        mTabs[0].setSelected(true);// 把第一个tab设为选中状态
        mTabs[1].setSelected(false);
        mTabs[2].setSelected(false);
        mTabs[3].setSelected(false);
        mTabs[4].setSelected(false);
        goFragment();
    }
}
