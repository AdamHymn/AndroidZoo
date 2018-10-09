package zoo.hymn.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bm.wb.R;

/**
 * ClassName: AnimationUtils
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/11/9
 */

public class AnimationUtils {

    public static RotateAnimation getRotateAnimation180Forward() {
        RotateAnimation rotate = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(200);//设置动画持续周期
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        return rotate;
    }

    public static RotateAnimation getRotateAnimation180Revers() {
        RotateAnimation rotate = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(200);//设置动画持续周期
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        return rotate;
    }

    public static void setRecordAnimation(Context context, ImageView imageView) {
        imageView.setImageDrawable(context.getDrawable(R.drawable.record_anim));
        Drawable drawable = imageView.getDrawable();
        AnimationDrawable rotate = (AnimationDrawable) drawable;
        rotate.setOneShot(false);
        rotate.start();
    }
    public static void setRecord1Animation(Context context, ImageView imageView) {
        imageView.setImageDrawable(context.getDrawable(R.drawable.record1_anim));
        Drawable drawable = imageView.getDrawable();
        AnimationDrawable rotate = (AnimationDrawable) drawable;
        rotate.setOneShot(false);
        rotate.start();
    }

}
