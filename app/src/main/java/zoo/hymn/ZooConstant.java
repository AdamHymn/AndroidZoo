package zoo.hymn;

import android.Manifest;

/**
 * ClassName: ZooConstant
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/13
 */

public class ZooConstant {

    public static int PAGE_SIZE = 10;

    /**
     * SharedPreferences键名相关
     */
    public static String KEY_FIRST_LAUNCH = "KEY_FIRST_LAUNCH";
    public static String IS_LOGIN = "IS_LOGIN";

    /**
     * 需要进行检测的权限数组
     */
    public static final String[] NEED_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };

    /**
     * 请求码、响应码相关
     */
    public static final int PERMISSON_REQUEST_CODE = 834;

    /**
     * 网络配置相关
     */
    public static String URL_MEDIA = "http://10.0.0.1:8088/";
    public static String URL = "http://10.0.0.1:8080/";
    public static String API = URL + "api/";

    /**
     * API接口相关
     */
    public static String uploadImageByType = API + "common/uploadImageByType";//根据类型上传图片

}
