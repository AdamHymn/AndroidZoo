package zoo.hymn.base.net.engines.okhttp;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bm.wb.ui.zoo.LoginActivity;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zoo.hymn.base.net.callback.ZooCallBack;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.net.response.base.BaseStringResponse;
import zoo.hymn.utils.NetUtil;
import zoo.hymn.utils.SharedPreferencesUtil;
import zoo.hymn.utils.StrUtil;
import zoo.hymn.views.SingleProgressDialog;

import static zoo.hymn.ZooConstant.URL_MEDIA;

/**
 * ClassName: OkHttpUtil
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/17
 */

public class OkHttpUtil {

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

    public static final String TAG = "OkHttpUtil";
    public static OkHttpClient mClient = null;
    protected ZooCallBack mBack;
    protected Context mContext;
    private Handler mDelivery;
    private Gson mGson;

    public OkHttpUtil(Context context, ZooCallBack back) {
        this.mBack = back;
        this.mContext = context;
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /**
     * post请求 （参数封装到map对象里）
     *
     * @param url
     * @param params
     * @param classType
     * @param tag
     * @param loading
     */
    public void postMap(String url, Map<String, String> params, final Class<? extends BaseResponse> classType, final int tag, final int loading) {
        postWithToken(url, Map2OK3Adapter(params), classType, tag, loading);
    }

    /**
     * post请求 （参数封装到json对象里）
     *
     * @param url
     * @param paramBean
     * @param classType
     * @param tag
     * @param loading
     */
    public void postJson(String url, Object paramBean, final Class<? extends BaseResponse> classType, final int tag, final int loading) {
        String json = mGson.toJson(paramBean);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        postWithToken(url, requestBody, classType, tag, loading);
    }

    /**
     * post请求按角色 （参数封装到map对象里）
     *
     * @param url
     * @param params
     * @param classType
     * @param tag
     * @param loading
     */
    public void postMapByRole(String url, Map<String, String> params, final Class<? extends BaseResponse> classType, final int tag, final int loading) {
        postWithToken(urlWithRole(url), Map2OK3Adapter(params), classType, tag, loading);
    }

    /**
     * post请求按角色 （参数封装到json对象里）
     *
     * @param url
     * @param paramBean
     * @param classType
     * @param tag
     * @param loading
     */
    public void postJsonByRole(String url, Object paramBean, final Class<? extends BaseResponse> classType, final int tag, final int loading) {
        String json = mGson.toJson(paramBean);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        postWithToken(urlWithRole(url), requestBody, classType, tag, loading);
    }


    /**
     * 私有方法 添加token的post请求
     *
     * @param url
     * @param requestBody
     * @param classType
     * @param tag
     * @param loading
     */
    private void postWithToken(String url, RequestBody requestBody, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("authorization", SharedPreferencesUtil.get(mContext, "TOKEN"))
                .post(requestBody)
                .build();

        doCall(request, classType, tag, loading);
    }


    /**
     * 私有方法 post请求
     *
     * @param url
     * @param requestBody
     * @param classType
     * @param tag
     * @param loading
     */
    private void post(String url, RequestBody requestBody, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        doCall(request, classType, tag, loading);
    }


    /**
     * 添加token的get请求
     *
     * @param url
     * @param classType
     * @param tag
     * @param loading
     */
    public void get(String url, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        Request request = new Request.Builder()
                .addHeader("authorization", SharedPreferencesUtil.get(mContext, "TOKEN"))
                .url(url)
                .get()
                .build();
        doCall(request, classType, tag, loading);
    }

    /**
     * 添加token的get请求
     *
     * @param url
     * @param classType
     * @param tag
     * @param loading
     */
    public void getByRole(String url, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        Request request = new Request.Builder()
                .addHeader("authorization", SharedPreferencesUtil.get(mContext, "TOKEN"))
                .url(urlWithRole(url))
                .get()
                .build();
        doCall(request, classType, tag, loading);
    }

    /**
     * 上传单个文件
     *
     * @param url
     * @param key
     * @param file
     * @param classType
     * @param tag
     * @param loading
     */
    public void uploadFile(String url, String key, File file, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        RequestBody fileBody = RequestBody.create(MediaType.parse(getMimeType(file.getName())), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), fileBody)
                .build();
        postWithToken(url, requestBody, classType, tag, loading);
    }

    /**
     * 批量上传多个文件，可带map参数
     *
     * @param url
     * @param files
     * @param fileKeys
     * @param params
     * @param classType
     * @param tag
     * @param loading
     */
    public void uploadFiles(String url, File[] files, String[] fileKeys, Map<String, String> params, final Class<? extends BaseResponse> classType, final int tag, final int loading) {
        doCall(buildMultipartFormRequest(url, files, fileKeys, params), classType, tag, loading);
    }

