package zoo.hymn.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.wb.R;
import com.bm.wb.api.APIMethods;
import com.bm.wb.bean.UserBean;
import com.bm.wb.ui.zoo.LoginActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import zoo.hymn.base.net.callback.ZooCallBack;
import zoo.hymn.base.net.engines.andbase.HYAbHttpUtil;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.utils.NetUtil;
import zoo.hymn.utils.StrUtil;
import zoo.hymn.views.CommonDialog;
import zoo.hymn.views.EaseTitleBar;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;
import static zoo.hymn.ZooConstant.PERMISSON_REQUEST_CODE;
import static zoo.hymn.ZooConstant.URL_MEDIA;


/**
 * ClassName: BaseActivity
 * Function : 所有activity的基类，内涵无网络数据处理页面切换，自定义顶部栏，初始化控件，数据
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/17
 */
public abstract class BaseActivity extends AppCompatActivity implements ZooCallBack {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private FrameLayout mRootLayout;//子布局根容器
    private RelativeLayout mRootTitleBar;//标题栏布局的根容器
    private ViewStub vsNoNet;
    private View vwNoNet;
    private ViewStub vsNoData;
    private View vwNoData;
    private int mCustomLayoutId;
    protected View defaultTitleView;
    protected Context mContext;

    /*** 无网络提示状态 */
    public final static int VIEW_SHOW = 0;//0采用布局模式
    public final static int TOAST_SHOW = 1;//1采用toast模式
    private int noNetShowMode;//标题栏状态
    /*** 标题栏状态 */
    public final static int DEFAULT_TITLE = 0;//默认标题栏
    public final static int CUSTOM_TITLE = 1;//自定义标题栏
    public final static int GONE_TITLE = 2;//无标题栏
    private int titleMode;//标题栏状态


