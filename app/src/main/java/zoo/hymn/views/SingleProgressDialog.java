package zoo.hymn.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.bm.wb.R;

import java.util.concurrent.atomic.AtomicInteger;

import zoo.hymn.utils.StrUtil;


public class SingleProgressDialog implements DialogInterface.OnCancelListener {

    private Dialog mDialog;
    private static SingleProgressDialog pd;
    private static AtomicInteger counter;//圈圈个数
    public Context mContext;

    public SingleProgressDialog(Context context, int msgId) {

        this.mContext = context;
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dg_two, null);
            TextView textView = (TextView) view
                    .findViewById(R.id.tips_loading_msg);
            if (msgId <= 0) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(context.getResources().getString(msgId));
            }
            mDialog = new Dialog(context, R.style.progress);
            mDialog.setContentView(view);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public SingleProgressDialog(Context context, String msg) {
        this.mContext = context;
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dg_two, null);

            TextView textView = (TextView) view
                    .findViewById(R.id.tips_loading_msg);
            if (TextUtils.isEmpty(msg)) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(msg);
            }
            mDialog = new Dialog(context, R.style.progress);
            mDialog.setContentView(view);
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public SingleProgressDialog(Context context) {
        this.mContext = context;
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dg_two, null);

            TextView textView = (TextView) view
                    .findViewById(R.id.tips_loading_msg);
            textView.setText(R.string.loading);
            mDialog = new Dialog(context, R.style.progress);
            mDialog.setContentView(view);
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public synchronized static SingleProgressDialog getSingleInstance(Context context) {
        if (pd != null && pd.mContext == context) {
            return pd;
        }
        counter = new AtomicInteger(0);
        return pd = new SingleProgressDialog(context);
    }

    public synchronized static SingleProgressDialog getSingleInstance(Context context, int msgId) {
        if(msgId <= 0){
            return getSingleInstance(context);
        }
        if (pd != null && pd.mContext == context) {
            return pd;
        }
        counter = new AtomicInteger(0);
        return pd = new SingleProgressDialog(context, msgId);
    }

    public synchronized static SingleProgressDialog getSingleInstance(Context context, String msg) {
        if(StrUtil.isEmpty(msg)){
            return getSingleInstance(context);
        }
        if (pd != null && pd.mContext == context) {
            return pd;
        }
        counter = new AtomicInteger(0);
        return pd = new SingleProgressDialog(context, msg);
    }

    public synchronized void show() {
        try {
            counter.incrementAndGet();
            mDialog.show();
            setCanceledOnTouchOutside(false);
            setCancelable();
            Log.e("SingleProgressDialog", "show: " + counter.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void dismiss() {
        try {
            if (mDialog != null) {
                if (counter.get() > 1) {
                    counter.decrementAndGet();
                    Log.e("SingleProgressDialog", "dismiss: " + counter.get());
                } else {
                    mDialog.dismiss();
                    clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCanceledOnTouchOutside(boolean cancel) {
        try {
            mDialog.setCanceledOnTouchOutside(cancel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCancelable() {
        try {
            mDialog.setCancelable(false);
            mDialog.setOnCancelListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        try {
            mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            counter = null;
            mDialog = null;
            pd = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