    /**
     * 下载文件
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public void downLoadFile(String fileUrl, String fileName, final String destFileDir, final int tag) {
        final BaseStringResponse baseResponse = new BaseStringResponse();
        baseResponse.msg = "success";
        baseResponse.status = "1";
        baseResponse.data = "";
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            successCallback(tag, baseResponse);
            return;
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedCallback(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    successCallback(tag, baseResponse);
                } catch (IOException e) {
                    failedCallback(tag, e.getMessage());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        failedCallback(tag, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 执行网络请求，处理回调方法
     *
     * @param request
     * @param classType
     * @param tag
     * @param loading
     */
    private void doCall(Request request, final Class<? extends BaseResponse> classType, final int tag, final int loading) {

        // 网络断网提醒
        try {
            if (!NetUtil.checkNet(mContext)) {
                mBack.noNet(tag);
                Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SingleProgressDialog.getSingleInstance(mContext, loading).show();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                call.cancel();
                failedCallback(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Logger.json(json);
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    BaseResponse baseResponse = null;

                    /*** 特殊处理*/
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int status = (int) jsonObject.get("status");
                        if (status == 0) {
                            failedCallback(tag, (String) jsonObject.get("msg"));
                            return;
                        }
                        if (status == 2) {
                            if (mContext != null) {
                                failedCallback(tag, (String) jsonObject.get("msg"));
                                try {
                                    LitePal.getDatabase().execSQL("delete from LoginBean");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                JPushInterface.setAliasAndTags(mContext.getApplicationContext(), "", null, null);
                                RongIM.getInstance().logout();
                                mContext.startActivity(new Intent(mContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        baseResponse = gson
                                .fromJson(json, classType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failedCallback(tag, "数据解析异常");
                        return;
                    }

                    if (baseResponse != null) {
                        if (BaseResponse.SUCCESS.equals(baseResponse.status)) {
                            successCallback(tag, baseResponse);
                        } else {
                            failedCallback(tag, baseResponse.msg);
                        }
                    } else {
                        failedCallback(tag, "请求成功，但响应内容为空");
                    }
                } else {
                    failedCallback(tag, json);
                }
            }
        });
    }

    public void doGetByNative(String url, Callback call) {
        Request request = new Request.Builder()
                .addHeader("authorization", SharedPreferencesUtil.get(mContext, "TOKEN"))
                .url(URL_MEDIA + url)
                .get()
                .build();
        mClient.newCall(request).enqueue(call);
    }

    /**
     * 将map表单转换成OKhttp3的RequestBody,解耦网络库
     *
     * @param params
     * @return
     */
    private RequestBody Map2OK3Adapter(Map<String, String> params) {
        FormBody.Builder body = new FormBody.Builder();
        if (params == null) {
            return body.build();
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        Logger.i(TAG, stringBuffer.toString());
        return body.build();
    }

    /**
     * 将role拼接到url
     *
     * @param url
     * @return
     */
    private String urlWithRole(String url) {
//        LoginBean loginBean = DataSupport.findFirst(LoginBean.class);
//        return API + loginBean.userPart + "/" + loginBean.role + url;
        return "roooo";
    }

    /**
     * POST上传文件构建参数
     */
    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Map<String, String> params) {
        String token = "";
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if ("TOKEN".equals(entry.getKey())) {
                    token = entry.getValue();
                } else {
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, entry.getValue()));
                }
            }
        }

        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(getMimeType(fileName)), file);
                //根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);

            }
        }
//        Logger.i(TAG, url + "\n" + builder.toString());
        RequestBody requestBody = builder.build();
        if (StrUtil.isEmpty(token)) {
            token = SharedPreferencesUtil.get(mContext, "TOKEN");
        } else {
            token = "Bearer " + token;
        }
        return new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .post(requestBody)
                .build();
    }


    /**
     * 获取指定文件名的 mime 类型
     *
     * @param path
     * @return
     */
    private String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 失败回调切到主线程
     */
    private void failedCallback(final int tag, final String error) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (mContext != null && mBack != null) {
                    Log.e("onFailure:fail ==>>", error);
                    mBack.fail(tag, error);
                    SingleProgressDialog.getSingleInstance(mContext).dismiss();
                }
            }
        });
    }

    /**
     * 成功回调切到主线程
     */
    private void successCallback(final int tag, final BaseResponse baseResponse) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (mContext != null && mBack != null) {
                    mBack.success(tag, baseResponse);
                    SingleProgressDialog.getSingleInstance(mContext).dismiss();
                }
            }
        });
    }
}
