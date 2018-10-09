package com.hymn.zoo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 描述 ： 可以控制是否左右滑动的viewpager
 * @author think
 *
 */
public class CustemViewPager extends ViewPager {

	/** The enabled. */
	private boolean enabled;
	
	/**
	 * Instantiates a new super un slide view pager.
	 *
	 * @param context the context
	 */
	public CustemViewPager(Context context) {
		super(context);
		this.enabled = true;
	}
	
	/**
	 * Instantiates a new super un slide view pager.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CustemViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = true;
	}

	/**
	 * 描述：触摸没有反应就可以了.
	 *
	 * @param event the event
	 * @return true, if successful
	 * @see ViewPager#onTouchEvent(MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	/**
	 * 描述：TODO.
	 *
	 * @param event the event
	 * @return true, if successful
	 * @see ViewPager#onInterceptTouchEvent(MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	/**
	 * Sets the paging enabled.
	 *
	 * @param enabled the new paging enabled
	 */
	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
