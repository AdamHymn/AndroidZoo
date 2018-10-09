package com.bm.wb.adpter;

/**
 * Created by huy02 on 2016/5/16.
 */

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bm.wb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import zoo.hymn.ZooConstant;
import zoo.hymn.base.adapter.ZOOBaseAdapter;
import zoo.hymn.utils.ViewUtil;


public class ImgAdapter extends ZOOBaseAdapter<String> {


    public ImgAdapter(Context mContext, List<String> mDataList) {
        super(mContext, mDataList, R.layout.zoo_img);
    }


    @Override
    public void Convert(ViewHolder viewHolder, String item) {
        ImageView img = viewHolder.getViewById(R.id.iv_img);
        Activity activity = (Activity) mContext;
        int screenWidth = 320;
//        if(activity instanceof WXDDetailActivity){
//            screenWidth = ViewUtil.getScreenWidthPixels(activity);
//        }
        int imgWidth = (int) ((screenWidth - ViewUtil.sp2px(mContext, 53)) / 3);
        int imgHeight = (imgWidth*2/3);
        // 图片下载，可以在此集成第三方异步加载图片网络库
        try {
            RequestOptions options = new RequestOptions();
            options.override(imgWidth, imgHeight);
            Glide.with(mContext).load(ZooConstant.URL_MEDIA+item).apply(options).into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
