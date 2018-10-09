package zoo.hymn.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


/**
 * 继承 CountDownTimer 防范 重写 父类的方法 onTick() 、 onFinish() 倒计时
 * 
 * 获取验证码倒计时方法
 */

public class XCountDownTimer extends CountDownUtil {
	private Context context;
	private TextView tv;
	private TextView tv2;
	private OnClickListener onClick;

	/**
	 * 
	 * @param millisInFuture
	 *            表示以毫秒为单位 倒计时的总数
	 * 
	 *            例如 millisInFuture=1000 表示1秒
	 * 
	 * @param countDownInterval
	 *            表示 间隔 多少微秒 调用一次 onTick 方法
	 * 
	 *            例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
	 * 
	 */
	public XCountDownTimer(TextView tv, long millisInFuture,
						   long countDownInterval) {
		super(millisInFuture, countDownInterval);
		this.tv = tv;
	}

	/**
	 * 
	 * @param millisInFuture
	 *            表示以毫秒为单位 倒计时的总数
	 * 
	 *            例如 millisInFuture=1000 表示1秒
	 * 
	 * @param countDownInterval
	 *            表示 间隔 多少微秒 调用一次 onTick 方法
	 * 
	 *            例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
	 * 
	 */
	public XCountDownTimer(Context context,TextView tv, long millisInFuture,
						   long countDownInterval, OnClickListener click) {
		super(millisInFuture, countDownInterval);
		this.onClick = click;
		this.tv = tv;
		this.context = context;
	}
	public XCountDownTimer(Context context,TextView tv,TextView tv2, long millisInFuture,
						   long countDownInterval, OnClickListener click) {
		super(millisInFuture, countDownInterval);
		this.onClick = click;
		this.tv = tv;
		this.tv2 = tv2;
		this.context = context;
	}

	@Override
	public void onFinish() {
		if (onClick != null) {
			tv.setOnClickListener(onClick);
		}
//		tv.setText("获取验证码");
//		tv.setBackground(context.getResources().getDrawable(R.drawable.btn_blue_round));
//		LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//		lp.setMargins((int)ViewUtil.dip2px(context,10.f),0,0,0);
//		lp.weight = 0.7f;
//		tv.setLayoutParams(lp);
//		tv.setSingleLine(true);
//		tv.setGravity(Gravity.CENTER);
		tv.setVisibility(View.VISIBLE);
		tv2.setVisibility(View.GONE);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		if (onClick != null) {
			tv.setOnClickListener(null);
//			tv.setBackground(context.getResources().getDrawable(R.drawable.btn_gray));
//			LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//			lp.setMargins((int)ViewUtil.dip2px(context,10.f),0,0,0);
//			lp.weight = 0.7f;
//			tv.setLayoutParams(lp);
//			tv.setGravity(Gravity.CENTER);
			tv2.setVisibility(View.VISIBLE);
			tv.setVisibility(View.GONE);
		}
		tv2.setText("(" + millisUntilFinished / 1000 + "s)");
	}
}
