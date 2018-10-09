package com.bm.wb.adpter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bm.wb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

import zoo.hymn.utils.ViewUtil;
import zoo.hymn.views.photopicker.utils.AndroidLifecycleUtils;


/**
 * 改进版
 */
public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.PhotoViewHolder> {

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;
    private Context mContext;
    public final static int TYPE_ADD = 1;
    public final static int TYPE_PHOTO = 2;

    public UploadAdapter(Context mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(inflater.inflate(R.layout.upload_picker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        Activity activity = null;
//        if (mContext instanceof AddWXDActivity) {
//            activity = (AddWXDActivity) mContext;
//        }
        int screenWidth = 0;
        try {
            screenWidth = ViewUtil.getScreenWidthPixels(activity);
        } catch (Exception e) {
            screenWidth = 320;
        }
        int imgWidth = screenWidth - (int) ViewUtil.dip2px(mContext, (20+56+10+30));//宽度计算不准确会导致图片显示问题
        Log.d("imgWidth", "imgWidth: " + imgWidth / 3);
        if (getItemViewType(position) == TYPE_PHOTO) {
            Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

            boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(imgWidth / 3, imgWidth / 4);
            holder.ivPhoto.setLayoutParams(fl);
            holder.ivPhoto.setPadding(
                    (int) ViewUtil.dip2px(mContext, 5),
                    (int) ViewUtil.dip2px(mContext, 5),
                    (int) ViewUtil.dip2px(mContext, 5),
                    (int) ViewUtil.dip2px(mContext, 5));
            if (canLoadImage) {
                RequestOptions options = new RequestOptions();
                options.centerCrop();
                options.placeholder(R.drawable.pic_default);
                options.error(R.drawable.pic_default);
                Glide.with(mContext)
                        .load(uri)
                        .apply(options)
                        .thumbnail(0.1f)
                        .into(holder.ivPhoto);
                holder.ivDelete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent e) {
                        //因为RecyclerView设置了OnTouch事件，且这个事件的返回值是True,即拦截事件不再传递。所以，设置onClick无法执行。因为onTouch方法优先onClick方法执行，所以用此方法。
                        //但是，onTouch方法会因为MotionEvent不同而调用多次，所以需要判断一个action即可，不然的话，会因为多次执行删除和更新数据导致RecyclerView报数组越界异常和滚动时无法刷新异常。
                        if (e.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("onTouch", "onTouch: " + position + "photos:" + photoPaths.size());
                            photoPaths.remove(position);
                            notifyItemRemoved(position);//添加删除动画
                            notifyItemRangeChanged(position, getItemCount());//刷新item序号
                        }
                        return true;
                    }
                });
            }
        } else {
            holder.ivPhoto.setLayoutParams(new FrameLayout.LayoutParams(imgWidth / 3, imgWidth / 4));
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.ivDelete.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        int count = photoPaths.size() + 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photoPaths.size()) ? TYPE_ADD : TYPE_PHOTO;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView ivDelete;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }

}
