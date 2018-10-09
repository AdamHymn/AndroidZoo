package zoo.hymn.base.net.callback;

import android.view.View;

import zoo.hymn.base.net.response.base.BaseResponse;


public interface BaseCallBack extends View.OnClickListener{
	void noNet(int tag);
	void noData(int tag);
	void netExc(int tag);
	void fail(int tag,BaseResponse response);
}
