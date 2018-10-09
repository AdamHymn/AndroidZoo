package com.bm.wb.ui.zoo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bm.wb.R;

import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.BitmapUtil;
import zoo.hymn.views.EaseTitleBar;

/**
 * ClassName: MapActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/23
 */


public class MapActivity extends BaseActivity {
    MapView mMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.ac_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        AMap aMap = null;
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        ((EaseTitleBar) defaultTitleView).setTitle("位置");
        if (getIntent().getBooleanExtra("showAll", false)) {
//            APIMethods.getInstance(mContext, this).myWorkmates("", 0);
        } else {
            addMarker(
                    getIntent().getDoubleExtra("lat", 34.18572),
                    getIntent().getDoubleExtra("lng", 108.88163),
                    getIntent().getStringExtra("avataUrl"),
                    getIntent().getStringExtra("nickname"), 0);
        }
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {

    }

    private void addMarker(final double lat, final double lng, String iconUrl, final String title, final int indexCenter) {
        try {
            if (lng == 0 || lat == 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

//        if (StrUtil.isEmpty(iconUrl)) {
        LatLng point = null;
        try {
            point = new LatLng(lat, lng);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bMap = BitmapUtil.toRoundBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.purple_pin));
//            if (getIntent().getBooleanExtra("showAll", false)) {
//                bMap = BitmapUtil.toRoundBitmap(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.touxiang));
        bMap = BitmapUtil.overrideBitmap(bMap, 120.f, 120.f);
//            }
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        mMapView.getMap().addMarker(new MarkerOptions().position(point).icon(des)
                .anchor(0.5f, 0.5f).title(title));
        moveMapCamera(lat, lng);
//            return;
//        }


//        Bitmap bMap = BitmapUtil.getBitmapFromUrl(iconUrl);

//        APIMethods2.getInstance(this, this).doGetByNative(iconUrl, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                try {
//                    Bitmap bMap = BitmapUtil.toRoundBitmap(BitmapFactory.decodeResource(getResources(),
//                            R.drawable.touxiang));
//                    bMap = BitmapUtil.overrideBitmap(bMap,150.f,150.f);
//                    BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
//                    mMapView.getMap().addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(des)
//                            .anchor(0.5f, 0.5f).title(title));
//                    if (indexCenter == 0) {
//                        try {
//                            moveMapCamera(lat, lng);
//                        } catch (Exception e2) {
//                            e2.printStackTrace();
//                        }
//                    }
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Bitmap bMap = BitmapUtil.toRoundBitmap(BitmapFactory.decodeStream(response.body().byteStream()));
//                            bMap = BitmapUtil.overrideBitmap(bMap,150.f,150.f);
//                            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
//                            mMapView.getMap().addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(des)
//                                    .anchor(0.5f, 0.5f).title(title));
//                            if (indexCenter == 0) {
//                                try {
//                                    moveMapCamera(lat, lng);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });

    }

    @Override
    public void success(int tag, final BaseResponse response) {
        super.success(tag, response);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                workResponse = (WorkResponse) response;
//                if (workResponse.data != null && workResponse.data.length > 0) {
//                    for (int i = 0; i < workResponse.data.length; i++) {
//                        addMarker(workResponse.data[i].lat, workResponse.data[i].lng, workResponse.data[i].avataUrl, workResponse.data[i].nickname, i);
//                    }
//                }
//            }
//        });
    }


    private static final float ZOOM_VALUE = 16f;

    //把地图画面移动到定位地点
    private void moveMapCamera(double latitude, double longitude) {
        mMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_VALUE));
    }
}
