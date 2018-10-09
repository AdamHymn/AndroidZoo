package zoo.hymn.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bm.wb.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zoo.hymn.base.handler.BaseHandler;
import zoo.hymn.utils.ViewUtil;

public class BannerView extends LinearLayout {

    private boolean isPlay = false;

    public void setPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public interface onBannerClickListener {
        /**
         * 点击广告
         *
         * @param position 广告的序号
         */
        void onBannerClick(int position);
    }

    public static int mTimeInterval = 3 * 1000;
    public static final int CURSOR_COLOR_NORMAL = 0xffd3d3d3;
    public static final int CUROSR_COLOR_SELECTED = 0xffffaa3d;
    public static final int CURSOR_SPACING = 5;

    private static final int WHAT_SWITCH_BANNER = 100;

    private final List<String> urls = new ArrayList<String>();

    private int colorNormal = CURSOR_COLOR_NORMAL;
    private int colorSelected = CUROSR_COLOR_SELECTED;
    private int cursorSpacing = CURSOR_SPACING;
    private int switchInterval = mTimeInterval;

    private int isOptions;

    private ViewPager viewPager;
    private LinearLayout cursorLayout;
    private final List<ImageView> imageViewList = new ArrayList<ImageView>();
    private final List<ImageView> cursorViewList = new ArrayList<ImageView>();

    private Handler handler;
    private onBannerClickListener bannerClickListener;

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public int getUrlSize(){
        return urls.size();
    }

    public void setTimeInterval(int timeInterval) {
        mTimeInterval = timeInterval;
    }

    public BannerView(Context context) {
        super(context);
        initialize();
    }

    RelativeLayout mLayout;

    private void initialize() {
        handler = new BannerHandler(this);

        mLayout = new RelativeLayout(getContext());
//        int dividerHeight = (int) ViewUtil.dip2px(getContext(), 1);
//        int screenWidth = ViewUtil.getScreenWidthPixels((BaseActivity)getContext());
//        int imgWidth = screenWidth*dividerHeight;
//        int imgHeight = imgWidth*401/751;
        addView(mLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        viewPager = new ViewPager(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mLayout.addView(viewPager, params);
    }

    public void setPointLocation(int paddingBottom) {
        isOptions = paddingBottom;
        cursorLayout = new LinearLayout(getContext());
        int height = (int) ViewUtil.dip2px(getContext(), 30);
        Log.e("height", height + "============");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, height);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
//                RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
                RelativeLayout.TRUE);
        cursorLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        int pading = (int) ViewUtil.dip2px(getContext(), paddingBottom);
        int rightPadd = (int) ViewUtil.dip2px(getContext(), 15);
        cursorLayout.setPadding(0, 0, rightPadd, 0);
        cursorLayout.setLayoutParams(layoutParams);
        mLayout.addView(cursorLayout);
    }

    private ImageAdapter mAdapter = null;

    private void createContents() {
        int currentItem = 0;
        cursorLayout.removeAllViews();
        imageViewList.clear();
        cursorViewList.clear();
        int zixe = (int) ViewUtil.dip2px(getContext(), 10);
        int pading = (int) ViewUtil.dip2px(getContext(), 5);
        if (urls.size() > 0) {
            for (int i = 0; i < urls.size(); i++) {
                createImageAndCursor(urls.get(i), true, zixe, pading);
            }
            if (mAdapter == null) {
                mAdapter = new ImageAdapter();
                viewPager.setAdapter(mAdapter);
                viewPager.setOnPageChangeListener(onPageChangeListener);
                viewPager.setCurrentItem(currentItem);
            } else {
                viewPager.removeAllViews();
                mAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(currentItem);
            }
            for (int i = 0; i < cursorViewList.size(); i++) {
                ImageView cursor = cursorViewList.get(i);
                cursor.destroyDrawingCache();
                if (currentItem == i) {
                    cursor.setImageResource(
                            R.drawable.shape_dot_blue);
                } else {
                    cursor.setImageResource(
                            R.drawable.shape_dot_white);
                }
            }
        } else {
            clearView();
        }

    }

