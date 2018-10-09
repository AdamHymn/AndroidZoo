package zoo.hymn.base.net.response.base;

/**
 * ClassName: VingaResponse
 * Function : 可带冗余参数基类
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/4/6
 */

public class VingaResponse {
    public String ContentEncoding;
    public String ContentType;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;
    public BaseResponse Data;
}
