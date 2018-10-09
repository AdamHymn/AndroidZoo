package zoo.hymn.base.net.engines.andbase;

/**
 * ClassName: HYAbHttpClient
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/12
 */


import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ab.cache.AbCacheHeaderParser;
import com.ab.cache.AbCacheResponse;
import com.ab.cache.AbCacheUtil;
import com.ab.cache.AbDiskBaseCache;
import com.ab.cache.AbDiskCache;
import com.ab.cache.http.AbHttpBaseCache;
import com.ab.global.AbAppConfig;
import com.ab.global.AbAppException;
import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbFileHttpResponseListener;
import com.ab.http.AbGzipDecompressingEntity;
import com.ab.http.AbHttpResponseListener;
import com.ab.http.AbJsonParams;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.http.entity.MultipartEntity;
import com.ab.http.entity.mine.content.StringBody;
import com.ab.http.entity.mine.content.WriteByteListener;
import com.ab.http.ssl.EasySSLProtocolSocketFactory;
import com.ab.image.AbImageLoader;
import com.ab.task.thread.AbThreadFactory;
import com.ab.util.AbAppUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLHandshakeException;

import zoo.hymn.utils.FileUtil;


public class HYAbHttpClient {
    private static Context mContext;
    public static Executor mExecutorService = null;
    private String encode = "UTF-8";
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 BIDUBrowser/6.x Safari/537.31";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String USER_AGENT = "User-Agent";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private CookieStore mCookieStore;
    private long cacheMaxAge;
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int FAILURE_MESSAGE_CONNECT = 2;
    protected static final int FAILURE_MESSAGE_SERVICE = 3;
    protected static final int START_MESSAGE = 4;
    protected static final int FINISH_MESSAGE = 5;
    protected static final int PROGRESS_MESSAGE = 6;
    protected static final int RETRY_MESSAGE = 7;
    private int mTimeout = 10000;
    private boolean mIsOpenEasySSL = true;
    private DefaultHttpClient mHttpClient = null;
    private HttpContext mHttpContext = null;
    private AbHttpBaseCache httpCache;
    private AbDiskBaseCache diskCache;
    private HttpRequestRetryHandler mRequestRetryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 2) {
                AbLogUtil.d(HYAbHttpClient.mContext, "超过最大重试次数，不重试");
                return false;
            } else if (exception instanceof NoHttpResponseException) {
                AbLogUtil.d(HYAbHttpClient.mContext, "服务器丢掉了连接，重试");
                return true;
            } else if (exception instanceof SSLHandshakeException) {
                AbLogUtil.d(HYAbHttpClient.mContext, "ssl 异常 不重试");
                return false;
            } else {
                HttpRequest request = (HttpRequest) context.getAttribute("http.request");
                boolean idempotent = request instanceof HttpEntityEnclosingRequest;
                if (!idempotent) {
                    AbLogUtil.d(HYAbHttpClient.mContext, "请求被认为是幂等的，重试");
                    return true;
                } else {
                    return exception != null;
                }
            }
        }
    };

    public HYAbHttpClient(Context context) {
        mContext = context;
        mExecutorService = AbThreadFactory.getExecutorService();
        this.mHttpContext = new BasicHttpContext();
        this.cacheMaxAge = AbAppConfig.DISK_CACHE_EXPIRES_TIME;
        PackageInfo info = AbAppUtil.getPackageInfo(context);
        File cacheDir = null;
        if (!AbFileUtil.isCanUseSD()) {
            cacheDir = new File(context.getCacheDir(), info.packageName);
        } else {
//            cacheDir = new File(AbFileUtil.getCacheDownloadDir(context));
            cacheDir = new File(context.getExternalCacheDir(),"net");
        }

        this.httpCache = AbHttpBaseCache.getInstance();
        this.diskCache = new AbDiskBaseCache(cacheDir);
    }

    public void getWithoutThread(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        try {
            responseListener.onStart();
            if (!AbAppUtil.isNetworkAvailable(mContext)) {
                Thread.sleep(200L);
                responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            if (params != null) {
                if (url.indexOf("?") == -1) {
                    url = url + "?";
                }

                url = url + params.getParamString();
            }

            HttpGet e = new HttpGet(url);
            e.addHeader("User-Agent", this.userAgent);
            e.addHeader("Accept-Encoding", "gzip");
            DefaultHttpClient httpClient = this.getHttpClient();
            httpClient.execute(e, new HYAbHttpClient.RedirectionResponseHandler2(url, responseListener), this.mHttpContext);
        } catch (Exception var6) {
            var6.printStackTrace();
            responseListener.onFailure(900, var6.getMessage(), new AbAppException(var6));
        }

    }

    public void postWithoutThread(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        try {
            responseListener.onStart();
            if (!AbAppUtil.isNetworkAvailable(mContext)) {
                Thread.sleep(200L);
                responseListener.onFailure(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            HttpPost e = new HttpPost(url);
            e.addHeader("User-Agent", this.userAgent);
            e.addHeader("Accept-Encoding", "gzip");
            boolean isContainFile = false;
            if (params != null) {
                HttpEntity httpClient = params.getEntity();
                e.setEntity(httpClient);
                if (params.getFileParams().size() > 0) {
                    isContainFile = true;
                }
            }

            DefaultHttpClient httpClient1 = this.getHttpClient();
            if (isContainFile) {
                e.addHeader("connection", "keep-alive");
                e.addHeader("Content-Type", "multipart/form-data1; boundary=" + params.boundaryString());
                AbLogUtil.i(mContext, "[HTTP POST]:" + url + ",包含文件域!");
            }

            httpClient1.execute(e, new HYAbHttpClient.RedirectionResponseHandler2(url, responseListener), this.mHttpContext);
        } catch (Exception var7) {
            var7.printStackTrace();
            AbLogUtil.i(mContext, "[HTTP POST]:" + url + ",error:" + var7.getMessage());
            responseListener.onFailure(900, var7.getMessage(), new AbAppException(var7));
        }

    }

    public void get(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HYAbHttpClient.this.doGet(url, params, responseListener);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    public void getWithCache(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String e = url;
                    if (params != null) {
                        if (url.indexOf("?") == -1) {
                            e = e + "?";
                        }

                        e = e + params.getParamString();
                    }

                    String cacheKey = HYAbHttpClient.this.httpCache.getCacheKey(e);
                    AbDiskCache.Entry entry = HYAbHttpClient.this.diskCache.get(cacheKey);
                    String responseBody;
                    if (entry != null && !entry.isExpired()) {
                        Thread.sleep(200L);
                        byte[] httpData1 = entry.data;
                        responseBody = new String(httpData1);
                        ((AbStringHttpResponseListener) responseListener).sendSuccessMessage(200, responseBody);
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP GET CACHED]:" + e + ",result：" + responseBody);
                        return;
                    }

                    AbLogUtil.i(AbImageLoader.class, "磁盘中没有缓存，或者已经过期");
                    if (AbAppUtil.isNetworkAvailable(HYAbHttpClient.mContext)) {
                        AbCacheResponse httpData = AbCacheUtil.getCacheResponse(e);
                        if (httpData != null) {
                            responseBody = new String(httpData.data);
                            AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP GET]:" + e + ",result：" + responseBody);
                            AbDiskCache.Entry entryNew = AbCacheHeaderParser.parseCacheHeaders(httpData, HYAbHttpClient.this.cacheMaxAge);
                            if (entryNew != null) {
                                HYAbHttpClient.this.diskCache.put(cacheKey, entryNew);
                                AbLogUtil.i(HYAbHttpClient.mContext, "HTTP 缓存成功");
                            } else {
                                AbLogUtil.i(AbImageLoader.class, "HTTP 缓存失败，parseCacheHeaders失败");
                            }

                            ((AbStringHttpResponseListener) responseListener).sendSuccessMessage(200, responseBody);
                        } else {
                            responseListener.sendFailureMessage(500, AbAppConfig.REMOTE_SERVICE_EXCEPTION, new AbAppException(AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                        }

                        return;
                    }

                    Thread.sleep(200L);
                    responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                } catch (Exception var10) {
                    var10.printStackTrace();
                    responseListener.sendFailureMessage(900, var10.getMessage(), new AbAppException(var10));
                    return;
                } finally {
                    responseListener.sendFinishMessage();
                }

            }
        });
    }

    public void post(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HYAbHttpClient.this.doPost(url, params, responseListener, (String) null, (String) null);
                } catch (Exception var2) {
                    var2.printStackTrace();
                    System.out.println("[HYAbHttpClient.this.doPost Exception]======="+var2.getMessage()+"=======");
                }

            }
        });
    }

    public void post(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener, final String headerKey, final String headerValue) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HYAbHttpClient.this.doPost(url, params, responseListener, headerKey, headerValue);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    private void doGet(String url, AbRequestParams params, AbHttpResponseListener responseListener) {
        try {
            if (!AbAppUtil.isNetworkAvailable(mContext)) {
                Thread.sleep(200L);
                responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            if (params != null) {
                if (url.indexOf("?") == -1) {
                    url = url + "?";
                }

                url = url + params.getParamString();
            }

            HttpGet e = new HttpGet(url);
            e.addHeader("User-Agent", this.userAgent);
            e.addHeader("Accept-Encoding", "gzip");
            DefaultHttpClient httpClient = this.getHttpClient();
            httpClient.execute(e, new HYAbHttpClient.RedirectionResponseHandler(url, responseListener), this.mHttpContext);
        } catch (Exception var6) {
            var6.printStackTrace();
            responseListener.sendFailureMessage(900, var6.getMessage(), new AbAppException(var6));
        }

    }

    private void doPost(String url, AbRequestParams params, AbHttpResponseListener responseListener, String headerKey, String headerValue) {
        try {
            if (!AbAppUtil.isNetworkAvailable(mContext)) {
                Thread.sleep(200L);
                responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            /**===================本地模拟数据拦截=========================*/
            /**=========================================================*/


            HttpPost httpPost = new HttpPost(url);
            if (headerKey != null && headerValue != null) {
                httpPost.addHeader(headerKey, headerValue);
            }
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.addHeader("User-Agent", this.userAgent);
            httpPost.addHeader("Accept-Encoding", "gzip");
            boolean isContainFile = false;
            if (params != null) {
                HttpEntity httpClient = params.getEntity();
                httpPost.setEntity(httpClient);
                if (params.getFileParams().size() > 0) {
                    isContainFile = true;
                }
            }

            DefaultHttpClient httpClient1 = this.getHttpClient();
            if (isContainFile) {
                httpPost.addHeader("connection", "keep-alive");
                httpPost.addHeader("Content-Type", "multipart/form-data1; boundary=" + params.boundaryString());
                AbLogUtil.i(mContext, "[HTTP POST]:" + url + ",包含文件域!");
            }

            httpClient1.execute(httpPost, new HYAbHttpClient.RedirectionResponseHandler(url, responseListener), this.mHttpContext);
        } catch (Exception var9) {
            var9.printStackTrace();
            AbLogUtil.i(mContext, "[HTTP POST]:" + url + ",error:" + var9.getMessage());
            responseListener.sendFailureMessage(900, var9.getMessage(), new AbAppException(var9));
        }

    }

    public void postJson(final String url, final AbJsonParams params, final AbStringHttpResponseListener responseListener) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConn = null;

                try {
                    if (!AbAppUtil.isNetworkAvailable(HYAbHttpClient.mContext)) {
                        Thread.sleep(200L);
                        responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                        return;
                    }

                    String e = null;
                    URL requestUrl = new URL(url);
                    urlConn = (HttpURLConnection) requestUrl.openConnection();
                    urlConn.setRequestMethod("POST");
                    urlConn.setConnectTimeout(HYAbHttpClient.this.mTimeout);
                    urlConn.setReadTimeout(HYAbHttpClient.this.mTimeout);
                    urlConn.setDoOutput(true);
                    StringBody body = null;
                    if (params != null) {
                        urlConn.setRequestProperty("connection", "keep-alive");
                        urlConn.setRequestProperty("Content-Type", "application/json");
                        body = StringBody.create(params.getJson(), "application/json", Charset.forName("UTF-8"));
                        body.writeTo(urlConn.getOutputStream(), (WriteByteListener) null);
                    } else {
                        urlConn.connect();
                    }

                    if (body != null) {
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP POST]:" + url + ",body:" + params.getJson());
                    } else {
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP POST]:" + url + ",body:无");
                    }

                    if (urlConn.getResponseCode() == 200) {
                        e = HYAbHttpClient.this.readString(urlConn.getInputStream());
                    } else {
                        e = HYAbHttpClient.this.readString(urlConn.getErrorStream());
                    }

                    AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP POST]Result:" + e);
                    urlConn.getInputStream().close();
                    responseListener.sendSuccessMessage(200, e);
                } catch (Exception var8) {
                    var8.printStackTrace();
                    AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP POST]:" + url + ",error：" + var8.getMessage());
                    responseListener.sendFailureMessage(900, var8.getMessage(), new AbAppException(var8));
                } finally {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }

                    responseListener.sendFinishMessage();
                }

            }
        });
    }

    public void doRequest(final String url, final AbRequestParams params, final AbStringHttpResponseListener responseListener) {
        responseListener.setHandler(new HYAbHttpClient.ResponderHandler(responseListener));
        responseListener.onStart();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConn = null;

                try {
                    if (AbAppUtil.isNetworkAvailable(HYAbHttpClient.mContext)) {
                        String e = null;
                        URL requestUrl = new URL(url);
                        urlConn = (HttpURLConnection) requestUrl.openConnection();
                        urlConn.setRequestMethod("POST");
                        urlConn.setConnectTimeout(HYAbHttpClient.this.mTimeout);
                        urlConn.setReadTimeout(HYAbHttpClient.this.mTimeout);
                        urlConn.setDoOutput(true);
                        if (params != null) {
                            urlConn.setRequestProperty("connection", "keep-alive");
                            urlConn.setRequestProperty("Content-Type", "multipart/form-data1; boundary=" + params.boundaryString());
                            MultipartEntity reqEntity = params.getMultiPart();
                            reqEntity.writeTo(urlConn.getOutputStream());
                        } else {
                            urlConn.connect();
                        }

                        if (urlConn.getResponseCode() == 200) {
                            e = HYAbHttpClient.this.readString(urlConn.getInputStream());
                        } else {
                            e = HYAbHttpClient.this.readString(urlConn.getErrorStream());
                        }

                        urlConn.getInputStream().close();
                        responseListener.sendSuccessMessage(200, e);
                        return;
                    }

                    Thread.sleep(200L);
                    responseListener.sendFailureMessage(600, AbAppConfig.CONNECT_EXCEPTION, new AbAppException(AbAppConfig.CONNECT_EXCEPTION));
                } catch (Exception var8) {
                    var8.printStackTrace();
                    AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP POST]:" + url + ",error：" + var8.getMessage());
                    responseListener.sendFailureMessage(900, var8.getMessage(), new AbAppException(var8));
                    return;
                } finally {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }

                    responseListener.sendFinishMessage();
                }

            }
        });
    }

    public void writeResponseData(Context context, HttpEntity entity, String name, AbFileHttpResponseListener responseListener) {
        if (entity != null) {
            if (responseListener.getFile() == null) {
                responseListener.setFile(context, name);
            }

            InputStream inStream = null;
            FileOutputStream outStream = null;

            try {
                inStream = entity.getContent();
                long e = entity.getContentLength();
                outStream = new FileOutputStream(responseListener.getFile());
                if (inStream != null) {
                    byte[] tmp = new byte[4096];
                    int count = 0;

                    int l;
                    while ((l = inStream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        count += l;
                        outStream.write(tmp, 0, l);
                        responseListener.sendProgressMessage((long) count, (long) ((int) e));
                    }
                }

                responseListener.sendSuccessMessage(200);
            } catch (Exception var20) {
                var20.printStackTrace();
                responseListener.sendFailureMessage(602, AbAppConfig.SOCKET_TIMEOUT_EXCEPTION, var20);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }

                    if (outStream != null) {
                        outStream.flush();
                        outStream.close();
                    }
                } catch (IOException var19) {
                    var19.printStackTrace();
                }

            }

        }
    }

    public void writeResponseData2(Context context, HttpEntity entity, String name, AbFileHttpResponseListener responseListener) {
        if (entity != null) {
            if (responseListener.getFile() == null) {
                responseListener.setFile(context, name);
            }

            InputStream inStream = null;
            FileOutputStream outStream = null;

            try {
                inStream = entity.getContent();
                long e = entity.getContentLength();
                outStream = new FileOutputStream(responseListener.getFile());
                if (inStream != null) {
                    byte[] tmp = new byte[4096];
                    int count = 0;

                    int l;
                    while ((l = inStream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        count += l;
                        outStream.write(tmp, 0, l);
                        responseListener.onProgress((long) count, (long) ((int) e));
                    }
                }

                responseListener.onSuccess(200);
            } catch (Exception var20) {
                var20.printStackTrace();
                responseListener.onFailure(602, AbAppConfig.SOCKET_TIMEOUT_EXCEPTION, var20);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }

                    if (outStream != null) {
                        outStream.flush();
                        outStream.close();
                    }
                } catch (IOException var19) {
                    var19.printStackTrace();
                }

            }

        }
    }

    public void readResponseData(HttpEntity entity, AbBinaryHttpResponseListener responseListener) {
        if (entity != null) {
            InputStream inStream = null;
            ByteArrayOutputStream outSteam = null;

            try {
                inStream = entity.getContent();
                outSteam = new ByteArrayOutputStream();
                long e = entity.getContentLength();
                if (inStream != null) {
                    int count = 0;
                    byte[] tmp = new byte[4096];

                    int l;
                    while ((l = inStream.read(tmp)) != -1) {
                        count += l;
                        outSteam.write(tmp, 0, l);
                        responseListener.sendProgressMessage((long) count, (long) ((int) e));
                    }
                }

                responseListener.sendSuccessMessage(200, outSteam.toByteArray());
            } catch (Exception var18) {
                var18.printStackTrace();
                responseListener.sendFailureMessage(602, AbAppConfig.SOCKET_TIMEOUT_EXCEPTION, var18);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }

                    if (outSteam != null) {
                        outSteam.close();
                    }
                } catch (Exception var17) {
                    var17.printStackTrace();
                }

            }

        }
    }

    public void readResponseData2(HttpEntity entity, AbBinaryHttpResponseListener responseListener) {
        if (entity != null) {
            InputStream inStream = null;
            ByteArrayOutputStream outSteam = null;

            try {
                inStream = entity.getContent();
                outSteam = new ByteArrayOutputStream();
                long e = entity.getContentLength();
                if (inStream != null) {
                    int count = 0;
                    byte[] tmp = new byte[4096];

                    int l;
                    while ((l = inStream.read(tmp)) != -1) {
                        count += l;
                        outSteam.write(tmp, 0, l);
                        responseListener.onProgress((long) count, (long) ((int) e));
                    }
                }

                responseListener.onSuccess(200, outSteam.toByteArray());
            } catch (Exception var18) {
                var18.printStackTrace();
                responseListener.onFailure(602, AbAppConfig.SOCKET_TIMEOUT_EXCEPTION, var18);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }

                    if (outSteam != null) {
                        outSteam.close();
                    }
                } catch (Exception var17) {
                    var17.printStackTrace();
                }

            }

        }
    }

    public void setTimeout(int timeout) {
        this.mTimeout = timeout;
    }

    public BasicHttpParams getHttpParams() {
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(30);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
        ConnManagerParams.setTimeout(httpParams, (long) this.mTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);
        HttpConnectionParams.setSoTimeout(httpParams, this.mTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.mTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, this.userAgent);
        HttpClientParams.setRedirecting(httpParams, false);
        HttpClientParams.setCookiePolicy(httpParams, "compatibility");
        httpParams.setParameter("http.route.default-proxy", (Object) null);
        return httpParams;
    }

    public DefaultHttpClient getHttpClient() {
        return this.mHttpClient != null ? this.mHttpClient : this.createHttpClient();
    }

    public DefaultHttpClient createHttpClient() {
        BasicHttpParams httpParams = this.getHttpParams();
        if (this.mIsOpenEasySSL) {
            EasySSLProtocolSocketFactory easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
            SchemeRegistry supportedSchemes = new SchemeRegistry();
            PlainSocketFactory socketFactory = PlainSocketFactory.getSocketFactory();
            supportedSchemes.register(new Scheme("http", socketFactory, 80));
            supportedSchemes.register(new Scheme("https", easySSLProtocolSocketFactory, 443));
            ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(httpParams, supportedSchemes);
            this.mHttpClient = new DefaultHttpClient(connectionManager, httpParams);
        } else {
            this.mHttpClient = new DefaultHttpClient(httpParams);
        }

        this.mHttpClient.setHttpRequestRetryHandler(this.mRequestRetryHandler);
        this.mHttpClient.setCookieStore(this.mCookieStore);
        return this.mHttpClient;
    }

    public boolean isOpenEasySSL() {
        return this.mIsOpenEasySSL;
    }

    public void setOpenEasySSL(boolean isOpenEasySSL) {
        this.mIsOpenEasySSL = isOpenEasySSL;
    }

    private String readString(InputStream is) {
        StringBuffer rst = new StringBuffer();
        byte[] buffer = new byte[1024];
        boolean len = false;

        int len1;
        try {
            while ((len1 = is.read(buffer)) > 0) {
                rst.append(new String(buffer, 0, len1, "UTF-8"));
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return rst.toString();
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEncode() {
        return this.encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void shutdown() {
        if (this.mHttpClient != null && this.mHttpClient.getConnectionManager() != null) {
            this.mHttpClient.getConnectionManager().shutdown();
        }

    }

    public CookieStore getCookieStore() {
        if (this.mHttpClient != null) {
            this.mCookieStore = this.mHttpClient.getCookieStore();
        }

        return this.mCookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.mCookieStore = cookieStore;
    }

    public long getCacheMaxAge() {
        return this.cacheMaxAge;
    }

    public void setCacheMaxAge(long cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }

    private class RedirectionResponseHandler implements ResponseHandler<String> {
        private AbHttpResponseListener mResponseListener = null;
        private String mUrl = null;

        public RedirectionResponseHandler(String url, AbHttpResponseListener responseListener) {
            this.mUrl = url;
            this.mResponseListener = responseListener;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpUriRequest request = (HttpUriRequest) HYAbHttpClient.this.mHttpContext.getAttribute("http.request");
            int statusCode = response.getStatusLine().getStatusCode();
            Object entity = response.getEntity();
            String responseBody = null;
            Header locationHeader;
            String location;
            if (statusCode == 200) {
                if (entity != null) {
                    if (this.mResponseListener instanceof AbStringHttpResponseListener) {
                        locationHeader = ((HttpEntity) entity).getContentEncoding();
                        if (locationHeader != null) {
                            location = locationHeader.getValue();
                            if (location != null && location.contains("gzip")) {
                                entity = new AbGzipDecompressingEntity((HttpEntity) entity);
                            }
                        }

                        location = EntityUtils.getContentCharSet((HttpEntity) entity) == null ? HYAbHttpClient.this.encode : EntityUtils.getContentCharSet((HttpEntity) entity);
                        responseBody = new String(EntityUtils.toByteArray((HttpEntity) entity), location);
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + responseBody);
                        ((AbStringHttpResponseListener) this.mResponseListener).sendSuccessMessage(statusCode, responseBody);
                    } else if (this.mResponseListener instanceof AbBinaryHttpResponseListener) {
                        responseBody = "Binary";
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + responseBody);
                        HYAbHttpClient.this.readResponseData((HttpEntity) entity, (AbBinaryHttpResponseListener) this.mResponseListener);
                    } else if (this.mResponseListener instanceof AbFileHttpResponseListener) {
                        String locationHeader1 = AbFileUtil.getCacheFileNameFromUrl(this.mUrl, response);
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + locationHeader1);
                        HYAbHttpClient.this.writeResponseData(HYAbHttpClient.mContext, (HttpEntity) entity, locationHeader1, (AbFileHttpResponseListener) this.mResponseListener);
                    }

                    try {
                        ((HttpEntity) entity).consumeContent();
                    } catch (Exception var8) {
                        var8.printStackTrace();
                    }

                    this.mResponseListener.sendFinishMessage();
                    return responseBody;
                }
            } else if (statusCode != 302 && statusCode != 301) {
                if (statusCode == 404) {
                    this.mResponseListener.sendFailureMessage(statusCode, request.getParams().toString()+AbAppConfig.NOT_FOUND_EXCEPTION+request.getURI().toString(), new AbAppException(AbAppConfig.NOT_FOUND_EXCEPTION));
                    this.mResponseListener.sendFinishMessage();
                } else if (statusCode == 403) {
                    this.mResponseListener.sendFailureMessage(statusCode, AbAppConfig.FORBIDDEN_EXCEPTION, new AbAppException(AbAppConfig.FORBIDDEN_EXCEPTION));
                    this.mResponseListener.sendFinishMessage();
                } else {
                    this.mResponseListener.sendFailureMessage(statusCode, AbAppConfig.REMOTE_SERVICE_EXCEPTION, new AbAppException(AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                    this.mResponseListener.sendFinishMessage();
                }
            } else {
                locationHeader = response.getLastHeader("location");
                location = locationHeader.getValue();
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    Log.e("statusCode:重定向 ", statusCode+"");
                    HYAbHttpClient.this.doPost(location, (AbRequestParams) null, this.mResponseListener, (String) null, (String) null);
                } else if ("GET".equalsIgnoreCase(request.getMethod())) {
                    HYAbHttpClient.this.doGet(location, (AbRequestParams) null, this.mResponseListener);
                }
            }

            return null;
        }
    }

    private class RedirectionResponseHandler2 implements ResponseHandler<String> {
        private AbHttpResponseListener mResponseListener = null;
        private String mUrl = null;

        public RedirectionResponseHandler2(String url, AbHttpResponseListener responseListener) {
            this.mUrl = url;
            this.mResponseListener = responseListener;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpUriRequest request = (HttpUriRequest) HYAbHttpClient.this.mHttpContext.getAttribute("http.request");
            int statusCode = response.getStatusLine().getStatusCode();
            Object entity = response.getEntity();
            String responseBody = null;
            Header locationHeader;
            String location;
            if (statusCode == 200) {
                if (entity != null) {
                    if (this.mResponseListener instanceof AbStringHttpResponseListener) {
                        locationHeader = ((HttpEntity) entity).getContentEncoding();
                        if (locationHeader != null) {
                            location = locationHeader.getValue();
                            if (location != null && location.contains("gzip")) {
                                entity = new AbGzipDecompressingEntity((HttpEntity) entity);
                            }
                        }

                        location = EntityUtils.getContentCharSet((HttpEntity) entity) == null ? HYAbHttpClient.this.encode : EntityUtils.getContentCharSet((HttpEntity) entity);
                        responseBody = new String(EntityUtils.toByteArray((HttpEntity) entity), location);
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + responseBody);
                        ((AbStringHttpResponseListener) this.mResponseListener).onSuccess(statusCode, responseBody);
                    } else if (this.mResponseListener instanceof AbBinaryHttpResponseListener) {
                        responseBody = "Binary";
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + responseBody);
                        HYAbHttpClient.this.readResponseData2((HttpEntity) entity, (AbBinaryHttpResponseListener) this.mResponseListener);
                    } else if (this.mResponseListener instanceof AbFileHttpResponseListener) {
                        String locationHeader1 = AbFileUtil.getCacheFileNameFromUrl(this.mUrl, response);
                        AbLogUtil.i(HYAbHttpClient.mContext, "[HTTP Response]:" + request.getURI() + ",result：" + locationHeader1);
                        HYAbHttpClient.this.writeResponseData(HYAbHttpClient.mContext, (HttpEntity) entity, locationHeader1, (AbFileHttpResponseListener) this.mResponseListener);
                    }

                    try {
                        ((HttpEntity) entity).consumeContent();
                    } catch (Exception var8) {
                        var8.printStackTrace();
                    }

                    this.mResponseListener.onFinish();
                    return responseBody;
                }
            } else if (statusCode != 302 && statusCode != 301) {
                if (statusCode == 404) {
                    this.mResponseListener.onFailure(statusCode, AbAppConfig.NOT_FOUND_EXCEPTION, new AbAppException(AbAppConfig.NOT_FOUND_EXCEPTION));
                    this.mResponseListener.onFinish();
                } else if (statusCode == 403) {
                    this.mResponseListener.onFailure(statusCode, AbAppConfig.FORBIDDEN_EXCEPTION, new AbAppException(AbAppConfig.FORBIDDEN_EXCEPTION));
                    this.mResponseListener.onFinish();
                } else {
                    this.mResponseListener.onFailure(statusCode, AbAppConfig.REMOTE_SERVICE_EXCEPTION, new AbAppException(AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                    this.mResponseListener.onFinish();
                }
            } else {
                locationHeader = response.getLastHeader("location");
                location = locationHeader.getValue();
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    HYAbHttpClient.this.postWithoutThread(location, new AbRequestParams(), this.mResponseListener);
                } else if ("GET".equalsIgnoreCase(request.getMethod())) {
                    HYAbHttpClient.this.getWithoutThread(location, new AbRequestParams(), this.mResponseListener);
                }
            }

            return null;
        }
    }

    private static class ResponderHandler extends Handler {
        private Object[] response;
        private AbHttpResponseListener responseListener;

        public ResponderHandler(AbHttpResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    this.response = (Object[]) msg.obj;
                    if (this.response != null) {
                        if (this.responseListener instanceof AbStringHttpResponseListener) {
                            if (this.response.length >= 2) {
                                ((AbStringHttpResponseListener) this.responseListener).onSuccess(((Integer) this.response[0]).intValue(), (String) this.response[1]);
                            } else {
                                AbLogUtil.e(HYAbHttpClient.mContext, "SUCCESS_MESSAGE " + AbAppConfig.MISSING_PARAMETERS);
                            }
                        } else if (this.responseListener instanceof AbBinaryHttpResponseListener) {
                            if (this.response.length >= 2) {
                                ((AbBinaryHttpResponseListener) this.responseListener).onSuccess(((Integer) this.response[0]).intValue(), (byte[]) this.response[1]);
                            } else {
                                AbLogUtil.e(HYAbHttpClient.mContext, "SUCCESS_MESSAGE " + AbAppConfig.MISSING_PARAMETERS);
                            }
                        } else if (this.responseListener instanceof AbFileHttpResponseListener) {
                            if (this.response.length >= 1) {
                                AbFileHttpResponseListener exception1 = (AbFileHttpResponseListener) this.responseListener;
                                exception1.onSuccess(((Integer) this.response[0]).intValue(), exception1.getFile());
                            } else {
                                AbLogUtil.e(HYAbHttpClient.mContext, "SUCCESS_MESSAGE " + AbAppConfig.MISSING_PARAMETERS);
                            }
                        }
                    }
                    break;
                case 1:
                    this.response = (Object[]) msg.obj;
                    if (this.response != null && this.response.length >= 3) {
                        AbAppException exception = new AbAppException((Exception) this.response[2]);
                        this.responseListener.onFailure(((Integer) this.response[0]).intValue(), (String) this.response[1], exception);
                    } else {
                        AbLogUtil.e(HYAbHttpClient.mContext, "FAILURE_MESSAGE " + AbAppConfig.MISSING_PARAMETERS);
                    }
                case 2:
                case 3:
                default:
                    break;
                case 4:
                    this.responseListener.onStart();
                    break;
                case 5:
                    this.responseListener.onFinish();
                    break;
                case 6:
                    this.response = (Object[]) msg.obj;
                    if (this.response != null && this.response.length >= 2) {
                        this.responseListener.onProgress(((Long) this.response[0]).longValue(), ((Long) this.response[1]).longValue());
                    } else {
                        AbLogUtil.e(HYAbHttpClient.mContext, "PROGRESS_MESSAGE " + AbAppConfig.MISSING_PARAMETERS);
                    }
                    break;
                case 7:
                    this.responseListener.onRetry();
            }

        }
    }


}

