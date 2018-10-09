package com.bm.wb.api;

import android.content.Context;

import com.bm.wb.R;

import java.io.File;
import java.util.HashMap;

import zoo.hymn.base.net.callback.ZooCallBack;
import zoo.hymn.base.net.engines.okhttp.OkHttpUtil;
import zoo.hymn.base.net.response.base.BaseStringResponse;

import static zoo.hymn.ZooConstant.uploadImageByType;

/**
 * ClassName: APIMethods2
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/12/21
 */

public class APIMethods extends OkHttpUtil {
    private static APIMethods httpMethods;

    public APIMethods(Context context, ZooCallBack back) {
        super(context, back);
    }

    public synchronized static APIMethods getInstance(Context context, ZooCallBack back) {
        if (httpMethods == null || mClient == null || httpMethods.mContext != context) {
            httpMethods = new APIMethods(context, back);
        }
        return httpMethods;
    }

    public static void SHUTDOWN() {
        httpMethods = null;
        mClient = null;
    }


    /**
     * 根据类型上传图片
     * @param file
     * @param type
     * @param tag
     */
    public void uploadImageByType(File file,final String type, int tag) {

        uploadFiles(uploadImageByType,new File[]{file},new String[]{"file"},new HashMap<String, String>(){
            {
                put("type",type);
            }
        }, BaseStringResponse.class, tag, R.string.loading);
    }

}
