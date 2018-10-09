package zoo.hymn.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 高德定位
 */
public class LocationUtil implements AMapLocationListener {
    private static final String TAG = "LocationUtil";
    private Context context;
    private static LocationUtil location;
    private LocationInfo locationInfo;
    private boolean isStop = false;
    private LocationListener mListener;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;


    private LocationUtil(Context context) {
        this.context = context;
        initLocation();
        locationInfo = new LocationInfo();

    }

    public static LocationUtil getInstance(Context context) {
        if (location == null) {
            location = new LocationUtil(context);
        }
        return location;
    }

    /**
     * 实例化高等定位
     * <p>
     * /设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
     * "gcj02"; "bd09ll"; "bd09";
     */
    private void initLocation() {
        //声明mLocationOption对象
        mlocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(5*60*1000L);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
    }

    /**
     * startLoc 开始定位 (这里描述这个方法适用条件 – 可选)
     *
     * @since 1.0.0
     */
    public void startLoc(LocationListener listener) {
        if (listener != null) {
            mListener = listener;
        }
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        //设置定位监听
        mlocationClient.setLocationListener(this);
        mlocationClient.startLocation();
        Log.e(TAG, "开始定位");
    }

    /**
     * stopLoc 关闭定位 (这里描述这个方法适用条件 – 可选)
     *
     * @throws
     * @since 1.0.0
     */
    public void stopLoc() {
        mlocationClient.unRegisterLocationListener(this);
        mlocationClient.stopLocation();
        Log.e(TAG, "关闭定位");
    }

    /**
     * @return the info
     */

    public LocationInfo getInfo() {
//        if (info != null && info.latitude > 0) {
//            return info;
//        }
        return locationInfo;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(LocationInfo info) {
        this.locationInfo = info;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        stopLoc();
        LocationInfo info = getInfo();
        if (aMapLocation != null) {
            //定位成功回调信息，设置相关消息
            if (aMapLocation.getErrorCode() == 0) {
                int locationTpye = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                info.latitude = aMapLocation.getLatitude();//获取纬度
                info.longitude = aMapLocation.getLongitude();//获取经度
                info.locationTime = DateUtil.getCurrentDate("");//定位 时间
                switch (locationTpye) {
                    //定位结果类型：基站定位结果 属于网络定位
                    case AMapLocation.LOCATION_TYPE_CELL:
                        //Wifi定位结果 属于网络定位，定位精度相对基站定位会更好
                    case AMapLocation.LOCATION_TYPE_WIFI:
                        //缓存定位结果 返回一段时间前设备在相同的环境中缓存下来的网络定位结果，节省无必要的设备定位消耗
                    case AMapLocation.LOCATION_TYPE_FIX_CACHE:
                        //前次定位结果 网络定位请求低于1秒、或两次定位之间设备位置变化非常小时返回，设备位移通过传感器感知
                    case AMapLocation.LOCATION_TYPE_SAME_REQ:
                        info.addr = aMapLocation.getAddress();
                        info.city = aMapLocation.getCity();
                        info.cityCode = aMapLocation.getCityCode();

                        Log.e(TAG,info.addr);
                        Log.e(TAG,info.city);
                        Log.e(TAG,info.cityCode);
                        break;
                    //GPS定位结果 通过设备GPS定位模块返回的定位结果
                    case AMapLocation.LOCATION_TYPE_GPS:
                        break;
                    //离线定位结果
                    case AMapLocation.LOCATION_TYPE_OFFLINE:
                        break;
                }
                if (mListener != null) {
                    mListener.locationLatLon(info);
                }
            } else {
                if (mListener != null) {
                    mListener.locationLatLon(null);
                }
//                //定位 失败
//                startLoc(null);
            }
        } else {
            if (mListener != null) {
                mListener.locationLatLon(null);
            }
//           //定位 失败
//            startLoc(null);
        }
    }

    public interface LocationListener {
        void locationLatLon(LocationInfo info);
    }

    public static class LocationInfo {
        /**
         * 访问时间
         */
        public String locationTime;
        /**
         * 返回码类型
         */
        public int errorType;
        /**
         * 纬度
         */
        public double latitude;
        /**
         * 经度
         */
        public double longitude;
        /**
         * 半经
         */
        public double radius;
        /**
         * 速度
         */
        public double speed;
        /**
         * 地址
         */
        public String addr;
        /**
         * 城市
         */
        public String city;

        /**
         * 城市代码
         */
        public String cityCode;


    }
}