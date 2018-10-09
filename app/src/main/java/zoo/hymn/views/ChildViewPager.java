package zoo.hymn.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ChildViewPager extends ViewPager {

    private ScrollView scrollView;

    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPagerAndListView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    //如果放在ViewPager或者ListView中需要重写下面三个方法解决滑动冲突
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        scrollView.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        scrollView.requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        scrollView.requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }

}
