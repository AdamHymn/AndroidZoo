package zoo.hymn.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wb.R;

import zoo.hymn.utils.AnimationUtils;


public class ProgressDialog {

    private Dialog mDialog;
    private Activity mContext;
    private static ProgressDialog progressDialog;
    private AnimationDrawable animationDrawable = null;

//    public static ProgressDialog getIntence(Context context, int resId) {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(context, resId);
//        }
//        return progressDialog;
//    }

    public ProgressDialog(Context context, int resId) {
        mContext = (Activity) context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dg_record, null);

        TextView textView = (TextView) view.findViewById(R.id.tips_loading_msg);
        ImageView imageView = (ImageView) view.findViewById(R.id.progress_view);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (resId != 0) {
            textView.setText(context.getResources().getString(resId));
        }


        mDialog = new Dialog(context, R.style.dialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);

    }

    public void show() {
        if (mDialog != null) {
            animationDrawable.setOneShot(false);
            animationDrawable.start();
            mDialog.show();
        }
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void dismiss() {
        if (mDialog != null) {
            if (mContext != null && !mContext.isFinishing()) {
                mDialog.dismiss();
                animationDrawable.stop();
            }
        }
    }
}
