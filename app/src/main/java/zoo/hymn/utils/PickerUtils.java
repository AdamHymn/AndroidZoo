package zoo.hymn.utils;

import android.app.Activity;

import java.util.List;

import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;

/**
 * ClassName: PickerUtils
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/1/6
 */

public class PickerUtils {

    public static void doSinglePicker(Activity activity,List<String> list, OnItemPickListener onItemPickListener) {
        if (list == null || list.size() == 0) {
            return;
        }
        SinglePicker picker = new SinglePicker<>(activity, list);
        picker.setCanLoop(false);//不禁用循环
        picker.setLineVisible(true);
        picker.setShadowVisible(true);
        picker.setTextSize(24);
        picker.setSelectedIndex(1);
        picker.setWheelModeEnable(true);
        //启用权重 setWeightWidth 才起作用
//        picker.setLabel("分");
        picker.setWeightEnable(true);
        picker.setWeightWidth(1);
        picker.setSelectedTextColor(0xFF279BAA);//前四位值是透明度
        picker.setUnSelectedTextColor(0xFF999999);
        picker.setOnSingleWheelListener(new OnSingleWheelListener() {
            @Override
            public void onWheeled(int index, String item) {
//                        showToast("index=" + index + ", item=" + item);
            }
        });
        picker.setOnItemPickListener(onItemPickListener);
        picker.show();
    }
}
