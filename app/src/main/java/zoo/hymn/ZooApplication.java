package zoo.hymn;

import android.app.Application;

import org.litepal.LitePal;

import io.rong.imkit.RongIM;
import zoo.hymn.utils.LocationUtil;

/**
 * ClassName: ZooApplication
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/11
 */

public class ZooApplication extends Application {

    public static LocationUtil.LocationInfo locationInfo = null;

    @Override
    public void onCreate() {
        super.onCreate();
        /*** 初始化数据库框架LitePal  */
        LitePal.initialize(this);
        /*** 初始化即时通信框架融云  */
        RongIM.init(this);
        /*** 初始化定位  */
//        LocationUtil.getInstance(this).startLoc(new LocationUtil.LocationListener() {
//            @Override
//            public void locationLatLon(LocationUtil.LocationInfo info) {
//                locationInfo = info;
//                if (locationInfo != null
//                        && !StrUtil.isEmpty(locationInfo.city)
//                        && locationInfo.longitude != 0
//                        && locationInfo.latitude != 0
//                        ) {
//                    OkHttpClient mClient = new OkHttpClient.Builder()
//                            .connectTimeout(10, TimeUnit.SECONDS)
//                            .writeTimeout(15, TimeUnit.SECONDS)
//                            .build();
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("lng", "" + info.longitude)
//                            .add("lat", "" + info.latitude)
//                            .add("address", info.addr)
//                            .add("cityName", info.city)
//                            .build();
//                    LoginBean loginBean = DataSupport.findFirst(LoginBean.class);
//                    if (loginBean != null) {
//
//                        final Request request = new Request.Builder()
//                                .url(API + loginBean.userPart + "/" + loginBean.role + updateUserInfo)
//                                .addHeader("authorization", SharedPreferencesUtil.get(getApplicationContext(), "TOKEN"))
//                                .post(requestBody)
//                                .build();
//                        Log.i("RequestParams", request.toString());
//
//                        mClient.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                System.err.println("onFailure"+e.getMessage());
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                System.err.println("onResponse"+response.body().string());
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onTerminate() {
        LocationUtil.getInstance(this).stopLoc();
        super.onTerminate();
    }
}
