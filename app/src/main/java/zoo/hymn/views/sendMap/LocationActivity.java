package zoo.hymn.views.sendMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bm.wb.R;

import java.util.ArrayList;
import java.util.List;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.views.EaseTitleBar;

/**
 * ClassName: gf
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/24
 */

public class LocationActivity extends BaseActivity {
    private PoiSearch.Query poiquery;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private LoactionAddressAdapter loactionAddressAdapter;
    private List<LocationAddress> locationAddressList;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.activity_location);
//        initListener();
    }

    private void initListener() {
//        loactionAddressAdapter.setItemListener(new LoactionAddressAdapter.OnItemListener() {
//            @Override
//            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {
//                Intent data = new Intent();
//                data.putExtra("data", locationAddressList.get(position).getPoiItem());
//                setResult(RESULT_OK, data);
//                finish();
//            }
//        });
    }

    @Override
    protected void initData() {
        GoogleMapUtils.getInstence().init(this, new GoogleMapUtils.GetGooleMapListener() {
            @Override
            public void onMapListener(String cityName, AMapLocation aMapLocation, boolean location) {
                if (true) {
                    if (!TextUtils.isEmpty(aMapLocation.getCityCode()) && !TextUtils.isEmpty(aMapLocation.getRoad())) {
                        code = aMapLocation.getCityCode();
                        searchList(aMapLocation.getCityCode(), aMapLocation.getRoad());
                    }
                } else {
                    showToast("定位失败");
                }
            }
        });

        etSearch.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            searchList(code, etSearch.getText().toString().trim());
        }
    };

    @Override
    protected void initView() {
        ((EaseTitleBar)defaultTitleView).setTitle("位置");
        etSearch = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.search_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationAddressList = new ArrayList<>();
        loactionAddressAdapter = new LoactionAddressAdapter(this, locationAddressList);
        recyclerView.setAdapter(loactionAddressAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent data = new Intent();
                        data.putExtra("data", locationAddressList.get(position).getPoiItem());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }));

    }

    private void searchList(String cityCode, String road) {
        if (TextUtils.isEmpty(road)) {
            locationAddressList.clear();
            loactionAddressAdapter.notifyDataSetChanged();
        }
        poiquery = new PoiSearch.Query(road, "", cityCode);
        poiquery.setPageSize(15);
        poiquery.setPageNum(2);
        PoiSearch poiSearch = new PoiSearch(this, poiquery);
        poiSearch.setOnPoiSearchListener(onPoiSearchListener);
        poiSearch.searchPOIAsyn();
    }


    //索引搜索
    PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(poiquery)) {// 是否是同一条
                        //     poiResult = result;
                        // 取得搜索到的poiitems有多少页
                        List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        List<SuggestionCity> suggestionCities = result
                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                        locationAddressList.clear();
                        if (poiItems != null && poiItems.size() > 0) {
                            for (int i = 0; i < poiItems.size(); i++) {
                                LocationAddress locationAddress = new LocationAddress();
                                locationAddress.setPoiItem(poiItems.get(i));
                                locationAddressList.add(locationAddress);
                            }
                        }
                        loactionAddressAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    @Override
    public void onClick(View view) {

    }
}
