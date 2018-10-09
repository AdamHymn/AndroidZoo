package zoo.hymn.views.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PayUtils {

	private Context ctx;
	private static String orderId;//订单ID
	private static String subject;//暂时写死了，可以为空
	private static String body;//订单num
	private static String price;//订单价格
	// 支付回调地址
	public static final String NotifyAddress = "http://api.365wanmeng.com/btsUserPayNotify/Alipay";
	// 商户PID
	public static final String PARTNER = "2088121586421408";
	// 商户收款账号
	public static final String SELLER = "2939311580@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALsQswAmJgFlFuH1eayRnS+glb3xb0DODtXqwk4+eWc/LFfZzmZKVT4q5zYBIbt89qSw8mX8wLT9YMeGT3haGihsDfCQE5ZQfSW9ipjddM82T5XuJwTd5NmZL2yyZx8XjM7zoOlwdJLxSm7fJk19tEJJDmpEbLV/x/oMhcHFn+YNAgMBAAECgYEAs3syQ9JWZMA5VFPSimCsECZjhftAY01Rrq2wAKKwmP9j7GZuQnbPkvZXINm1I86iVyHvcSsxOnZhhQ648SCCNuFiv6qzW1a43ks1qxnZ6PcOtooRD3arexEbv+If0y6pb1FOEiHU/bD/FTV7EmsgHs35DV+vtuHsTDo8An+8eAECQQDrlneUtciDVXSGC7pnVeCqiDVguJ6PEvTUKlk6umy/X93L5IBryXYjJHR/0ynUvwp9/VmghhO7RbNFTrwLR9tRAkEAy0X2QaPesl2K5dvtpBbXzugm/4aHG2FCdGn5SjVGBdo49frM4M/70fCJuhBGB1XYXvet6upRcYH1U5+FKLH3/QJBAIsent0dgBw2YYoq8NfG+dYae1LzkwawTg3gODMbFc7pYMPf2wpLvMzB4V3+p8wvSJXf/4ZwxsQnQ9cgyKDsvRECQQC/rRsGzmUiP/7HvNibUEbzfgm4Dby83rtxVrSReyaqQhpQKMykeLp2PqFAaDQAmrxK4LaIu6dMp0UUM2m29AppAkBj451H/8JaxASjx8PPab+XT4K575rk1lIBhP+Y9d56521mq0NVb"
			+ "Jf7yn85GX8ERctMk/59yMyScAyi1kk6/fHq";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306"
			+ "Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/"
			+ "fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FC"
			+ "Ea3/cNMW0QIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	public PayUtils(Context ctx, String orderId, String subject, String body,
			String price) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		PayUtils.orderId = orderId;
		PayUtils.subject = subject;
		PayUtils.body = body;
		PayUtils.price = price;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
        @SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				((Activity) ctx).finish();
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(ctx, "支付成功", Toast.LENGTH_SHORT).show();
					ctx.startActivity(new Intent(ctx,
							AliPayDemoActivity.class).putExtra("result",
							true).putExtra("OrderID", orderId));
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(ctx, "支付结果确认中", Toast.LENGTH_SHORT)
								.show();
						((Activity) ctx).finish();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(ctx, "支付失败", Toast.LENGTH_SHORT).show();
						ctx.startActivity(new Intent(ctx,
								AliPayDemoActivity.class)
								.putExtra("result", false)
								.putExtra("OrderID", orderId)
								.putExtra("subject", subject)
								.putExtra("body", body)
								.putExtra("price", price));
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(ctx)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
                                public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									((Activity) ctx).finish();
								}
							}).show();
			return;
		}
		String orderInfo = getOrderInfo();

		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(((Activity) ctx));
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(((Activity) ctx));
		String version = payTask.getVersion();
		Toast.makeText(ctx, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo() {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + body + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + "消费者端购物车下单" + "\"";
		// orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + NotifyAddress + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE,true);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
