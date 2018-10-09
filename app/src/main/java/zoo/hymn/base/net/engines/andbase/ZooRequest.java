package zoo.hymn.base.net.engines.andbase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import zoo.hymn.base.net.callback.ZooCallBack;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.utils.NetUtil;
import zoo.hymn.views.SingleProgressDialog;


/**
 * 网络请求封装类
 */
public class ZooRequest extends HYAbHttpUtil {

    protected ZooCallBack mBack;
    protected Context mcontext;

    public ZooRequest(Context context, ZooCallBack back) {
        super(context);
        this.mBack = back;
        this.mcontext = context;
    }

    /**
     * 后台网络请求
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void getHidden(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag) {
        Log.i("RequestParams", url + params.getParamString());
        mClient.get(url, params, new AbStringHttpResponseListener() {
            @Override
            public void onSuccess(int arg0, String arg1) {
                Log.e("onSuccess:",arg1);
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }

            @Override
            public void onStart() {
                Log.e("onStart:","arg1");
            }

            @Override
            public void onFinish() {
                Log.e("onFinish:","arg1");
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable throwable) {
                Log.e("onFailure:",arg1);
                if (throwable != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + throwable.getMessage());
                    mBack.fail(tag, throwable.getMessage());
                }
            }
        });
    }
    /**
     * 后台网络请求
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void postHidden(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag) {
        Log.i("RequestParams", url + params.getParamString());
        mClient.post(url, params, new AbStringHttpResponseListener() {
            @Override
            public void onSuccess(int arg0, String arg1) {
                Log.e("onSuccess:",arg1);
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }

            @Override
            public void onStart() {
                Log.e("onStart:","arg1");
            }

            @Override
            public void onFinish() {
                Log.e("onFinish:","arg1");
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable throwable) {
                Log.e("onFailure:",arg1);
                if (throwable != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + throwable.getMessage());
                    mBack.fail(tag, throwable.getMessage());
                }
            }
        });
    }
    /**
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void post(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag) {
        Log.i("RequestParams", url + params.getParamString());

        // 网络断网提醒
        try {
            if (!NetUtil.checkNet(mcontext)) {
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
                SingleProgressDialog.getSingleInstance(mcontext).show();
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (arg2 != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + arg2.getMessage());
                    mBack.fail(tag, arg2.getMessage());
                }
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }
        });
    }
    /**
     * 通用类的接口post方法，status与其他的相反，1是成功。0是失败
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void commonPost(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag) {
        Log.i("RequestParams", url + params.getParamString());

        // 网络断网提醒
        try {
            if (!NetUtil.checkNet(mcontext)) {
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
                SingleProgressDialog.getSingleInstance(mcontext,"上传中").show();
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (arg2 != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + arg2.getMessage());
                    mBack.fail(tag, arg2.getMessage());
                }
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                SingleProgressDialog.getSingleInstance(mcontext).dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.FAIL.equals(response.result)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }
        });
    }

    /**
     * @param msg    加载对话框的文字
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void post(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag,
                     final int msg) {
        Log.i("RequestParams", url + params.getParamString());

        // 网络断网提醒
        try {
            if (!NetUtil.checkNet(mcontext)) {
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
                if (arg2 != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + arg2.getMessage());
                    mBack.fail(tag, arg2.getMessage());
                }
                if (msg != 0) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }
        });
    }

    /**
     * @param msg    加载对话框的文字
     * @param url    请求接口链接
     * @param params 接口参数
     * @param tag    标识访问类型
     */
    public void post(final String url,
                     final AbRequestParams params,
                     final Class<? extends BaseResponse> classType,
                     final int tag,
                     final String msg) {
        Log.i("RequestParams", url + params.getParamString());

        // 网络断网提醒
        try {
            if (!NetUtil.checkNet(mcontext)) {
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
                    SingleProgressDialog.getSingleInstance(mcontext, msg).show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(msg)) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (arg2 != null) {
                    Log.e("onFailure:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1 + " " + arg2.getMessage());
                    mBack.fail(tag, arg2.getMessage());
                }
                if (!TextUtils.isEmpty(msg)) {
                    SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                }
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                SingleProgressDialog.getSingleInstance(mcontext, msg).dismiss();
                if (arg1 != null && arg0 == 200) {
                    Logger.json(arg1);
                    Gson gson = new Gson();
                    BaseResponse response = null;
                    try {
                        response = gson
                                .fromJson(arg1, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBack.fail(tag, "数据解析异常");
                        return;
                    }

                    if (response != null) {
                        if (BaseResponse.SUCCESS.equals(response.status)) {
                            mBack.success(tag, response);
                        } else {
                            mBack.fail(tag, response.msg);
                        }
                    } else {
                        mBack.fail(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    mBack.fail(tag, arg1);
                    Log.e("onSuccess:response ==>>", "状态码：" + arg0 + "\n响应内容：" + arg1);
                }
            }
        });
    }

}