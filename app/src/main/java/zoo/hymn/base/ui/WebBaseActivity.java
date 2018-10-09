package zoo.hymn.base.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bm.wb.R;

import java.io.File;

import zoo.hymn.utils.BitmapUtil;
import zoo.hymn.utils.NetUtil;
import zoo.hymn.utils.PhotoUtils;
import zoo.hymn.views.pickSinglePhoto.PickSinglePhotoDialog;
import zoo.hymn.views.webview.Html5WebView;


/**
 * ClassName: WebBaseActivity
 * Function : 所有webview的基类,请在此处配置webview
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/17
 */
public abstract class WebBaseActivity extends BaseActivity {

    private FrameLayout mLayout;
    protected ProgressBar progress;
    protected WebView webView;
    protected WebChromeClient webChromeClient;
    protected WebViewClient webViewClient;
    protected String requestUrl = "";
    protected String htmlData = "";
    protected String title = "";
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private File imageFile = null;
    private Uri imageUri;
    private Uri cropImageUri;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress.setVisibility(View.GONE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.ac_common_web);
    }

    @Override
    protected void initView() {
        progress = (ProgressBar) findViewById(R.id.progress);
        mLayout = (FrameLayout) findViewById(R.id.web_layout);
//        webView = (WebView) findViewById(R.id.webView);
        // 创建 WebView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView = new Html5WebView(getApplicationContext());
        webView.setLayoutParams(params);
        mLayout.addView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置webview为无缓存模式，不使用缓存，只从网络获取数据.
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        webView.destroyDrawingCache();
        webView.addJavascriptInterface(this, "testObjcCallback");
//        saveData(settings);

        if (webChromeClient == null) {
            webView.setWebChromeClient(new MyWebChromeClient());
        } else {
            webView.setWebChromeClient(webChromeClient);
        }
        if (webViewClient == null) {
            webView.setWebViewClient(new MyWebViewClient());
        } else {
            webView.setWebViewClient(webViewClient);
        }
        checkNet();
    }

    protected void loadView() {
        if (requestUrl != null) {
            webView.loadUrl(requestUrl);
        } else {
            //加载、并显示HTML代码
            webView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);
        }
    }

    @Override
    public void checkNet() {
        if (!NetUtil.isNetworkAvailable(mContext)) {
            noNet(1);
        } else {
            visibleBodyView();
            loadView();
        }
    }

    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置

        if (NetUtil.checkNet(mContext)) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }
        File cacheDir = mContext.getCacheDir();
        if (cacheDir != null) {
            String appCachePath = cacheDir.getAbsolutePath();
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setDatabaseEnabled(true);
            mWebSettings.setAppCacheEnabled(true);
            mWebSettings.setAppCachePath(appCachePath);
        }
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * 实现一个基础的 WebViewClient ，如果有更多的需要，直接继承它
     */
    static class BaseWebViewClient extends WebViewClient {

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 实现一个基础的 WebChromeClient ，如果有更多的需要，直接继承它
     */
    static class BaseWebChromeClient extends WebChromeClient {
        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback
                callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
        //=========多窗口的问题==========================================================
    }


    /**
     * 初始化请求URL，or   WebChromeClient webChromeClient;
     * WebViewClient webViewClient;
     *
     * @since setRequestUrl(), setWebChromeClient(), setWebViewClient()
     */

    @Override
    public void reLoad(View view) {
        checkNet();
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            imgReset();
        }
    }

    /**
     * 循环遍历标签中的图片
     * "var script = document.createElement('script');"
     * "script.type = 'text/javascript';"
     * "script.text = \"function ResizeImages() { "
     * "var img;"
     * "var maxwidth=%f;"
     * "for(i=0;i <document.images.length;i++){"
     * "img = document.images[i];"
     * "if(img.width > maxwidth){"
     * "img.width = maxwidth;"
     * "}"
     * "}"
     * "}\";"
     * "document.getElementsByTagName('head')[0].appendChild(script);", DEF_SCREEN_WIDTH - 36];
     * js 语法
     */
    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%';   " +
                "}" +
                "})()");
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress == 100) {
                progress.setProgress(newProgress);
                handler.sendEmptyMessageDelayed(0, 500);
            } else {
                if (View.GONE == progress.getVisibility()) {
                    progress.setVisibility(View.VISIBLE);
                }
                progress.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
        //=========多窗口的问题==========================================================

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }

    }

  /*  // 改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView != null){
                if (webView.canGoBack()) {
                    webView.goBack();// 返回上一页面
                    return true;
                } else {
                    this.finish();
                }
            }else
                this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /**
     * JS接口
     */


    @android.webkit.JavascriptInterface
    public void push() {
        showToast("js 调了");
        finish();
    }

    protected void setRequestUrl(String url) {
        this.requestUrl = url;
    }

    protected void setWebChromeClient(WebChromeClient chromeClient) {
        this.webChromeClient = chromeClient;
    }

    protected void setWebViewClient(WebViewClient viewClient) {
        this.webViewClient = viewClient;
    }

    private void openImageChooserActivity() {
        cropImageUri = PickSinglePhotoDialog.getCropImageUri(this);
        imageFile = PickSinglePhotoDialog.getCurrentImageFile(this);
        PickSinglePhotoDialog dialog = new PickSinglePhotoDialog(this, imageFile);
        dialog.showClearDialog();

//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
//            if (null == uploadMessage && null == uploadMessageAboveL) return;
//            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//            if (uploadMessageAboveL != null) {
//                onActivityResultAboveL(requestCode, resultCode, intent);
//            } else if (uploadMessage != null) {
//                uploadMessage.onReceiveValue(result);
//                uploadMessage = null;
//            }
//        }

        //兼容7.0以上
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.REQUEST_CAMERA://拍照完成回调
                    imageUri = Uri.fromFile(imageFile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(this, PhotoUtils.PACKAGENAME_FILEPROVIDER, imageFile);//通过FileProvider创建一个content类型的Uri
                    }
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri);
                    break;
                case PhotoUtils.REQUEST_GALLERY://访问相册完成回调
                    Uri newUri = Uri.parse(PhotoUtils.getPath(this, intent.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(this, PhotoUtils.PACKAGENAME_FILEPROVIDER, new File(newUri.getPath()));
                    }
                    PhotoUtils.cropImageUri(this, newUri, cropImageUri);
                    break;
                case PhotoUtils.REQUEST_CROP_IMAGE://裁剪完图片后的操作
                    imageFile = null;
                    if (uploadMessageAboveL == null) {
                        return;
                    }
                    Uri[] results = new Uri[]{cropImageUri};
                    uploadMessageAboveL.onReceiveValue(results);
                    uploadMessageAboveL = null;
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }
}
