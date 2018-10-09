package zoo.hymn.views.pay;


import zoo.hymn.base.net.response.base.BaseResponse;

/**
 * ClassName: WxPayOrderBackModel
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/8/25
 */

public class WxPayOrderBackModel extends BaseResponse {
    /**
     * data : {"result_code":"SUCCESS","sign":"AF327720D2FADDA9BA17B59A8BF92E07","mch_id":"1486211822","prepay_id":"wx20170830143019d2a9e831a90713206662","return_msg":"OK","currentTimeMillis":"1504074619792","appid":"wxf6bb198de3458ff8","notifyUrl":"http://10.58.178.81:8018/financeTax//app/alipay/appPayResultReturn.do","nonce_str":"CEnTtmYVgZGadZt9","return_code":"SUCCESS","trade_type":"APP"}
     */

    public Data data;

    public static class Data {
        /**
         * result_code : SUCCESS
         * sign : AF327720D2FADDA9BA17B59A8BF92E07
         * mch_id : 1486211822
         * prepay_id : wx20170830143019d2a9e831a90713206662
         * return_msg : OK
         * currentTimeMillis : 1504074619792
         * appid : wxf6bb198de3458ff8
         * notifyUrl : http://10.58.178.81:8018/financeTax//app/alipay/appPayResultReturn.do
         * nonce_str : CEnTtmYVgZGadZt9
         * return_code : SUCCESS
         * trade_type : APP
         */

        public String result_code;
        public String sign;
        public String mch_id;
        public String prepay_id;
        public String return_msg;
        public String currentTimeMillis;
        public String appid;
        public String notifyUrl;
        public String nonce_str;
        public String return_code;
        public String trade_type;
    }
}
