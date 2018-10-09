package zoo.hymn.views.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bm.wb.R;
import com.bm.wb.ui.zoo.LoginActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zoo.hymn.utils.ViewUtil;
import zoo.hymn.views.welcome.CirclePageIndicator;

public class GuideAc extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private GuideAdapter mGuideAdapter;
    private int[] images;//显示介绍的图片的id值
    private ArrayList<View> views;
    private CirclePageIndicator mIndicator;
    private RelativeLayout container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fg_welcome_circles);

        mViewPager = (ViewPager) findViewById(R.id.welcome_circle_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.welcome_circle_page_indicator);
        container = (RelativeLayout) findViewById(R.id.rl_container);

        views = new ArrayList<>();
        images = new int[]{R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
        mGuideAdapter = new GuideAdapter(views);
        for (int i = 0; i < images.length; i++) {
            ImageView mImageView = new ImageView(this);
            views.add(mImageView);
        }
        //为mViewPager绑定适配器
        mViewPager.setAdapter(mGuideAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(1);

        //设置指示器属性，并关联viewpager
        mIndicator.setViewPager(mViewPager);
        mIndicator.setSnap(true);
        mIndicator.setStrokeWidth(0f);
        mIndicator.setStrokeColor(Color.YELLOW);
        mIndicator.setPageColor(Color.WHITE);
        mIndicator.setFillColor(getResources().getColor(R.color.colorAccent));
        mIndicator.setOnPageChangeListener(this);

        //添加登录标示
        SharedPreferences sp = getSharedPreferences("guide", MODE_PRIVATE);
        sp.edit().putBoolean("first", false).commit();


    }

    /**
     * 根据登录信息跳转到主界面
     */
    private void goToMain() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e("", "onPageSelected: " + position);
//        handler.sendEmptyMessage(position);
        if (position == images.length - 1) {
            TextView tvWelcome = new TextView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            tvWelcome.setBackground(getResources().getDrawable(R.drawable.bg_public_btn));
            tvWelcome.setText("立即进入");
            tvWelcome.setTextSize(16f);
            tvWelcome.setTextColor(Color.WHITE);
            tvWelcome.setGravity(Gravity.CENTER);
            params.height = (int) ViewUtil.dip2px(this, 44);
            params.width = (int) ViewUtil.dip2px(this, 112);
            params.alignWithParent = true;
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0,0,0,(int) ViewUtil.dip2px(this, 50));
            tvWelcome.setLayoutParams(params);
            tvWelcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMain();
                }
            });
            container.addView(tvWelcome, 0);
            mIndicator.setVisibility(View.INVISIBLE);
        } else {
            mIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class GuideAdapter extends PagerAdapter {
        private List<View> views;
        private final LinkedList<View> recycleBin = new LinkedList<>();

        public GuideAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //在此设置背景图片，提高加载速度，解决OOM问题
            View view;
            int count = getCount();
            if (!recycleBin.isEmpty()) {
                view = recycleBin.pop();
            } else {
                view = views.get(position);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                view.setBackgroundResource(images[position % count]);
                view.setLayoutParams(params);
            }
            container.addView(view, 0);

            return views.get(position);
        }
    }
}
