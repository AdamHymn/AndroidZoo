package zoo.hymn.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.wb.R;


public class CommonCallDialog {

    /**
     * 拨打电话的对话框
     */
    View viewCall;
    Dialog dialogCall;
    private LinearLayout callLiner;
    private TextView phone;
    private LinearLayout cancelLiner;

    private LinearLayout callNumberLiner;

    /**
     * @param mContext
     * @param //isCanCelable点击回退键是否能取消
     * @param number
     */
    public void showCallDialog(final Context mContext, Boolean isCanCelable,
                               final String number) {
        LayoutInflater inflaterCall = LayoutInflater.from(mContext);
        viewCall = inflaterCall.inflate(R.layout.call_phone_dialog, null);
        callLiner = (LinearLayout) viewCall
                .findViewById(R.id.call_phone_dialog_call);
       callNumberLiner = (LinearLayout) viewCall
                .findViewById(R.id.call_phone_dialog_call_number_liner);
        phone = (TextView) viewCall.findViewById(R.id.call_phone_dialog_phone);
        cancelLiner = (LinearLayout) viewCall
                .findViewById(R.id.call_phone_dialog_cancel);
        dialogCall = new Dialog(mContext, R.style.common_dialog_bg);
        dialogCall.setCancelable(isCanCelable);

        dialogCall.setContentView(viewCall);
        Window dialogWindow = dialogCall.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();// 获取对话框当前的参数值
        WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的1
        dialogWindow.setAttributes(lp);
//        callLiner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               tell(mContext, number);
//                dialogCall.dismiss();
//            }
//        });

        callNumberLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tell(mContext, number);
                dialogCall.dismiss();
            }
        });
        cancelLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCall.dismiss();
            }
        });
        phone.setText("客户电话："+number);
        dialogCall.show();
    }

    public void dissmissCallDialog() {
        dialogCall.dismiss();
    }

    /**
     * 打电话
     *
     * @param
     * @param content
     */
    public static void tell(Context context, String content) {
//        NoteUtil.clickEvnt(context, EventId.BUTTON_);
        Intent intent = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + content));
        context.startActivity(intent);
    }

}
