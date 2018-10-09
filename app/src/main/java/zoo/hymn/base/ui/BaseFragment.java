package zoo.hymn.base.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bm.wb.R;
import com.bm.wb.api.APIMethods;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import zoo.hymn.base.net.callback.ZooCallBack;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.utils.NetUtil;
import zoo.hymn.views.EaseTitleBar;

import static zoo.hymn.base.net.engines.andbase.HYAbRequest.NET9;


/**
 * ClassName: BaseActivity
 * Function : 所有activity的基类，内涵无网络数据处理页面切换，自定义顶部栏，初始化控件，数据
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/17
 */
public abstract class BaseFragment extends Fragment implements ZooCallBack {

    private FrameLayout mRootLayout;//子布局根容器
    private RelativeLayout mRootTitleBar;//标题栏布局的根容器
    private ViewStub vsNoNet;
    private View vwNoNet;
    private ViewStub vsNoData;
    private View vwNoData;
    private int mCustomLayoutId;
    private View mView;
    protected View defaultTitleView;
    protected Bundle mBundle;
    protected Context mContext;
    protected Activity mActivity;
    protected Unbinder unbinder;
    ;
    /***
     * 无网络提示状态
     */
    public final static int VIEW_SHOW = 0;//0采用布局模式
    public final static int TOAST_SHOW = 1;//1采用toast模式
    private int noNetShowMode;//标题栏状态
    /***
     * 标题栏状态
     */
    public final static int DEFAULT_TITLE = 0;//默认标题栏
    public final static int CUSTOM_TITLE = 1;//自定义标题栏
    public final static int GONE_TITLE = 2;//无标题栏


    private int titleMode;//标题栏状态

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e("onHiddenChanged", "onHiddenChanged: " + hidden);
        if (hidden) {
            APIMethods.SHUTDOWN();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", "onStop: " );
        APIMethods.SHUTDOWN();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mContext = getContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mContext = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mBundle = savedInstanceState;
        //子类可在其此方法中设置自定义标题栏
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ac_base, container, false);
        mRootLayout = (FrameLayout) mView.findViewById(R.id.base_fl_layout);
        mRootTitleBar = (RelativeLayout) mView.findViewById(R.id.base_rl_title_bar);
        addChildView(inflater, setBodyLayout());
        return mView;
    }

    /**
     * 设置标题栏显示模式
     * DEFAULT_TITLE
     * CUSTOM_TITLE
     * GONE_TITLE
     *
     * @param mode
     */
    protected void setTitleMode(int mode) {
        this.titleMode = mode;
    }

    /**
     * 设置无网络提示状态
     * VIEW_SHOW
     * TOAST_SHOW
     *
     * @param mode
     */
    protected void setNoNetShowMode(int mode) {
        this.noNetShowMode = mode;
    }

    /**
     * 自定义标题栏,需要先设置setTitleMode（CUSTOM_TITLE）后才可以显示
     *
     * @param resId
     */
    protected void setTitleLayoutId(int resId) {
        this.mCustomLayoutId = resId;
    }

    private View backTitleView(LayoutInflater inflater) {
        defaultTitleView = null;
        switch (titleMode) {
            case CUSTOM_TITLE:
                if (mCustomLayoutId != 0) {
                    defaultTitleView = inflater.inflate(mCustomLayoutId,
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
    private void addChildView(LayoutInflater inflater, int bodyLayoutResId) {
        View titleView = backTitleView(inflater);
        if (titleView == null) {
            mRootTitleBar.setVisibility(View.GONE);
        } else {
            mRootTitleBar.setVisibility(View.VISIBLE);
            mRootTitleBar.addView(titleView);
        }
        View bodyView = inflater.inflate(bodyLayoutResId, null, false);
        mRootLayout.addView(bodyView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        unbinder = ButterKnife.bind(this, bodyView);
        initView(bodyView);
        initData();
    }

    /**
     * 设置fragment要显示layout
     */
    protected abstract int setBodyLayout();

    /**
     * 实例化view,设置click事件
     */
    protected abstract void initView(View view);

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 无网络，网络异常时,重新加载按钮事件
     * 此抽象方法名与通用无网络页面中的布局点击事件同名
     *
     * @param view
     */
    public void reLoad(View view) {
        checkNet();
    }

    public void checkNet() {
        if (!NetUtil.isNetworkAvailable(mContext)) {
            noNet(-1);
        } else {
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
            vsNoNet = (ViewStub) getView().findViewById(R.id.vs_no_net);
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
        if (mRootLayout != null) {
            mRootLayout.setVisibility(goneOrVisible);
        }
    }

    private void initNoDataLayout(int isGone) {
        if (vwNoNet != null) {
            vwNoNet.setVisibility(View.GONE);
        }
        if (vwNoData == null) {
            vsNoData = (ViewStub) getView().findViewById(R.id.vs_no_data);
            vwNoData = vsNoData.inflate();
        } else {
            vwNoData.setVisibility(isGone);
        }
    }

    @Override
    public void fail(final int tag, final String error) {
        getActivity().runOnUiThread(new Runnable() {
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
        switch (noNetShowMode) {
            case VIEW_SHOW:
                visibleNoNet();
                break;
            default:
                showToast(mContext.getResources().getString(R.string.network_not_connected));
                break;
        }
    }

    @Override
    public void onDestroy() {
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
        if (mBundle != null) {
            mBundle.clear();
            mBundle = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
