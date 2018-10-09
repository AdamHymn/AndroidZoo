package zoo.hymn.base.net.response.base;

/**
 * ClassName: BasePageResponse
 * Function : 分页接收json数组
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/1/16
 */
public class BasePageResponse<T> extends BaseArrayResponse<T>{

    public int pageIndex;//页码
    public int pageSize;//每页条目数
    public int pageTotal;//总页数(有的后台开发人员返回这个单词)
    public int count;//总页数(有的后台开发人员返回这个单词)
}
