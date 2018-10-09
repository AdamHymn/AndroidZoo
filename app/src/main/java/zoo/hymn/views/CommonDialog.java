package zoo.hymn.views;


import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bm.wb.R;


public class CommonDialog {
    public interface DialogClick {
        void cancel(Object tag);

        void confirm(Object tag);
    }

    private String titleStr, contentStr, cancelStr, confirmStr;
    private DialogClick click;

    public CommonDialog(DialogClick click){
        this.click = click;
    }

    /**
     * @param titleStr   标题内容
     * @param contentStr 要显示的内容
     * @param cancelStr  取消按钮名字
     * @param confirmStr  确认按钮名字
     * @param click      按钮回调事件
     */
    public CommonDialog(String titleStr, String contentStr,
                        String cancelStr, String confirmStr, DialogClick click) {
        super();
        this.titleStr = titleStr;
        this.contentStr = contentStr;
        this.cancelStr = cancelStr;
        this.confirmStr = confirmStr;
        this.click = click;
    }

    /**
     * @param contentStr
     * @param cancelStr
     * @param click
     * @link oneButtonDialog()
     */
    public CommonDialog(String titleStr, String contentStr, String cancelStr, DialogClick click) {
        super();
        this.contentStr = contentStr;
        this.cancelStr = cancelStr;
        this.titleStr = titleStr;
        this.click = click;
    }

    private Dialog clearBuilder;
    private TextView content;
    private TextView tvTitle;
    private Button left, right;

    public CommonDialog withEditTextDialog(Context context, String titleStr) {
        View clearView = LayoutInflater.from(context).inflate(
                R.layout.common_dialog_with_edittext, null);
        clearBuilder = new Dialog(context, R.style.common_dialog_bg);
        final TextView title = (TextView) clearView.findViewById(R.id.tv_dg_title);
        final EditText content = (EditText) clearView.findViewById(R.id.et_content);
        Button left = (Button) clearView.findViewById(R.id.btn_cancel);
        Button right = (Button) clearView.findViewById(R.id.btn_true);
        title.setText(titleStr);
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_cancel) {
                    click.cancel(1);
                }
                if (v.getId() == R.id.btn_true) {
                    click.confirm(content.getText().toString());
                }

            }
        };
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        clearBuilder.getWindow().setContentView(clearView);
        clearBuilder.setCanceledOnTouchOutside(true);
        clearBuilder.setCancelable(true);
        return this;
    }
    public CommonDialog withEditTextDialog(Context context, String titleStr, String etHint, int inputType, InputFilter[] inputFilters) {
        View clearView = LayoutInflater.from(context).inflate(
                R.layout.common_dialog_with_edittext, null);
        clearBuilder = new Dialog(context, R.style.common_dialog_bg);
        final TextView title = (TextView) clearView.findViewById(R.id.tv_dg_title);
        final EditText content = (EditText) clearView.findViewById(R.id.et_content);
        Button left = (Button) clearView.findViewById(R.id.btn_cancel);
        Button right = (Button) clearView.findViewById(R.id.btn_true);
        title.setText(titleStr);
        content.setHint(etHint);
        if(inputFilters != null && inputFilters.length > 0) {
            content.setFilters(inputFilters);
        }
        content.setInputType(inputType);
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_cancel) {
                    click.cancel(1);
                }
                if (v.getId() == R.id.btn_true) {
                    click.confirm(content.getText().toString());
                }

            }
        };
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        clearBuilder.getWindow().setContentView(clearView);
        clearBuilder.setCanceledOnTouchOutside(true);
        clearBuilder.setCancelable(true);
        return this;
    }
    public CommonDialog twoButtonDialog(Context context) {
        View clearView = LayoutInflater.from(context).inflate(
                R.layout.common_dialog, null);
        clearBuilder = new Dialog(context, R.style.common_dialog_bg);
        content = (TextView) clearView.findViewById(R.id.tv_dg_content);
        tvTitle = (TextView) clearView
                .findViewById(R.id.tv_dg_title);
        left = (Button) clearView.findViewById(R.id.btn_cancel);
        right = (Button) clearView.findViewById(R.id.btn_true);
        content.setText(contentStr);
        tvTitle.setText(titleStr);
        left.setText(cancelStr);
        right.setText(confirmStr);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_cancel) {
                    click.cancel(1);
                }
                if (v.getId() == R.id.btn_true) {
                    click.confirm(1);
                }

            }
        };
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        clearBuilder.getWindow().setContentView(clearView);
        clearBuilder.setCanceledOnTouchOutside(false);
        clearBuilder.setCancelable(false);
        return this;
    }

    public void oneButtonDialog(Context context) {
        View clearView = LayoutInflater.from(context).inflate(
                R.layout.common_dialog_one_button, null);
        clearBuilder = new Dialog(context, R.style.common_dialog_bg);
        content = (TextView) clearView.findViewById(R.id.common_dialog_one_buton_content);
        tvTitle = (TextView) clearView.findViewById(R.id.common_dialog_one_buton_title);
        left = (Button) clearView.findViewById(R.id.btn_choose);
        tvTitle.setText(titleStr);
        content.setText(contentStr);
        left.setText(cancelStr);
        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_choose) {
                    click.confirm(3);
                }

            }
        };
        left.setOnClickListener(clickListener);
        clearBuilder.getWindow().setContentView(clearView);
        clearBuilder.setCanceledOnTouchOutside(false);
        clearBuilder.setCancelable(false);
    }


    /**
     * 关闭当前显示的dialog
     */
    public void closeClearDialog() {
        if (clearBuilder != null) {
            clearBuilder.dismiss();
        }
    }

    /**
     * 如果dialog已经存在就显示的dialog
     */
    public void showClearDialog() {
        if (clearBuilder != null
//				&& !clearBuilder.isShowing()
                ) {
            clearBuilder.show();
        }
    }

    /**
     * 判断dialog是否显示
     *
     * @return
     */
    public boolean isShowingDialogs() {
        return clearBuilder.isShowing();
    }

    /**
     * 释放dialog
     */
    public void recycleClearDialog() {
        if (clearBuilder != null) {
            if (clearBuilder.isShowing()) {
                clearBuilder.dismiss();
            }
            clearBuilder = null;
        }
    }

    /**
     * 设置dialog不响应返回键
     *
     * @param flag
     */
    public void setCancel(boolean flag) {
        if (clearBuilder != null) {
            clearBuilder.setCanceledOnTouchOutside(flag);
            clearBuilder.setCancelable(flag);
        }
    }

}
