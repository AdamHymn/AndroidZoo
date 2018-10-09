package zoo.hymn.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bm.wb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zoo.hymn.base.adapter.ZOOBaseAdapter;
import zoo.hymn.utils.ViewUtil;

/**
 * 列表多项选择对话框
 */
public class MultiSelectItemsDialog {
    List<CheckBoxBean> items;

    public List<CheckBoxBean> getItems() {
        return items;
    }

    public List<CheckBoxBean> getCheckedItems() {
        List<CheckBoxBean> checkedItems = new ArrayList<>();
        for (CheckBoxBean cbb : items) {
            if (cbb.isChecked) {
                checkedItems.add(cbb);
            }
        }
        return checkedItems;
    }

    public MultiSelectItemsDialog(final Activity activity, List<CheckBoxBean> items, OnClickListener listener) {
        if ("请选择".equals(items.get(0).name)) {
            items.remove(0);
        }
        this.items = items;
        View clearView = LayoutInflater.from(activity).inflate(
                R.layout.dg_select_items_multicheck, null);
        clearBuilder = new Dialog(activity, R.style.common_dialog_bg);
        ListView listView = (ListView) clearView.findViewById(R.id.lv_items);
        Button btn_cancel = (Button) clearView.findViewById(R.id.dialog_tv_cancel);
        Button btn_confirm = (Button) clearView.findViewById(R.id.dialog_tv_confirm);
//        if(items.size() <= 2){
//            ViewUtil.setViewLayoutParams(listView, (int) ViewUtil.sp2px(activity,110));
//        }
        ItemAdapter itemAdapter = new ItemAdapter(activity);
        listView.setAdapter(itemAdapter);
//        ExpandableListView.setListViewHeight(listView);
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.dialog_tv_cancel) {
                    closeClearDialog();
                }
            }
        };
        btn_confirm.setOnClickListener(listener);
        btn_cancel.setOnClickListener(clickListener);

        Window window = clearBuilder.getWindow();
        window.setGravity(Gravity.BOTTOM);

        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        clearBuilder.setContentView(clearView, layoutParams);
        clearBuilder.show();
    }

    private Dialog clearBuilder;

    public void closeClearDialog() {
        if (clearBuilder != null) {
            clearBuilder.dismiss();
        }
    }

    public void showClearDialog() {
        if (clearBuilder != null && !clearBuilder.isShowing()) {
            clearBuilder.show();
        }
    }

    public boolean isShowingDialogs() {
        return clearBuilder.isShowing();
    }

    public void recycleClearDialog() {
        if (clearBuilder != null) {
            if (clearBuilder.isShowing()) {
                clearBuilder.dismiss();
            }
            clearBuilder = null;
        }
    }

    public void setCancel(boolean flag) {
        if (clearBuilder != null) {
            clearBuilder.setCancelable(flag);
        }
    }

    class ItemAdapter extends ZOOBaseAdapter<CheckBoxBean> {
        private Map<Integer, Boolean> map = new HashMap<>();// 存放已被选中的CheckBox

        public ItemAdapter(Activity activity) {
            super(activity, items, R.layout.dg_select_item_multicheck);
        }

        @Override
        public void Convert(final ViewHolder viewHolder, final CheckBoxBean item) {
            final CheckBox checkBox = viewHolder.getViewById(R.id.cb_item);
            checkBox.setText(item.name);
//            checkBox.setChecked(item.isChecked);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.isChecked = isChecked;
                    if (isChecked) {
                        map.put(viewHolder.getPosition(), true);
                    } else {
                        map.remove(viewHolder.getPosition());
                    }
                }
            });

            if (map != null && map.containsKey(viewHolder.getPosition())) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    public static class CheckBoxBean {

        /**
         * @param name
         */
        public CheckBoxBean(String name) {
            this.name = name;
        }

        /**
         * @param id
         * @param name
         */
        public CheckBoxBean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int id = -1;
        public String name = "";
        public boolean isChecked = false;
    }
}
