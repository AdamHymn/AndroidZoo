package zoo.hymn.base.net.callback;


/**
 * 请求网络完成回调接口
 */
public interface BaseDataCallBack<T> extends BaseCallBack {
	void success(int tag, T response);
}
