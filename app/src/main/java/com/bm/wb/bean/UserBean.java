package com.bm.wb.bean;

import org.litepal.crud.DataSupport;

/**
 * ClassName: UserBean
 * Function : 用户信息模型
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/11/28
 */

public class UserBean extends DataSupport {

    public long lastLoginTime;
    public long createdTime;
    public String userId;
    public String userName;
    public String password;
    public String nickname;
    public String avatarUrl;
    public String phone;
    public String role;
    public String token;

    public String rongToken;
    public String pushAlias;
    public String pushTags;
}
