package zoo.hymn.views.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.bm.wb.R;
import com.bm.wb.ui.zoo.LoginActivity;


public class WelcomeAc extends Activity {
    public static final String TAG = WelcomeAc.class.getSimpleName();
    private long mTime = 3000;
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



    private void isWhat() {
        SharedPreferences sp = getSharedPreferences("guide",MODE_PRIVATE);
        if (sp.getBoolean("first", true)) {
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
}