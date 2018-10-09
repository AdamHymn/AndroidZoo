package zoo.hymn.views.citylistview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bm.wb.R;
import zoo.hymn.utils.FileUtil;
import zoo.hymn.utils.StrUtil;
import zoo.hymn.views.ClearEditText;


public class CityListSampleActivity extends Activity implements View.OnClickListener {
    RadioButton radioButton6;
    RadioButton radioButton5;
    ListView countryLvcountry;
    SideBar sidrbar;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView curSideBarLetter, tvLocationCity;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<CityBean> sourceDataList, chrSourceDataList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_city_list);
        initViews();
        init();
    }

    private void init() {
        radioButton6 = (RadioButton) findViewById(R.id.radioButton6);
        radioButton5 = (RadioButton) findViewById(R.id.radioButton5);
        countryLvcountry = (ListView) findViewById(R.id.country_lvcountry);
        sidrbar = (SideBar) findViewById(R.id.sidrbar);
        radioButton5.setOnClickListener(this);
        radioButton6.setOnClickListener(this);
    }

    private void initViews() {

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        tvLocationCity = (TextView) findViewById(R.id.tvLocationCity);
        curSideBarLetter = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(curSideBarLetter);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (adapter != null) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }

            }
        });

        sourceDataList = new ArrayList<>();
        if (flag) {
            try {
                JSONObject jsonObject = new JSONObject(FileUtil.readJSONFromAssets(this, "cityMapping.json"));
                JSONArray jsonArray = jsonObject.getJSONArray("RECORDS");
                for (int i = 0; i < jsonArray.length(); i++) {
                    CityBean cb = new CityBean();
                    cb.PROVINCE_ID = jsonArray.getJSONObject(i).getString("PROVINCE_ID");
                    cb.CITY_ID = jsonArray.getJSONObject(i).getString("CITY_ID");
                    cb.CITY_NAME = jsonArray.getJSONObject(i).getString("CITY_NAME");
                    sourceDataList.add(cb);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(FileUtil.readJSONFromAssets(this, "cityMapping.json"));
                JSONArray jsonArray = jsonObject.getJSONArray("RECORDS");
                for (int i = 0; i < jsonArray.length(); i++) {
                    CityBean cb = new CityBean();
                    cb.PROVINCE_ID = jsonArray.getJSONObject(i).getString("PROVINCE_ID");
                    cb.CITY_ID = jsonArray.getJSONObject(i).getString("CITY_ID");
                    cb.CITY_NAME = jsonArray.getJSONObject(i).getString("CITY_NAME");
                    sourceDataList.add(cb);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        adapter = new SortAdapter(this, chrSourceDataList = filledData(sourceDataList));
        sortListView.setAdapter(adapter);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Toast.makeText(getApplication(), ((CityBean)adapter.getItem(position)).CITY_NAME, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("CITY_NAME", ((CityBean) adapter.getItem(position)).CITY_NAME);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        tvLocationCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityBean cb = getLocationCityInfo(tvLocationCity.getText().toString());
                if(null == cb ){
                    return;
                }else{
                    Logger.d(cb);
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("CITY_NAME", tvLocationCity.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    /**
     * 为ListView填充数据
     *
     * @param data
     * @return
     */
    private List<CityBean> filledData(List<CityBean> data) {


        List<CityBean> mSortList = new ArrayList<CityBean>();

        for (int i = 0; i < data.size(); i++) {
            CityBean sortModel = new CityBean();
            sortModel.CITY_NAME = (data.get(i).CITY_NAME);

            //汉字转换成拼音
            String pinyin = characterParser.getSelling(data.get(i).CITY_NAME);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.chr = sortString.toUpperCase();
            } else {
                sortModel.chr = "#";
            }

            mSortList.add(sortModel);
        }
        Collections.sort(mSortList, new Comparator<CityBean>() {
            @Override
            public int compare(CityBean cityBean, CityBean t1) {
                return cityBean.chr.compareTo(t1.chr);
            }
        });
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (StrUtil.isEmpty(filterStr)) {
//			CommonDialog dialog = new CommonDialog("tips", "ok?", "ok", new CommonDialog.DialogClick() {
//                @Override
//                public void cancel(Object tag) {
//
//                }
//
//                @Override
//                public void confirm(Object tag) {
//
//                }
//            });
//            dialog.oneButtonDialog(CityListSampleActivity.this);
//            dialog.showClearDialog();
            return;
        }
        List<CityBean> filterDataList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = chrSourceDataList;
        } else {
            filterDataList.clear();
            for (CityBean sortModel : chrSourceDataList) {
                String name = sortModel.CITY_NAME;
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDataList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDataList, pinyinComparator);
        adapter.updateListView(filterDataList);
    }

    private CityBean getLocationCityInfo(String city) {
        for (CityBean cityBean : chrSourceDataList
                ) {
            if (cityBean.CITY_NAME.contains(city)) {
                return cityBean;
            }
        }
        return null;
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.radioButton6) {
            flag = !flag;
            initViews();
        } else if (i == R.id.radioButton5) {
            flag = !flag;
            initViews();

        } else {

        }
    }
}
