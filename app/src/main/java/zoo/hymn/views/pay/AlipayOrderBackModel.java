package zoo.hymn.views.pay;


import zoo.hymn.base.net.response.base.BaseResponse;

/**
 * ClassName: AlipayOrderBackModel
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/8/25
 */

public class AlipayOrderBackModel extends BaseResponse {
    /**
     * data : {"createOrderInfo":{"cutOrderId":"CRD1503648407106483179","cutSeqId":"f604de32cec94479b953c484b9a11534","cutCompType":"1","businessName":"","businessId":"PRODID-2017-00000011","orderType":"1","buyCredentialsNumber":"200","serverMouth":"0","buyAmount":"2000.00","buyUserId":"TX17A000045","buyUserName":"霸气","companyAddress":"中午","professorId":"","professorName":"","paymentFlag":"0","givingCycle":"","serverStartTime":"","serverEndTime":"","credentialsAmount":"200.00","productAmount":"0.00","productInfoList":"","batTaxNumber":"0","orderStateName":"","sysPayWay":"","createDate":"2017/08/25 16:06:47"},"aliPayInfo":"alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017072507893516&biz_content=%7B%22body%22%3A%22%E8%B4%A2%E6%94%BE%E5%BF%83%E6%9C%8D%E5%8A%A1%22%2C%22out_trade_no%22%3A%22CRD1503648407106483179%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E8%B4%A2%E6%94%BE%E5%BF%83%E6%9C%8D%E5%8A%A1%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%222000.00%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F10.58.178.81%3A8018%2FfinanceTax%2F%2Fapp%2Falipay%2FappPayResultReturn.do&sign=RKfqoRDDeJjsQrqrrroyV6V0MczYF%2BeETSaNqlJHyGvrqIVX3i%2F7dGsEVXiU3F%2BL9h1IgHutgs%2BbxHq0XiXUZeYEK7mX12ceGG1GTGtlPsFhKpohU%2BjvRx1v1HWcgaqFa0qIBsvWWmJrF9E8hY0S%2BoJZHtiSNA40YIvxo%2FJ85jCCYRjoHeaAM6%2BGNcHnO%2BjzQ4HzMM%2FzqjTIGTK%2F9fNCcQzhsYM%2BV9x1k%2Bu3lgjKP0Gbh5XHQKNLb8v07FEN%2B2fv%2BfNxfafdDEuoqY%2F4Ath7l9NUnq2GcnDyEV%2F6iJtVzXha0tR05yR43o%2FkJLa5fD9%2FuFXU5hIflPVGR4RMMXCNrA%3D%3D&sign_type=RSA2&timestamp=2017-08-25+16%3A24%3A33&version=1.0"}
     */

    public Data data;

    public static class Data {
        /**
         * createOrderInfo : {"cutOrderId":"CRD1503648407106483179","cutSeqId":"f604de32cec94479b953c484b9a11534","cutCompType":"1","businessName":"","businessId":"PRODID-2017-00000011","orderType":"1","buyCredentialsNumber":"200","serverMouth":"0","buyAmount":"2000.00","buyUserId":"TX17A000045","buyUserName":"霸气","companyAddress":"中午","professorId":"","professorName":"","paymentFlag":"0","givingCycle":"","serverStartTime":"","serverEndTime":"","credentialsAmount":"200.00","productAmount":"0.00","productInfoList":"","batTaxNumber":"0","orderStateName":"","sysPayWay":"","createDate":"2017/08/25 16:06:47"}
         * aliPayInfo : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017072507893516&biz_content=%7B%22body%22%3A%22%E8%B4%A2%E6%94%BE%E5%BF%83%E6%9C%8D%E5%8A%A1%22%2C%22out_trade_no%22%3A%22CRD1503648407106483179%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E8%B4%A2%E6%94%BE%E5%BF%83%E6%9C%8D%E5%8A%A1%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%222000.00%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F10.58.178.81%3A8018%2FfinanceTax%2F%2Fapp%2Falipay%2FappPayResultReturn.do&sign=RKfqoRDDeJjsQrqrrroyV6V0MczYF%2BeETSaNqlJHyGvrqIVX3i%2F7dGsEVXiU3F%2BL9h1IgHutgs%2BbxHq0XiXUZeYEK7mX12ceGG1GTGtlPsFhKpohU%2BjvRx1v1HWcgaqFa0qIBsvWWmJrF9E8hY0S%2BoJZHtiSNA40YIvxo%2FJ85jCCYRjoHeaAM6%2BGNcHnO%2BjzQ4HzMM%2FzqjTIGTK%2F9fNCcQzhsYM%2BV9x1k%2Bu3lgjKP0Gbh5XHQKNLb8v07FEN%2B2fv%2BfNxfafdDEuoqY%2F4Ath7l9NUnq2GcnDyEV%2F6iJtVzXha0tR05yR43o%2FkJLa5fD9%2FuFXU5hIflPVGR4RMMXCNrA%3D%3D&sign_type=RSA2&timestamp=2017-08-25+16%3A24%3A33&version=1.0
         */

        public CreateOrderInfo createOrderInfo;
        public String aliPayInfo;

        public static class CreateOrderInfo {
            /**
             * cutOrderId : CRD1503648407106483179
             * cutSeqId : f604de32cec94479b953c484b9a11534
             * cutCompType : 1
             * businessName :
             * businessId : PRODID-2017-00000011
             * orderType : 1
             * buyCredentialsNumber : 200
             * serverMouth : 0
             * buyAmount : 2000.00
             * buyUserId : TX17A000045
             * buyUserName : 霸气
             * companyAddress : 中午
             * professorId :
             * professorName :
             * paymentFlag : 0
             * givingCycle :
             * serverStartTime :
             * serverEndTime :
             * credentialsAmount : 200.00
             * productAmount : 0.00
             * productInfoList :
             * batTaxNumber : 0
             * orderStateName :
             * sysPayWay :
             * createDate : 2017/08/25 16:06:47
             */

            public String cutOrderId;
            public String cutSeqId;
            public String cutCompType;
            public String businessName;
            public String businessId;
            public String orderType;
            public String buyCredentialsNumber;
            public String serverMouth;
            public String buyAmount;
            public String buyUserId;
            public String buyUserName;
            public String companyAddress;
            public String professorId;
            public String professorName;
            public String paymentFlag;
            public String givingCycle;
            public String serverStartTime;
            public String serverEndTime;
            public String credentialsAmount;
            public String productAmount;
            public String productInfoList;
            public String batTaxNumber;
            public String orderStateName;
            public String sysPayWay;
            public String createDate;
        }
    }
}
