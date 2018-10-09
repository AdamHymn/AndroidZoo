package com.bm.wb.ui.zoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wb.R;

import butterknife.BindView;
import butterknife.OnClick;
import zoo.hymn.ZooConstant;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.DeviceUtils;
import zoo.hymn.utils.StrUtil;
import zoo.hymn.utils.Tools;
import zoo.hymn.views.EditText.ClearEditText;
import zoo.hymn.views.EditText.PasswordEditText;

import static zoo.hymn.ZooConstant.NEED_PERMISSIONS;

/**
 * ClassName: LoginActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/1/4
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.et_pwd)
    PasswordEditText etPwd;
    @BindView(R.id.til_pwd)
    TextInputLayout tilPwd;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.de_img_backgroud)
    ImageView mImg_Background;

    String uuid = "";

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitleMode(GONE_TITLE);
        addChildView(R.layout.zoo_login_ac);
    }


    @Override
    protected void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);

        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tilName.setErrorEnabled(false);
                tilPwd.setErrorEnabled(false);
                return false;
            }
        });
        etPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tilName.setErrorEnabled(false);
                tilPwd.setErrorEnabled(false);
                return false;
            }
        });
    }

    @Override
    public void initData() {

        checkPermissions(NEED_PERMISSIONS);

        if (DeviceUtils.verifyPhoneStatePermissions(this)) {
            uuid = DeviceUtils.getIMEI(this);
        }

    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void fail(int tag, final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!StrUtil.isEmpty(error) && error.contains("用户")) {
                    etName.startShakeAnimation();
                    tilName.setError(error);
                } else if (!StrUtil.isEmpty(error) && error.contains("密码")) {
                    etPwd.startShakeAnimation();
                    tilPwd.setError(error);
                } else {
                    showToast(error);
                }
            }
        });

    }


    @Override
    public void success(final int tag, final BaseResponse response) {

    }


    @OnClick({R.id.tv_forget_pwd,
            R.id.tv_register,
            R.id.btn_login,
            R.id.ll_cancel,
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_cancel:
                Tools.hideSoftInput(this);
                break;
            case R.id.tv_forget_pwd:
                break;
            case R.id.tv_register:
                break;
            case R.id.btn_login:

//                if (StrUtil.isEmpty(etName.getText().toString())) {
//                    etName.startShakeAnimation();
//                    tilName.setError(getString(R.string.name_empty));
//                    return;
//                } else {
//                    tilName.setErrorEnabled(false);
//                }
//                if (StrUtil.isEmpty(etPwd.getText().toString())) {
//                    etPwd.startShakeAnimation();
//                    tilPwd.setError(getString(R.string.pwd_empty));
//                    return;
//                } else {
//                    tilPwd.setErrorEnabled(false);
//                }

                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //兼容7.0以上
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DeviceUtils.REQUEST_PHONE_STATE_CODE:
                    if (DeviceUtils.verifyPhoneStatePermissions(this)) {
                        uuid = DeviceUtils.getIMEI(this);
                    }
                    break;
            }
        }

    }



}
