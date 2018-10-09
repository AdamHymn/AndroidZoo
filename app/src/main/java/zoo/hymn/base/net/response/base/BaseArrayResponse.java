package zoo.hymn.base.net.response.base;

/**
 * ClassName: BaseArrayResponse
 * Function : data字段为数组[]类型继承此类
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/16
 */
public class BaseArrayResponse<T> extends BaseResponse {
	public T[] data;
}
