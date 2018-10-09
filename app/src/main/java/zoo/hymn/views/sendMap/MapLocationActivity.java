package zoo.hymn.views.sendMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bm.wb.R;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.views.EaseTitleBar;

/**
 * ClassName: MapLocationActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/24
 */

public class MapLocationActivity extends BaseActivity implements
        PoiSearch.OnPoiSearchListener {

    private MapView mMapView;
    private AMap aMap;
    private ImageView ivLocation;

    private TextView tvSearch;
    private Bundle savedInstanceState;

    private ListView listView;
    private SearchResultAdapter searchResultAdapter;
    private List<PoiItem> resultData;

    private AMapLocationClient mlocationClient;
    private Marker locationMarker, checkinMarker;
    private LatLonPoint searchLatlonPoint;
    private WifiManager mWifiManager;
    private PoiSearch poisearch;
    private Circle mcircle;
    private LatLng checkinpoint, mlocation;
    private boolean isItemClickAction, isLocationAction;

    private Marker locMarker;
    private Circle ac;
    private Circle c;
    private long start;
    private final Interpolator interpolator1 = new LinearInterpolator();
    //org.apache.commons.lang3.concurrent.BasicThreadFactory
    private ScheduledExecutorService executorService;

    private static final float ZOOM_VALUE = 16f;
    private static final float CIRCLE_RADIUS = 10000000f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        addChildView(R.layout.activity_map_location);
    }

    @Override
    protected void initView() {
        ((EaseTitleBar) defaultTitleView).setTitle("位置");
        mMapView = (MapView) findViewById(R.id.gs_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        ivLocation = (ImageView) findViewById(R.id.iv_loaction);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setTrafficEnabled(false);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.getUiSettings().setZoomControlsEnabled(false);

        resultData = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(this);
        searchResultAdapter.setData(resultData);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(searchResultAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != searchResultAdapter.getSelectedPosition()) {
                    //选择后，将地图中心移动到所选择数据
                    PoiItem poiItem = (PoiItem) searchResultAdapter.getItem(position);
                    LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                    isItemClickAction = true;
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(curLatlng));
                    searchResultAdapter.setSelectedPosition(position);
                    searchResultAdapter.notifyDataSetChanged();

                    //选择后，将选择数据返回给上级页面
                    Intent data = new Intent();
                    data.putExtra("data", poiItem);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    //选择后，将选择数据返回给上级页面
                    Intent data = new Intent();
                    data.putExtra("data", (PoiItem) searchResultAdapter.getItem(position));
                    setResult(RESULT_OK, data);
                    finish();
                }


            }
        });

    }

    @Override
    protected void initData() {
        //初始化定位
        initListener();
        //开始定位
        startLocation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (resultData != null && resultData.size() > 0) {
            //选择后，将选择数据返回给上级页面
            Intent data = new Intent();
            data.putExtra("data", resultData.get(0));
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void initListener() {

        //初始化定位client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            /**
             * 返回定位结果的回调
             * @param aMapLocation 定位结果
             */
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null
                        && aMapLocation.getErrorCode() == 0) {
                    mlocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    searchLatlonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    checkinpoint = mlocation;
                    isLocationAction = true;
                    searchResultAdapter.setSelectedPosition(0);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlocation, ZOOM_VALUE));
                    addLocationMarker(aMapLocation);
                    if (mcircle != null) {
                        mcircle.setCenter(mlocation);
                    } else {
                        mcircle = aMap.addCircle(new CircleOptions().center(mlocation).radius(CIRCLE_RADIUS).strokeWidth(5));
                    }
                    if (searchLatlonPoint != null) {
                        resultData.clear();
                        resultData.add(new PoiItem("ID", searchLatlonPoint, "我的位置", searchLatlonPoint.toString()));
                        doSearchQuery(searchLatlonPoint);
                        searchResultAdapter.notifyDataSetChanged();
                    }

                } else {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
                    showToast("定位失败");
                }
            }
        });

        //设置地图画面移动的监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            /**
             * 地图移动过程回调
             * @param cameraPosition
             */
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                doSearchQuery(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude));

            }

            /**
             * 地图移动结束回调
             * 在这里判断移动距离有无超过500米
             * @param cameraPosition
             */
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!isItemClickAction && !isLocationAction) {
                    searchResultAdapter.setSelectedPosition(-1);
                    searchResultAdapter.notifyDataSetChanged();
                }
                if (isItemClickAction) {
                    isItemClickAction = false;
                }
                if (isLocationAction) {
                    isLocationAction = false;
                }

                if (mcircle != null) {
                    if (mcircle.contains(cameraPosition.target)) {
                        checkinpoint = cameraPosition.target;
                    } else {
                        Toast.makeText(MapLocationActivity.this, "微调距离不可超过" + CIRCLE_RADIUS + "米", Toast.LENGTH_SHORT).show();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlocation, ZOOM_VALUE));
                    }
                } else {
                    startLocation();
                    Toast.makeText(MapLocationActivity.this, "重新定位中。。。", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //设置地图加载完成监听
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            /**
             * 地图加载完成回调
             */
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter();
            }
        });
