package zoo.hymn.views.pay;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bm.wb.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zoo.hymn.base.net.response.base.BaseDataResponse;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.net.response.base.BaseStringResponse;
import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.views.EaseTitleBar;
import zoo.hymn.views.pay.alipay.AuthResult;
import zoo.hymn.views.pay.alipay.PayResult;
import zoo.hymn.views.pay.wxpay.MD5Util;


/**
 * ClassName: PayChooseActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/6/26
 */

public class PayChooseActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Button buy_now;
    private RadioGroup zhifu;
    private RadioButton rb_ye;
    private CheckBox cb_ye;
    /**
     * 账户支付 0
     * 支付宝支付 1
     * 微信支付2
     */
    private int payType = 1;
    private TextView tv_sum;
    private float balance = 0f;
    private float deposit = 0f;
    private float totalPrice = 0f;
    private String upkeepId = "0";


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        try {
            deposit = Float.parseFloat(getIntent().getStringExtra("deposit"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            totalPrice = Float.parseFloat(getIntent().getStringExtra("totalPrice"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            balance = Float.parseFloat(getIntent().getStringExtra("balance"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            upkeepId = getIntent().getStringExtra("upkeepId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addChildView(R.layout.pay_choose_act);
        LocalBroadcastManager.getInstance(this).registerReceiver(PayResultBroadcastReceiver, new IntentFilter(PAY_RESULT_ACTION));
    }

    @Override
    protected void initView() {
        buy_now = (Button) findViewById(R.id.btn_buy);
        zhifu = (RadioGroup) findViewById(R.id.rg_zhifu);
        rb_ye = (RadioButton) findViewById(R.id.rb_ye);
        cb_ye = (CheckBox) findViewById(R.id.cb_ye);
        tv_sum = (TextView) findViewById(R.id.tv_sum);
        zhifu.setOnCheckedChangeListener(this);
        buy_now.setOnClickListener(this);

        if(deposit > 0) {
            tv_sum.setText("维修保证金：¥ " + deposit + "元");
        }else{
            deposit = totalPrice;
            tv_sum.setText("订单金额：¥ " + totalPrice + "元");
        }

        if (balance >= deposit) {
            //3选1支付
            rb_ye.setVisibility(View.VISIBLE);
            cb_ye.setVisibility(View.GONE);
            rb_ye.setText("账户余额抵扣：¥ "+deposit+"元");
        }
        if (balance < deposit) {
            //3选2支付
            rb_ye.setVisibility(View.GONE);
            cb_ye.setVisibility(View.VISIBLE);
            cb_ye.setText("账户余额抵扣：¥ "+balance+"元");
        }
        if (balance <= 0) {
            rb_ye.setVisibility(View.GONE);
            cb_ye.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {

        ((EaseTitleBar) defaultTitleView).setTitle("支付");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(PayResultBroadcastReceiver);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

        switch (i) {
            case R.id.rb_zfb:
                payType = 1;
                break;
            case R.id.rb_wx:
                payType = 2;
                break;
            case R.id.rb_ye:
                payType = 0;
                break;
        }
    }

    @Override
    public void onClick(View view) {

        //支付保证金
        PayItem payItem = new PayItem();
        payItem.upkeepId = upkeepId;

        if (balance >= deposit) {
            //3选1支付
            Items item = new Items();
            item.payType = payType;
            item.amount = deposit;
            payItem.items.add(item);
        }
        if (balance < deposit) {
            //3选2支付
            if (cb_ye.isChecked()) {
                Items item = new Items();
                item.payType = 0;
                item.amount = balance;
                payItem.items.add(item);


                Items item1 = new Items();
                item1.payType = payType;
                item1.amount = deposit - balance;
                payItem.items.add(item1);

            } else {
                //2选1支付
                Items item1 = new Items();
                item1.payType = payType;
                item1.amount = deposit;
                payItem.items.add(item1);
            }
        }

        switch (payType) {

            case 0:
//                APIMethods2.getInstance(this, this).postJsonByRole("/payUpkeep", payItem, BaseStringResponse.class, 0, R.string.loading);
                break;
            case 1:
//                APIMethods2.getInstance(this, this).postJsonByRole("/payUpkeep", payItem, BaseStringResponse.class, 0, R.string.loading);
                break;
            case 2:
//                APIMethods2.getInstance(this, this).postJsonByRole("/payUpkeep", payItem, WXResultResponse.class, 0, R.string.loading);
                break;
        }

    }

    class PayItem {
        PayItem() {
            items = new ArrayList<>();
        }

        public List<Items> items;
        public String upkeepId;
    }
    public class Items {
        public int payType;
        public float amount;
    }

    //***********************支付宝支付*************************************************************************************************************
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PayChooseActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(PayChooseActivity.this, PayResultActivity.class));
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayChooseActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(PayChooseActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(PayChooseActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

    };


    /**
     * 支付宝支付业务
     * @param orderInfo 获取必须来自服务端；
     */
    private void aliPay(final String orderInfo) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayChooseActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //***********************微信支付*************************************************************************************************************

    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    /**
     * 广播接收支付结果,用于微信支付结果回调
     */
    public static final String PAY_RESULT_ACTION = "com.pay.result.action";
    private BroadcastReceiver PayResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("Action:" + action);
            if (PAY_RESULT_ACTION.equals(action)) {
                int errCode = intent.getIntExtra("errCode", 0);
                if (errCode == 0) {
                    Toast.makeText(PayChooseActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PayChooseActivity.this, PayResultActivity.class));
                    finish();
                } else if (errCode == -1) {
                    Toast.makeText(PayChooseActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                } else if (errCode == -2) {
                    Toast.makeText(PayChooseActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PayChooseActivity.this, "支付异常", Toast.LENGTH_SHORT).show();
                }


            }
        }
    };

    /**
     * 调起微信支付业务
     *（简单说分三步：
     * 1.给PayReq赋值；
     * 2.注册APPID;
     * 3.发送PayReq给微信SDK）
     * @param wxResultBean 从服务器端获取
     */
    private void wechatPay(WXResultBean wxResultBean) {
        msgApi.registerApp(wxResultBean.appId);
        PayReq req = new PayReq();
        req.appId = wxResultBean.appId;
        req.partnerId = wxResultBean.partnerId;
        req.prepayId = wxResultBean.prepayId;
        req.nonceStr = wxResultBean.nonceStr;
        req.timeStamp = wxResultBean.timestamp;
        req.packageValue = "Sign=WXPay";

        //生成签名KEY
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signParams.size(); i++) {
            sb.append(signParams.get(i).getName());
            sb.append('=');
            sb.append(signParams.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(wxResultBean.sign);

        req.sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        msgApi.registerApp(req.appId);
        msgApi.sendReq(req);

    }

    class WXResultBean {
        /**
         * package : Sign=WXPay
         * appId :
         * sign : 8AE568AB3F53F2432202E0B3A04FCDC2
         * partnerId :
         * prepayId : null
         * nonceStr : 258046631997794
         * timestamp : 1514274661
         */

        public String appId;
        public String sign;
        public String partnerId;
        public String prepayId;
        public String nonceStr;
        public String timestamp;
    }

    class WXResultResponse extends BaseDataResponse<WXResultBean> {
    }


    @Override
    public void success(int tag, BaseResponse response) {
        switch (payType) {
            case 0:
                finish();
                break;
            case 1:
                BaseStringResponse baseStringResponse = (BaseStringResponse) response;
                aliPay(baseStringResponse.data);
                break;
            case 2:
                WXResultResponse wxResultResponse = (WXResultResponse) response;
                wechatPay(wxResultResponse.data);
                break;
        }

    }

}
