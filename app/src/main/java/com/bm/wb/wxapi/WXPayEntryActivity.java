package com.bm.wb.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bm.wb.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static zoo.hymn.views.pay.PayChooseActivity.PAY_RESULT_ACTION;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.wechat_key));

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		/**
		 * 0 成功 展示成功页面
		 * -1错误可能的原因：签名错误、未注册APPID、
		 * 项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
		 * -2 交易取消
		 */
		Log.e(TAG,resp.errCode+"");
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent().setAction(PAY_RESULT_ACTION).putExtra("errCode",resp.errCode));
			finish();
		}
	}

}