    protected String getText(TextView tv){
        return tv.getText().toString() == null ?"":tv.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");   //注意：基类是Activity时参数为android:fragments， 一定要在super.onCreate函数前执行！！！
        }
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.ac_base);
        mRootLayout = (FrameLayout) findViewById(R.id.base_fl_layout);
        mRootTitleBar = (RelativeLayout) findViewById(R.id.base_rl_title_bar);
    }

    /**
     * 设置标题栏显示模式
     * 在addChildView(),前设置。
     * DEFAULT_TITLE
     * CUSTOM_TITLE
     * GONE_TITLE
     * @param mode
     */
    protected void setTitleMode(int mode) {
        this.titleMode = mode;
    }
    /**
     * 设置无网络提示状态
     * VIEW_SHOW
     * TOAST_SHOW
     * @param mode
     */
    protected void setNoNetShowMode(int mode) {
        this.noNetShowMode = mode;
    }

    /**
     * 自定义标题栏,需要先设置setTitleMode（CUSTOM_TITLE）后才可以显示
     * @param resId
     */
    protected void setTitleLayoutId(int resId) {
        setTitleMode(CUSTOM_TITLE);
        this.mCustomLayoutId = resId;
    }

    private View backTitleView() {
        defaultTitleView = null;
        switch (titleMode) {
            case CUSTOM_TITLE:
                if (mCustomLayoutId != 0) {
                    defaultTitleView = getLayoutInflater().inflate(mCustomLayoutId,
                            null);
                } else {
                    throw new ExceptionInInitializerError(
                            "...自定义标题栏mCustomLayoutId没有初始化，请调用setTitleLayoutId(int resId)进行初始化...");
                }
                break;
            case GONE_TITLE:
                defaultTitleView = null;
                break;

            default:
                if (mContext != null) {
                    defaultTitleView = new EaseTitleBar(mContext);
                } else {
                    throw new ExceptionInInitializerError(
                            "...mContext没有初始化，请在onCreate()方法中的super.onCreate(arg0)代码后进行初始化...");
                }
                break;
        }
        return defaultTitleView;
    }


    /**
     * @param bodyLayoutResId 每个activity载体要填充的layout
     */
    public void addChildView(int bodyLayoutResId) {
        View titleView = backTitleView();
        if (titleView == null) {
            mRootTitleBar.setVisibility(View.GONE);
        } else {
            mRootTitleBar.setVisibility(View.VISIBLE);
            mRootTitleBar.addView(titleView);
        }
        View bodyView = getLayoutInflater().inflate(bodyLayoutResId, null);
        mRootLayout.addView(bodyView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        ButterKnife.bind(this);
        initView();
        initData();
    }


    /**
     * 实例化view,设置click事件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 无网络，网络异常时,重新加载按钮事件
     * 此抽象方法名与通用无网络页面中的布局点击事件同名
     * @param view
     */
    public void reLoad(View view){
        checkNet();
    }

    public void checkNet(){
        if(!NetUtil.isNetworkAvailable(mContext)){
            noNet(-1);
        }else{
            visibleBodyView();
            initData();
        }
    }

    /**
     * 正常显示
     */
    protected void visibleBodyView() {
        visibleBodyView(View.VISIBLE);
    }

    /**
     * 显示没有网络的页面
     */
    protected void visibleNoNet() {
        initNoNetLayout(View.VISIBLE);
        mRootLayout.setVisibility(View.GONE);
    }
    /**
     * 显示没有数据的页面
     */
    protected void visibleNoData() {
        // TODO Auto-generated method stub
        initNoDataLayout(View.VISIBLE);
        mRootLayout.setVisibility(View.GONE);
    }

    /**
     * 网络超时时显示的处理页面，避免二次提交现象 数据加载前也是如此显示
     */
    protected void goneAllView() {
        visibleBodyView(View.GONE);
    }

    private void initNoNetLayout(int isGone) {
        if (vwNoData != null) {
            vwNoData.setVisibility(View.GONE);
        }
        if (vwNoNet == null) {
            vsNoNet = (ViewStub) findViewById(R.id.vs_no_net);
            vwNoNet = vsNoNet.inflate();
        } else {
            vwNoNet.setVisibility(isGone);
        }
    }

    /**
     * 网络超时时显示的处理页面，避免二次提交现象
     */
    private void visibleBodyView(int goneOrVisible) {
        if (vwNoNet != null) {
            vwNoNet.setVisibility(View.GONE);
        }
        if (vwNoData != null) {
            vwNoData.setVisibility(View.GONE);
        }
        if(mRootLayout != null) {
            mRootLayout.setVisibility(goneOrVisible);
        }
    }

    private void initNoDataLayout(int isGone) {
        if (vwNoNet != null) {
            vwNoNet.setVisibility(View.GONE);
        }
        if (vwNoData == null) {
            vsNoData = (ViewStub) findViewById(R.id.vs_no_data);
            vwNoData = vsNoData.inflate();
        } else {
            vwNoData.setVisibility(isGone);
        }
    }

    @Override
    public void fail(final int tag, final String error) {
        if(StrUtil.isEmpty(error)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(error);
            }
        });

    }

    @Override
    public void success(int tag, BaseResponse response) {

    }

    @Override
    public void noData(int tag) {
        // TODO Auto-generated method stub
        visibleNoData();
    }

    @Override
    public void noNet(int tag) {
        switch (noNetShowMode){
            case VIEW_SHOW:
                visibleNoNet();
                break;
            default:
               showToast(mContext.getResources().getString(R.string.network_not_connected));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mRootTitleBar != null) {
            mRootTitleBar.removeAllViews();
            mRootTitleBar = null;
        }
        if (mRootLayout != null) {
            mRootLayout.removeAllViews();
            mRootLayout = null;
        }
        if (vwNoNet != null) {
            vwNoNet = null;
        }
        if (vwNoData != null) {
            vwNoData = null;
        }
        if (defaultTitleView != null) {
            defaultTitleView = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HYAbHttpUtil.shutdownHttpClient();
        APIMethods.SHUTDOWN();
    }

    protected void showToast(String msg) {
        if (mContext != null) {
            if("success".equals(msg) || "没有数据".equals(msg)){
                return;
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
    protected void showToast(int msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /*****************CHECK_PERMISSIONS*********************/
    /**
     * @param permissions
     * @since 2.5.0
     */
    protected void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUEST_CODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        CommonDialog commonDialog = new CommonDialog(getString(R.string.notifyTitle), getString(R.string.notifyMsg), getString(R.string.cancel), getString(R.string.setting), new CommonDialog.DialogClick() {
            @Override
            public void cancel(Object tag) {
                finish();
            }

            @Override
            public void confirm(Object tag) {
                startAppSettings();
            }
        });
        commonDialog.twoButtonDialog(this).showClearDialog();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.notifyTitle);
//        builder.setMessage(R.string.notifyMsg);
//
//        // 拒绝, 退出应用
//        builder.setNegativeButton(R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//
//        builder.setPositiveButton(R.string.setting,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startAppSettings();
//                    }
//                });
//
//        builder.setCancelable(false);
//
//        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    /*****************CHECK_PERMISSIONS*********************/

    /*****************JPUSH RONGCHAT*********************/
    private void dealWithJpushAndRong(final UserBean loginBean) {

        /**
         * 调用JPush API设置Alias
         */
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, loginBean.pushAlias));
        String[] tags = loginBean.pushTags.split(",");
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, new HashSet<String>(Arrays.asList(tags))));

        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地内存，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 将用户信息缓存到本地内存中。
         * @see UserInfoProvider
         */
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {

                return new UserInfo(userId,
                        loginBean.nickname, Uri.parse(URL_MEDIA + loginBean.avatarUrl));//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

//            showToast(logs);
        }

    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

//            showToast(logs);
        }

    };

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {init(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @return RongIM  客户端核心类的实例。
     */
    protected void connect(final Activity mContext) {
        UserBean lg = DataSupport.findFirst(UserBean.class);
        if (lg != null) {
            dealWithJpushAndRong(lg);
            if (StrUtil.isNotEmpty(lg.rongToken)) {
                if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                    RongIM.connect(lg.rongToken, new RongIMClient.ConnectCallback() {

                        /**
                         * Token 错误。可以从下面两点检查
                         * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                         * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                         */
                        @Override
                        public void onTokenIncorrect() {
                            showToast("onTokenIncorrect！");
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
                        }

                        /**
                         * 连接融云成功
                         *
                         * @param userid 当前 token 对应的用户 id
                         */
                        @Override
                        public void onSuccess(String userid) {
                            Log.d("LoginActivity", "--onSuccess" + userid);
                        }

                        /**
                         * 连接融云失败
                         *
                         * @param errorCode 错误码，可到官网 查看错误码对应的注释
                         */
                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            showToast("RongIMClient errorCode！" + errorCode);
                        }
                    });
                }
            }
        }
    }
    /*****************JPUSH RONGCHAT*********************/
}