    private void createImageAndCursor(String url, boolean isLast, int size, int padding) {
        Context context = getContext();
        RecRoundImage view = new RecRoundImage(context);
//        ImageView view = new ImageView(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        view.setScaleType(ScaleType.FIT_XY);
        view.setOnClickListener(onImageClickListener);
        imageViewList.add(view);
//        if (isOptions == 5){

//        switch (Integer.parseInt(url)){
//            case 0:
//                try {
//                    Glide.with(context).load(R.drawable.l1).into(view);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case 1:
//                try {
//                    Glide.with(context).load(R.drawable.l2).into(view);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case 2:
//                try {
//                    Glide.with(context).load(R.drawable.l3).into(view);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
        try {
            Glide.with(context).load(url).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView cursor = new ImageView(context);
//        cursor.setImageResource(R.drawable.dot_white);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(padding, 0, padding, 0);
        if (!isLast) {
            lp.rightMargin = cursorSpacing;
        }
        cursor.setLayoutParams(lp);
        cursorViewList.add(cursor);
        cursorLayout.addView(cursor);
    }

    /**
     * 设置图片链接集合
     *
     * @param collection
     */
    public void setImageUrls(Collection<String> collection) {
        urls.clear();
        if (collection != null) {
            urls.addAll(collection);
        }
        createContents();
    }

    private void selectCursor(int position) {
        for (int i = 0; i < cursorViewList.size(); i++) {
            ImageView cursor = cursorViewList.get(i);
            cursor.destroyDrawingCache();
//            cursor.setImageResource(R.drawable.dot_white);
            int size = (int) ViewUtil.dip2px(getContext(), 10);
            int selectSize = (int) ViewUtil.dip2px(getContext(), 15);
            int pading = (int) ViewUtil.dip2px(getContext(), 5);
            if (i == position) {
                LinearLayout.LayoutParams selectLp = new LinearLayout.LayoutParams(selectSize, selectSize);
                selectLp.setMargins(pading, 0, pading, 0);
                cursor.setLayoutParams(selectLp);
                cursor.setImageResource(R.drawable.shape_dot_blue);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMargins(pading, 0, pading, 0);
                cursor.setLayoutParams(lp);
                cursor.setImageResource(R.drawable.shape_dot_white);
            }
        }

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            play();
        } else {
            pause();
        }
    }

    /**
     * 获取广告监听器
     *
     * @return
     */
    public onBannerClickListener getOnBannerClickListener() {
        return bannerClickListener;
    }

    /**
     * 设置广告监听器
     *
     * @param onBannerClickListener
     */
    public void setOnBannerClickListener(
            onBannerClickListener onBannerClickListener) {
        this.bannerClickListener = onBannerClickListener;
    }

    class ImageAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(imageViewList.get(position));
            return imageViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (imageViewList.size() - 1 >= position) {
                ImageView view = imageViewList.get(position);
                ((ViewPager) container).removeView(view);
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            synchronized (urls) {
                return imageViewList.size();
            }
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private OnClickListener onImageClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            pause();
            if (bannerClickListener != null) {
                bannerClickListener.onBannerClick(viewPager.getCurrentItem());
            }
            play();
        }
    };

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            selectCursor(arg0);
            pause();
            play();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    public void play() {
        if (isPlay) {
            handler.sendEmptyMessageDelayed(WHAT_SWITCH_BANNER, switchInterval);
        }
    }

    public void pause() {
        handler.removeMessages(WHAT_SWITCH_BANNER);
    }

    static class BannerHandler extends BaseHandler<BannerView> {

        public BannerHandler(BannerView r) {
            super(r);
        }

        @Override
        public void handleMessage(BannerView r, Message msg) {
            if (msg.what == WHAT_SWITCH_BANNER) {
                int index = r.viewPager.getCurrentItem();
                if (index == r.urls.size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
                r.viewPager.setCurrentItem(index, true);
                r.handler.sendEmptyMessageDelayed(WHAT_SWITCH_BANNER,
                        r.switchInterval);
            }
        }
    }

    /**
     * @return the switchInterval
     */
    public int getSwitchInterval() {
        return switchInterval;
    }

    /**
     * @param switchInterval the switchInterval to set
     */
    public void setSwitchInterval(int switchInterval) {
        this.switchInterval = switchInterval;
    }

    public void clearView() {
        pause();
        urls.clear();
        this.cursorViewList.clear();
        this.imageViewList.clear();
        if (viewPager != null) {
            viewPager.destroyDrawingCache();
            viewPager.removeAllViews();
            viewPager = null;
        }

        if (cursorLayout != null) {
            cursorLayout.destroyDrawingCache();
            cursorLayout.removeAllViews();
            cursorLayout = null;
        }
    }
}
