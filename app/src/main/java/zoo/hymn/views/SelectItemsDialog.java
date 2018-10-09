package zoo.hymn.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.bm.wb.R;

import java.util.List;

import zoo.hymn.base.adapter.ZOOBaseAdapter;
import zoo.hymn.utils.ViewUtil;

/**
 * 列表选择对话框
 */
public class SelectItemsDialog {

    public SelectItemsDialog(final Activity activity, List<String> items, AdapterView.OnItemClickListener listener) {
        View clearView = LayoutInflater.from(activity).inflate(
                R.layout.dg_select_items, null);
        clearBuilder = new Dialog(activity, R.style.common_dialog_bg);
        ListView listView = (ListView) clearView.findViewById(R.id.lv_items);
        Button btn_cancel = (Button) clearView.findViewById(R.id.btn_cancel);
        if(items.size() <= 2){
            ViewUtil.setViewLayoutParams(listView, (int) ViewUtil.sp2px(activity,110));
        }
        ItemAdapter itemAdapter = new ItemAdapter(activity,items);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(listener);
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_cancel) {
                    closeClearDialog();
                }
            }
        };
        btn_cancel.setOnClickListener(clickListener);

        Window window = clearBuilder.getWindow();
        window.setGravity(Gravity.BOTTOM);
        clearBuilder.setContentView(clearView);

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

    class ItemAdapter extends ZOOBaseAdapter<String> {

        public ItemAdapter(Activity activity, List<String> items){
            super(activity,items,R.layout.dg_select_item);
        }

        @Override
        public void Convert(ViewHolder viewHolder, String item) {
            viewHolder.setValueById(R.id.tv_item,item);
        }
    }
}