//
//        //是否显示地图中放大缩小按钮
//        mUiSettings.setZoomControlsEnabled(false);
//        mUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
//        aMap.setMyLocationEnabled(false);// 是否可触发定位并显示定位层
//
//        //不设置触摸地图的时候会报错
//        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//            }
//        });
        ivLocation.setOnClickListener(onClickListener);
        tvSearch.setOnClickListener(onClickListener);
    }


    //定位
    private void location() {
        GoogleMapUtils.getInstence().init(this, new GoogleMapUtils.GetGooleMapListener() {
            @Override
            public void onMapListener(String cityName, AMapLocation aMapLocation, boolean location) {
                if (location) {
                    if (!TextUtils.isEmpty(aMapLocation.getCityCode()) && !TextUtils.isEmpty(aMapLocation.getRoad())) {
                        //把地图移动到定位地点
                        moveMapCamera(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        addLocationMarker(aMapLocation);
                    }
                } else {
                    showToast("定位失败");
                }
            }
        });
    }

    //把地图画面移动到定位地点
    private void moveMapCamera(double latitude, double longitude) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_VALUE));
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_loaction:
                    location();
                    break;
                case R.id.tv_search:
                    Intent intent = new Intent();
                    intent.setClass(MapLocationActivity.this, LocationActivity.class);
                    startActivityForResult(intent, 1);
                    break;

            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // 获取返回的数据
                    PoiItem poiItem = (PoiItem) data.getParcelableExtra("data");
                    moveMapCamera(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                }

            }
        }
    }

    /**
     * 设置定位参数
     *
     * @return 定位参数类
     */
    private AMapLocationClientOption getOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setLocationCacheEnable(false);//设置是否返回缓存中位置，默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        return mOption;
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        checkWifiSetting();
        //设置定位参数
        mlocationClient.setLocationOption(getOption());
        // 启动定位
        mlocationClient.startLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
            mlocationClient = null;
        }
    }


    /**
     * 添加选点marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    /**
     * 检查wifi，并提示用户开启wifi
     */
    private void checkWifiSetting() {
        if (mWifiManager.isWifiEnabled()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("开启WIFI模块会提升定位准确性"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent); // 打开系统设置界面
            }
        });
        builder.setNegativeButton("不了", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        destroyLocation();
    }


    /**
     * 搜索周边poi
     *
     * @param centerpoint
     */
    private void doSearchQuery(LatLonPoint centerpoint) {
        PoiSearch.Query query = new PoiSearch.Query("", "", "");
        query.setPageSize(20);
        query.setPageNum(0);
        poisearch = new PoiSearch(this, query);
        poisearch.setOnPoiSearchListener(this);
        poisearch.setBound(new PoiSearch.SearchBound(centerpoint, 500, true));
        poisearch.searchPOIAsyn();
    }

    /**
     * 搜索Poi回调
     *
     * @param poiResult  搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {

        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getPois().size() > 0) {
                List<PoiItem> poiItems = poiResult.getPois();
                resultData.clear();
                resultData.addAll(poiItems);
                searchResultAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MapLocationActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MapLocationActivity.this, "搜索失败，错误 " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ID搜索poi的回调
     *
     * @param poiItem    搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int resultCode) {

    }

    /**
     * 顶点签到，将签到点标注在地图上
     */
    private void checkin() {
        if (checkinMarker == null) {
            if (checkinpoint != null) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                checkinMarker = aMap.addMarker(new MarkerOptions().position(checkinpoint).title("签到").snippet(date));
                Toast.makeText(MapLocationActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
            } else {
                startLocation();
                Toast.makeText(MapLocationActivity.this, "请定位后重试，定位中。。。", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MapLocationActivity.this, "今日已签到", Toast.LENGTH_SHORT).show();
        }
    }


    private void addLocationMarker(AMapLocation aMapLocation) {
        LatLng mylocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        float accuracy = aMapLocation.getAccuracy();
        if (locMarker == null) {
            locMarker = addMarker(mylocation);
            ac = aMap.addCircle(new CircleOptions().center(mylocation)
                    .fillColor(Color.argb(100, 255, 218, 185)).radius(accuracy)
                    .strokeColor(Color.argb(255, 255, 228, 185)).strokeWidth(5));
            c = aMap.addCircle(new CircleOptions().center(mylocation)
                    .fillColor(Color.argb(70, 255, 218, 185))
                    .radius(accuracy).strokeColor(Color.argb(255, 255, 228, 185))
                    .strokeWidth(0));
        } else {
            locMarker.setPosition(mylocation);
            ac.setCenter(mylocation);
            ac.setRadius(accuracy);
            c.setCenter(mylocation);
            c.setRadius(accuracy);
        }
        Scalecircle(c);
    }


    private Marker addMarker(LatLng point) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        Marker marker = aMap.addMarker(new MarkerOptions().position(point).icon(des)
                .anchor(0.5f, 0.5f));
        return marker;
    }


    public void Scalecircle(final Circle circle) {
        start = SystemClock.uptimeMillis();
        executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //do something
                double r = circle.getRadius();
                long duration = 1000;

                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float input = (float) elapsed / duration;
//                外圈循环缩放
//                float t = interpolator.getInterpolation((float)(input-0.25));//return (float)(Math.sin(2 * mCycles * Math.PI * input))
//                double r1 = (t + 2) * r;
//                外圈放大后消失
                    float t = interpolator1.getInterpolation(input);
                    double r1 = (t + 1) * r;
                    circle.setRadius(r1);
                    if (input > 2) {
                        start = SystemClock.uptimeMillis();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, 0, 30, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClick(View view) {

    }


}
