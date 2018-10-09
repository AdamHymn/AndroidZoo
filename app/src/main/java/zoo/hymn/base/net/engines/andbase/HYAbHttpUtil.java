package zoo.hymn.base.net.engines.andbase;

/**
 * ClassName: HYAbHttpUtil
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/12
 */


import android.content.Context;

import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbFileHttpResponseListener;
import com.ab.http.AbHttpResponseListener;
import com.ab.http.AbJsonParams;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;

import zoo.hymn.base.net.engines.andbase.HYAbHttpClient;


public class HYAbHttpUtil {
    protected static HYAbHttpClient mClient = null;

    public HYAbHttpUtil(Context context) {
        mClient = new HYAbHttpClient(context);
    }

    public void getWithoutThread(String url, AbStringHttpResponseListener responseListener) {
        mClient.getWithoutThread(url, (AbRequestParams)null, responseListener);
    }

    public void getWithoutThread(String url, AbRequestParams params, AbStringHttpResponseListener responseListener) {
        mClient.getWithoutThread(url, params, responseListener);
    }

    public void postWithoutThread(String url, AbStringHttpResponseListener responseListener) {
        mClient.postWithoutThread(url, (AbRequestParams)null, responseListener);
    }

    public void postWithoutThread(String url, AbRequestParams params, AbStringHttpResponseListener responseListener) {
        mClient.postWithoutThread(url, params, responseListener);
    }

    public void get(String url, AbHttpResponseListener responseListener) {
        mClient.get(url, (AbRequestParams)null, responseListener);
    }

    public void get(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        mClient.get(url, params, responseListener);
    }

    public void get(String url, AbBinaryHttpResponseListener responseListener) {
        mClient.get(url, (AbRequestParams)null, responseListener);
    }

    public void get(String url, AbRequestParams params, AbFileHttpResponseListener responseListener) {
        mClient.get(url, params, responseListener);
    }

    public void getWithCache(String url, AbHttpResponseListener responseListener) {
        mClient.getWithCache(url, (AbRequestParams)null, responseListener);
    }

    public void getWithCache(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        mClient.getWithCache(url, params, responseListener);
    }

    public void getWithCache(String url, AbBinaryHttpResponseListener responseListener) {
        mClient.getWithCache(url, (AbRequestParams)null, responseListener);
    }

    public void getWithCache(String url, AbRequestParams params, AbFileHttpResponseListener responseListener) {
        mClient.getWithCache(url, params, responseListener);
    }

    public void post(String url, AbHttpResponseListener responseListener) {
        mClient.post(url, (AbRequestParams)null, responseListener);
    }

    public void post(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        mClient.post(url, params, responseListener);
    }

    public void post(String url, AbRequestParams params, AbHttpResponseListener responseListener, String headerKey, String headerValue) {
        mClient.post(url, params, responseListener, headerKey, headerValue);
    }

    public void post(String url, AbRequestParams params, AbFileHttpResponseListener responseListener) {
        mClient.post(url, params, responseListener);
    }

    public void postJson(String url, AbJsonParams params, AbStringHttpResponseListener responseListener) {
        mClient.postJson(url, params, responseListener);
    }

    public void request(String url, AbStringHttpResponseListener responseListener) {
        request(url, (AbRequestParams)null, responseListener);
    }

    public void request(String url, AbRequestParams params, AbStringHttpResponseListener responseListener) {
        mClient.doRequest(url, params, responseListener);
    }

    public void setTimeout(int timeout) {
        mClient.setTimeout(timeout);
    }

    public void setEasySSLEnabled(boolean enabled) {
        mClient.setOpenEasySSL(enabled);
    }

    public void setEncode(String encode) {
        mClient.setEncode(encode);
    }

    public void setUserAgent(String userAgent) {
        mClient.setUserAgent(userAgent);
    }

    public static void shutdownHttpClient() {
        if(mClient != null) {
            mClient.shutdown();
            mClient = null;
        }

    }

    public long getCacheMaxAge() {
        return mClient.getCacheMaxAge();
    }

    public void setCacheMaxAge(long cacheMaxAge) {
        mClient.setCacheMaxAge(cacheMaxAge);
    }
}

