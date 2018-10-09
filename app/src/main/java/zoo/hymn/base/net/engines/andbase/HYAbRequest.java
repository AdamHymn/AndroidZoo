package zoo.hymn.base.net.engines.andbase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import zoo.hymn.base.net.callback.BaseCallBack;
import zoo.hymn.base.net.callback.BaseDataCallBack;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.net.response.base.VingaResponse;
import zoo.hymn.views.SingleProgressDialog;


/**
 * 网络请求封装类
 */
public class HYAbRequest extends HYAbHttpUtil {

    /**
     * 同一页面，多个网络请求标识
     */
    public static final int NET0 = 0;
    public static final int NET1 = 1;
    public static final int NET2 = 2;
    public static final int NET3 = 3;
    public static final int NET4 = 4;
    public static final int NET5 = 5;
    public static final int NET6 = 6;
    public static final int NET7 = 7;
    public static final int NET8 = 8;
    public static final int NET9 = 9;

    protected BaseCallBack mBack;
    protected Context mcontext;
    protected SingleProgressDialog singleProgressDialog;

    public HYAbRequest(Context context, BaseCallBack back) {
        super(context);
        this.mBack = back;
        this.mcontext = context;
    }

    /**
     * @param msg       加载对话框的文字
     * @param url       请求接口链接
     * @param params    接口参数
     * @param tag       标识访问类型
     * @param classType 返回 值类型也是json模型 void
     *                  <p>
     *                  如果不是同时请求多个接口，就用此方法
     */
    public void vingaPost(final String url,
                          final AbRequestParams params,
                          final Class<? extends VingaResponse> classType,
                          final int tag,
                          final int msg) {
        Log.i("RequestParams", url + params.getParamString());
        // 网络断网提醒
        try {
            if (!isNetworkAvailable(mcontext)) {
                mBack.noNet(tag);
                Toast.makeText(mcontext, "无网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClient.post(url, params, new AbStringHttpResponseListener() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                }
                if (arg2 != null) {
                    Toast.makeText(mcontext, arg2.getMessage(), Toast.LENGTH_SHORT).show();
                    BaseResponse response = new BaseResponse();
                    response.msg = arg2.getMessage();
                    mBack.fail(tag, response);
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    //排查后台人员调试接口造成的异常
                    if (arg1.contains("[status] =>")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "正在调试接口,请联系客服...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    // 处理接口异常
                    if (arg1.contains("error</b>:")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "接口异常...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    Gson gson = new Gson();
                    VingaResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.msg = "数据解析异常,请联系客服...";
                        mBack.fail(tag, baseResponse);
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.Data.status)) {
                            if (mBack instanceof BaseDataCallBack) {
                                ((BaseDataCallBack) mBack).success(tag, arg1);
                            } else {
                                BaseResponse baseResponse = new BaseResponse();
                                baseResponse.msg = "请使用DataCallback回调方法获取响应内容！";
                                mBack.fail(tag, baseResponse);
                            }
                        } else {
                            BaseResponse baseResponse = new BaseResponse();
                            baseResponse.msg = response.Data.msg;
                            mBack.fail(tag, baseResponse);
                        }
                    } else {
                        mBack.netExc(tag);
                    }
                } else {
                    Log.e("response string ==>>", arg1);
                }
            }
        });
    }

    /**
     * @param msg       加载对话框的文字
     * @param url       请求接口链接
     * @param params    接口参数
     * @param tag       标识访问类型
     * @param classType 返回 值类型也是json模型 void
     *                  <p>
     *                  如果不是同时请求多个接口，就用此方法
     */
    public void vingaPost(final String url,
                          final AbRequestParams params,
                          final Class<? extends VingaResponse> classType,
                          final int tag,
                          final String msg) {
        Log.i("RequestParams", url + "?" + params.getParamString());
        // 网络断网提醒
        try {
            if (!isNetworkAvailable(mcontext)) {
                mBack.noNet(tag);
                Toast.makeText(mcontext, "无网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClient.post(url, params, new AbStringHttpResponseListener() {


            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog = SingleProgressDialog.getSingleInstance(mcontext, msg);
                    singleProgressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog.dismiss();
                }
                if (arg2 != null) {
                    Log.e("====onFailure=arg1=", "onFailure: " + arg1);
                    Log.e("====onFailure=arg2=", "onFailure: " + arg2.getMessage());
//					Toast.makeText(mcontext, arg2.getMessage(),Toast.LENGTH_SHORT).show();
                    BaseResponse response = new BaseResponse();
                    response.msg = arg2.getMessage();
                    mBack.fail(tag, response);
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                singleProgressDialog.dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    //排查后台人员调试接口造成的异常
                    if (arg1.contains("[status] =>")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "正在调试接口,请联系客服...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    // 处理接口异常
                    if (arg1.contains("error</b>:")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "接口异常...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    Gson gson = new Gson();
                    VingaResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.msg = "数据解析异常,请联系客服...";
                        mBack.fail(tag, baseResponse);
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.Data.status)) {
                            if (mBack instanceof BaseDataCallBack) {
                                ((BaseDataCallBack) mBack).success(tag, arg1);
                            } else {
                                BaseResponse baseResponse = new BaseResponse();
                                baseResponse.msg = "请使用DataCallback回调方法获取响应内容！";
                                mBack.fail(tag, baseResponse);
                            }
                        } else {
                            BaseResponse baseResponse = new BaseResponse();
                            baseResponse.msg = response.Data.msg;
                            mBack.fail(tag, baseResponse);
                        }
                    } else {
                        mBack.netExc(tag);
                    }
                } else {
                    Log.e("response string ==>>", arg1);
                }
            }
        });
    }

    /**
     * @param msg       加载对话框的文字
     * @param url       请求接口链接
     * @param params    接口参数
     * @param tag       标识访问类型
     * @param classType 返回 值类型也是json模型 void
     *                  <p>
     *                  如果不是同时请求多个接口，就用此方法
     */
    public void basePost(final String url,
                         final AbRequestParams params,
                         final Class<? extends BaseResponse> classType,
                         final int tag,
                         final String msg) {
        Log.i("RequestParams", url + "?" + params.getParamString());
        // 网络断网提醒
        try {
            if (!isNetworkAvailable(mcontext)) {
                mBack.noNet(tag);
                Toast.makeText(mcontext, "无网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClient.post(url, params, new AbStringHttpResponseListener() {


            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog = SingleProgressDialog.getSingleInstance(mcontext, msg);
                    singleProgressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    singleProgressDialog.dismiss();
                }
                if (arg2 != null) {
                    Log.e("====onFailure=arg1=", "onFailure: " + arg1);
                    Log.e("====onFailure=arg2=", "onFailure: " + arg2.getMessage());
//					Toast.makeText(mcontext, arg2.getMessage(),Toast.LENGTH_SHORT).show();
                    BaseResponse response = new BaseResponse();
                    response.msg = arg2.getMessage();
                    mBack.fail(tag, response);
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                singleProgressDialog.dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    //排查后台人员调试接口造成的异常
                    if (arg1.contains("[status] =>")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "正在调试接口,请联系客服...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    // 处理接口异常
                    if (arg1.contains("error</b>:")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "接口异常...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response = new BaseResponse();
                        response.msg = "数据解析异常,请联系客服...";
                        mBack.fail(tag, response);
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            if (mBack instanceof BaseDataCallBack) {
                                ((BaseDataCallBack) mBack).success(tag, response);
                            } else {
                                try {
                                    throw new Exception("请使用DataCallback回调方法获取响应内容！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mBack.fail(tag, response);
                            }
                        } else {
                            mBack.fail(tag, response);
                        }
                    } else {
                        mBack.netExc(tag);
                    }
                } else {
                    Log.e("response string ==>>", arg1);
                }
            }
        });
    }

    /**
     * @param msg       加载对话框的文字
     * @param url       请求接口链接
     * @param params    接口参数
     * @param tag       标识访问类型
     * @param classType 返回 值类型也是json模型 void
     *                  <p>
     *                  如果不是同时请求多个接口，就用此方法
     */
    public void basePost(final String url,
                         final AbRequestParams params,
                         final Class<? extends BaseResponse> classType,
                         final int tag,
                         final int msg) {
        Log.i("RequestParams", url + "?" + params.getParamString());
        // 网络断网提醒
        try {
            if (!isNetworkAvailable(mcontext)) {
                mBack.noNet(tag);
                Toast.makeText(mcontext, "无网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClient.post(url, params, new AbStringHttpResponseListener() {


            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    singleProgressDialog = SingleProgressDialog.getSingleInstance(mcontext, msg);
                    singleProgressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    singleProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (msg != 0) {
                    singleProgressDialog.dismiss();
                }
                if (arg2 != null) {
                    Log.e("====onFailure=arg1=", "onFailure: " + arg1);
                    Log.e("====onFailure=arg2=", "onFailure: " + arg2.getMessage());
//					Toast.makeText(mcontext, arg2.getMessage(),Toast.LENGTH_SHORT).show();
                    BaseResponse response = new BaseResponse();
                    response.msg = arg2.getMessage();
                    mBack.fail(tag, response);
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                singleProgressDialog.dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    //排查后台人员调试接口造成的异常
                    if (arg1.contains("[status] =>")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "正在调试接口,请联系客服...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    // 处理接口异常
                    if (arg1.contains("error</b>:")) {
                        BaseResponse response = new BaseResponse();
                        response.msg = "接口异常...";
                        mBack.fail(tag, response);
                        Log.e("error", arg1);
                        return;
                    }
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response = new BaseResponse();
                        response.msg = "数据解析异常,请联系客服...";
                        mBack.fail(tag, response);
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            if (mBack instanceof BaseDataCallBack) {
                                ((BaseDataCallBack) mBack).success(tag, response);
                            } else {
                                try {
                                    throw new Exception("请使用DataCallback回调方法获取响应内容！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mBack.fail(tag, response);
                            }
                        } else {
                            mBack.fail(tag, response);
                        }
                    } else {
                        mBack.netExc(tag);
                    }
                } else {
                    Log.e("response string ==>>", arg1);
                }
            }
        });
    }

    /**
     * 网络判断
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}