package com.bm.wb.ui.zoo;

/**
 * ClassName: s
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/3/1
 */

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bm.wb.R;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import zoo.hymn.base.net.response.base.BaseDataResponse;
import zoo.hymn.base.net.response.base.BaseResponse;
import zoo.hymn.base.ui.BaseActivity;

import static zoo.hymn.ZooConstant.URL_MEDIA;

/**
 * ClassName: ConversationActivity
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/11/22
 */

public class ConversationActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    String targetId;
    String title;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitleMode(GONE_TITLE);
        addChildView(R.layout.conversation_fg);
    }

    @Override
    protected void initView() {
        //会话界面 对方id
        targetId = getIntent().getData().getQueryParameter("targetId");
        //对方 昵称
        title = getIntent().getData().getQueryParameter("title");
        if (!TextUtils.isEmpty(title)) {
            //todo 设置标题为对方昵称
            tvTitle.setText(title);
        } else if (!TextUtils.isEmpty(targetId)) {
            tvTitle.setText(targetId);
        } else {
            tvTitle.setText("聊天");
        }

        //        APIMethods2.getInstance(this,this).get(API+"common/userSimpleInfo?id="+targetId,UserResponse.class,0,R.string.loading);

    }

    @Override
    public void success(int tag, BaseResponse response) {
        super.success(tag, response);
        UserResponse userResponse = (UserResponse) response;
        if(userResponse.data != null){
            tvTitle.setText(userResponse.data.nickname);
            /**
             * 刷新用户缓存数据。
             *
             * @param userInfo 需要更新的用户缓存数据。
             */
            RongIM.getInstance().refreshUserInfoCache(
                    new UserInfo(userResponse.data.id,
                            userResponse.data.nickname,
                            Uri.parse(URL_MEDIA+userResponse.data.avataUrl)));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }

    @OnClick(R.id.title_bar_left_layout)
    public void onViewClicked() {
        finish();
    }

    class User {
        String id;
        String nickname;
        String avataUrl;
    }
    class UserResponse extends BaseDataResponse<User> {}
}