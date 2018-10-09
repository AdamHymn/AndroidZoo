package zoo.hymn.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;


/**
 * 倒计时的工具类，Android5.0以下的cannel方法不起作用，此工具类的代码来源与Android5.0的倒计时源码可以解决5.0
 * 以下的cannel不起作用的问题 *
 * 在使用过程中发现，在一个activity或者fragment中开启了计时器，如果倒计时没有完成即退出activity或者fragment
 * ，此时onTick仍然会继续执行
 */
public abstract class CountDownUtil {
	/**
	 * Millis since epoch when alarm should stop.
	 */
	private final long mMillisInFuture;

	/**
	 * The interval in millis that the user receives callbacks
	 */
	private final long mCountdownInterval;

	private long mStopTimeInFuture;

	/**
	 * boolean representing if the timer was cancelled
	 */
	private boolean mCancelled = false;

	/**
	 * @param millisInFuture
	 *            The number of millis in the future from the call to
	 *            {@link #start()} until the countdown is done and
	 *            {@link #onFinish()} is called.
	 * @param countDownInterval
	 *            The interval along the way to receive {@link #onTick(long)}
	 *            callbacks.
	 */
	public CountDownUtil(long millisInFuture, long countDownInterval) {
		mMillisInFuture = millisInFuture;
		mCountdownInterval = countDownInterval;
	}

	/**
	 * Cancel the countdown.
	 */
	public synchronized final void cancel() {
		mCancelled = true;
		mHandler.removeMessages(MSG);
	}

	/**
	 * Start the countdown.
	 */
	public synchronized final CountDownUtil start() {
		mCancelled = false;
		if (mMillisInFuture <= 0) {
			onFinish();
			return this;
		}
		mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
		return this;
	}

	/**
	 * Callback fired on regular interval.
	 * 
	 * @param millisUntilFinished
	 *            The amount of time until finished.
	 */
	public abstract void onTick(long millisUntilFinished);

	/**
	 * Callback fired when the time is up.
	 */
	public abstract void onFinish();

	private static final int MSG = 1;

	// handles counting down
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

				synchronized (CountDownUtil.this) {
					if (mCancelled) {
						return;
					}

					final long millisLeft = mStopTimeInFuture
							- SystemClock.elapsedRealtime();

					if (millisLeft <= 0) {
						onFinish();
					} else if (millisLeft < mCountdownInterval) {
						// no tick, just delay until done
						sendMessageDelayed(obtainMessage(MSG), millisLeft);
					} else {
						long lastTickStart = SystemClock.elapsedRealtime();
						onTick(millisLeft);

						// take into account user's onTick taking time to
						// execute
						long delay = lastTickStart + mCountdownInterval
								- SystemClock.elapsedRealtime();

						// special case: user's onTick took more than interval
						// to
						// complete, skip to next interval
						while (delay < 0) {
                            delay += mCountdownInterval;
                        }

						sendMessageDelayed(obtainMessage(MSG), delay);
					}
				}
		}
	};
}
// }
