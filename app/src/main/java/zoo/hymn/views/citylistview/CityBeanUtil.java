package zoo.hymn.views.citylistview;

import android.content.Context;
import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zoo.hymn.utils.FileUtil;
import zoo.hymn.utils.StrUtil;


/**
 * ClassName: CityBeanUtil
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/7/25
 */

public class CityBeanUtil {

    /**
     * 解析json文件到城市列表对象
     * @return
     */
    public static List<CityBean> parseJsonCityList(Context context){

        List<CityBean> sourceDataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(FileUtil.readJSONFromAssets(context, "cityMapping.json"));
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
            return null;
        }
        return sourceDataList;
    }

    /**
     * 为城市列表数据添加拼音属性，并按A-Z排序返回
     * @param data
     * @return
     */
    public static List<CityBean> filledData(List<CityBean> data) {


        List<CityBean> mSortList = new ArrayList<CityBean>();

        for (int i = 0; i < data.size(); i++) {
            CityBean sortModel = data.get(i);

            //汉字转换成拼音
            String pinyin = CharacterParser.getInstance().getSelling(sortModel.CITY_NAME);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.chr = sortString.toUpperCase();
            } else {
                sortModel.chr = "#";
            }

            mSortList.add(sortModel);
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    public static List<CityBean> filterData(String filterStr,List<CityBean> chrSourceDataList) {
        if (StrUtil.isEmpty(filterStr)) {
            return null;
        }
        List<CityBean> filterDataList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = chrSourceDataList;
        } else {
            filterDataList.clear();
            for (CityBean sortModel : chrSourceDataList) {
                String name = sortModel.CITY_NAME;
                if (name.indexOf(filterStr.toString()) != -1 || CharacterParser.getInstance().getSelling(name).startsWith(filterStr.toString())) {
                    filterDataList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDataList, new PinyinComparator());
        return filterDataList;
    }

    /**
     * 根据城市名返回城市对象
     * @param city
     * @return
     */
    public static CityBean getLocationCityInfo(String city,List<CityBean> chrSourceDataList) {
        for (CityBean cityBean : chrSourceDataList
                ) {
            if (cityBean.CITY_NAME.contains(city)) {
                return cityBean;
            }
        }
        return null;
    }

}
