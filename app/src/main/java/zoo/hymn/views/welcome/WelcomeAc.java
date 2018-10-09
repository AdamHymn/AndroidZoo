package zoo.hymn.views.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bm.wb.R;
import com.bm.wb.ui.zoo.LoginActivity;

import zoo.hymn.ZooConstant;
import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.SharedPreferencesUtil;


public class WelcomeAc extends BaseActivity {
    public static final String TAG = WelcomeAc.class.getSimpleName();
    private long mTime = 2000;
    private Handler fHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mTime <= 0) {
                isWhat();
                return;
            }
            mTime = mTime - 1000;
            if (fHandler != null) {
                fHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.a_ac_welcome);
        fHandler.post(runnable);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    private void isWhat() {
        if (SharedPreferencesUtil.getCustomSPBool(this, ZooConstant.KEY_FIRST_LAUNCH, true)) {
            skip(WelcomeAc.this, GuideAc.class);
        } else {
            skip(WelcomeAc.this, LoginActivity.class);
        }
    }


    public void skip(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {

    }
}