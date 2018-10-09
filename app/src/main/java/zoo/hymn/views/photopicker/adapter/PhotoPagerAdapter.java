package zoo.hymn.views.photopicker.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bm.wb.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zoo.hymn.views.photopicker.event.PhotoOnLongClickManager;
import zoo.hymn.views.photopicker.utils.AndroidLifecycleUtils;

import static zoo.hymn.ZooConstant.URL_MEDIA;

/**
 * Created by donglua on 15/6/21.
 */

// modify PhotoPickerFragment.java add setOnLongClickListener

public class PhotoPagerAdapter extends PagerAdapter {

    private List<String> paths = new ArrayList<>();
    private ArrayList<String> longData;

    private RequestManager mGlide;

    public PhotoPagerAdapter(RequestManager glide, List<String> paths, ArrayList<String> longData) {
        this.paths = paths;
        this.mGlide = glide;
        this.longData = longData;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.__picker_picker_item_pager, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);

        final String path = URL_MEDIA + paths.get(position);
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);

        if (canLoadImage) {

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.override(800, 800);
            options.dontAnimate();
            options.dontTransform();
            options.placeholder(R.drawable.__picker_ic_photo_black_48dp);
            options.error(R.drawable.__picker_ic_broken_image_black_48dp);
            mGlide.load(uri)
                    .thumbnail(0.1f)
                    .apply(options)
//                    .dontAnimate()
//                    .dontTransform()
//                    .override(800, 800)
//                    .placeholder(R.drawable.__picker_ic_photo_black_48dp)
//                    .error(R.drawable.__picker_ic_broken_image_black_48dp)
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        ((Activity) context).onBackPressed();
                    }
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longData != null && longData.size() > 0) {
                    OnLongDialog(context, path);
                }
                return true;
            }
        });

        container.addView(itemView);

        return itemView;
    }

    private void OnLongDialog(Context context, final String path) {
        final AlertDialog albumDialog = new AlertDialog.Builder(context).create();
        albumDialog.setCanceledOnTouchOutside(true);
        albumDialog.setCancelable(true);
        View v = LayoutInflater.from(context).inflate(
                R.layout.__picker_dialog_photo_pager, null);
        albumDialog.show();
//        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(280, ViewGroup.LayoutParams.MATCH_PARENT);
        albumDialog.setContentView(v);
        albumDialog.getWindow().setGravity(Gravity.CENTER);
        albumDialog.getWindow().setBackgroundDrawableResource(R.drawable.__picker_bg_dialog);
        ListView dialog_lv = (ListView) v.findViewById(R.id.dialog_lv);

        PhotoDialogAdapter photoDialogAdapter = new PhotoDialogAdapter(context, longData);
        dialog_lv.setAdapter(photoDialogAdapter);
        dialog_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                albumDialog.dismiss();
                PhotoOnLongClickManager photoOnLongClickManager = PhotoOnLongClickManager.getInstance();
                photoOnLongClickManager.setOnLongClick(i, path);
            }
        });
    }

    public void setLongData(ArrayList<String> longData){
        this.longData = longData;
    }

    @Override
    public int getCount() {
        return paths.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//        Glide.clear((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }



}
