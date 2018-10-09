package com.bm.wb.ui.zoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.bm.wb.R;

import java.lang.reflect.Field;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import zoo.hymn.base.ui.BaseActivity;


public class SlidingMainActivity extends BaseActivity {


    private static final String CURRENT_TAB_INDEX = "CURRENT_TAB_INDEX";
    public static final String LOGOUT_BROADCAST = "com.bm.logout";
    private int currentTabIndex = -1;// 当前fragment的index
    private int index = 0;//点击底部按钮记录的index
    private String[] titles;
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

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMode(GONE_TITLE);
        addChildView(R.layout.zoo_sliding_main);
        if (savedInstanceState != null) {
            currentTabIndex = savedInstanceState.getInt(CURRENT_TAB_INDEX, 0);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(logoutBroadcastReceiver, new IntentFilter(LOGOUT_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutBroadcastReceiver);
    }

    @Override
    protected void initView() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnv_menu);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
        pagerTabStrip.setTextColor(Color.WHITE);
        pagerTabStrip.setVisibility(View.GONE);
    }


    @Override
    protected void initData() {

        ConversationListFragment conversationListFragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话，该会话非聚合显示
                .build();
        conversationListFragment.setUri(uri);
        homeFragment = new HomeFragment();
        meFragment = new MeFragment();
        fragments = new Fragment[]{homeFragment, conversationListFragment, meFragment};
        titles = new String[]{"首页", "聊天", "个人中心"};

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        };
        viewPager.setAdapter(pagerAdapter);

        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_staff:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_wx:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_me:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });


        initEvents();
    }

    private void initEvents() {

    }


    @Override
    public void onClick(View view) {

    }

    private void disableShiftMode(BottomNavigationView navigationView) {

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
