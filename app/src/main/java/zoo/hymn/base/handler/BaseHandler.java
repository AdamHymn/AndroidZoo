package zoo.hymn.base.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.SoftReference;

/**
 * 
 * 防止内存泄漏的预处理
 */
public class BaseHandler<T> extends Handler {

  private final SoftReference<T> reference;

  public BaseHandler(T r) {
    this.reference = new SoftReference<T>(r);
  }

  public BaseHandler(T r, Callback callback) {
    super(callback);
    this.reference = new SoftReference<T>(r);
  }

  public BaseHandler(T r, Looper looper) {
    super(looper);
    this.reference = new SoftReference<T>(r);
  }

  public BaseHandler(T r, Looper looper, Callback callback) {
    super(looper, callback);
    this.reference = new SoftReference<T>(r);
  }

  @Override
  public final void handleMessage(Message msg) {
    T r = reference.get();
    if (r != null) {
      handleMessage(r, msg);
    }
  }

  public void handleMessage(T r, Message msg) {
  }

}
