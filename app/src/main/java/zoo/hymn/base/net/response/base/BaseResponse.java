package zoo.hymn.base.net.response.base;

/**
 * ClassName: BaseResponse
 * Function : 网络请求返回对象基类
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/16
 */
public class BaseResponse {
	public static final String FAIL    = "0";//失败
	public static final String SUCCESS = "1";//成功(有的后台开发人员返回0标示成功，有的返回1标示成功，请注意修改)
	public static final String TOKEN_INVALID  = "2";//token过期，退出账户，调整到登录页面
	public String status;//状态码
	public String result;//状态码
	public String msg;//消息
	public int responseCode = 200;
	public String responseMessage = "OK";
	public String responseContent = "";

}
