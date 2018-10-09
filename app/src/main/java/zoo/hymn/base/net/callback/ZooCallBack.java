package zoo.hymn.base.net.callback;

import android.view.View;

import zoo.hymn.base.net.response.base.BaseResponse;


/**
 * ClassName: DataCallBack
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/7/25
 */

public interface ZooCallBack extends View.OnClickListener{
    void noNet(int tag);
    void noData(int tag);
    void fail(int tag,String error);
    void success(int tag,BaseResponse response);
}
