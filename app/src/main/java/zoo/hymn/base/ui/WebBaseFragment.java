package zoo.hymn.base.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bm.wb.R;

import zoo.hymn.utils.NetUtil;


/**
 * ClassName: WebBaseActivity
 * Function : 所有webview的基类,请在此处配置webview
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/17
 */
public abstract class WebBaseFragment extends BaseFragment {

    protected ProgressBar progress;
    protected WebView webView;
    protected WebChromeClient webChromeClient;
    protected WebViewClient webViewClient;
    protected String requestUrl;
    protected String htmlData = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress.setVisibility(View.GONE);
        }
    };


    @Override
    protected final int setBodyLayout() {
        return R.layout.ac_common_web;
    }

    @Override
    protected void initView(View view) {
        progress = (ProgressBar) view.findViewById(R.id.progress);
//        webView = (WebView) view.findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置webview为无缓存模式，不使用缓存，只从网络获取数据.
        webView.destroyDrawingCache();

        if (webChromeClient == null) {
            webView.setWebChromeClient(new MyWebChromeClient());
        } else {
            webView.setWebChromeClient(webChromeClient);
        }
        if (webViewClient == null) {
            webView.setWebViewClient(new MyWebViewClient());
        }
        else {
            webView.setWebViewClient(webViewClient);
        }
        checkNet();

    }


    private void loadVIew(){
        if(requestUrl != null) {
            webView.loadUrl(requestUrl);
        }else{
            //加载、并显示HTML代码
            webView.loadDataWithBaseURL(null,htmlData, "text/html", "utf-8", null);
        }
    }

    @Override
    public void checkNet(){
        if(!NetUtil.isNetworkAvailable(mContext)){
            noNet(1);
        }else{
            visibleBodyView();
            loadVIew();
        }
    }

    /**
     * 初始化请求URL，or   WebChromeClient webChromeClient;
     * WebViewClient webViewClient;
     * @since setRequestUrl(),setWebChromeClient(),setWebViewClient()
     */
    @Override
    public abstract void initData();

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
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 加载开始
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            url=url;
        }
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
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void showSource(String html) {

        }
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
}

