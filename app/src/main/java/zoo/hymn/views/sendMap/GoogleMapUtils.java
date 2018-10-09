package zoo.hymn.views.sendMap;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * ClassName: GoogleMapUtils
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/24
 */

public class GoogleMapUtils {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static GoogleMapUtils amap;
    private GetGooleMapListener getGooleMapListener1;
    private boolean isfirst=true;
    private Context context;

    public static GoogleMapUtils getInstence() {
        if (amap == null) {
            amap = new GoogleMapUtils();
        }
        return amap;
    }

    public void init(final Context context ,GetGooleMapListener getGooleMapListener) {

        this.context =context;
        this.getGooleMapListener1=getGooleMapListener;
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                //    Log.v("sssssss","sssssss这个是地区id  比如海珠区"+aMapLocation);
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    if (getGooleMapListener1!=null) {
                        getGooleMapListener1.onMapListener(aMapLocation.getCity(), aMapLocation, true);
                    }


                } else {
                    if (getGooleMapListener1!=null) {
                        getGooleMapListener1.onMapListener("定位失败", null, false);
                    }
                }

            }
        };
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(30*60*1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public interface GetGooleMapListener{

        void onMapListener(String cityName, AMapLocation aMapLocation, boolean location);
    }}

