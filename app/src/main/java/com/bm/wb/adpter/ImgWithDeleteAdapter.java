package com.bm.wb.adpter;

/**
 * Created by huy02 on 2016/5/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bm.wb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import zoo.hymn.ZooConstant;
import zoo.hymn.base.adapter.ZOOBaseAdapter;
import zoo.hymn.utils.ViewUtil;


public class ImgWithDeleteAdapter extends ZOOBaseAdapter<String> {

    public interface DeleteIMGListener {
        void doDel(String url);
    }

    private DeleteIMGListener deleteIMGListener;

    public ImgWithDeleteAdapter(Context mContext, List<String> mDataList, DeleteIMGListener deleteIMGListener) {
        super(mContext, mDataList, R.layout.zoo_item_img_with_delete);
        this.deleteIMGListener = deleteIMGListener;
    }


    @Override
    public void Convert(final ViewHolder viewHolder, final String item) {
        ImageView del = viewHolder.getViewById(R.id.iv_delete);
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
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteIMGListener.doDel(item);
            }
        });
    }
}
