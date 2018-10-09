package zoo.hymn.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bm.wb.R;


/**
 * 轻松标题栏
 */
public class EaseTitleBar extends RelativeLayout {

    protected RelativeLayout rootLayout;
    protected LinearLayout leftLayout;
    public ImageView leftImage;
    public TextView leftText;
    protected LinearLayout rightLayout;
    public ImageView rightImage;
    public TextView rightText;
    protected TextView centerText;

    public EaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseTitleBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_title_bar, this);
        rootLayout = (RelativeLayout) findViewById(R.id.title_bar_root_layout);
        leftLayout = (LinearLayout) findViewById(R.id.title_bar_left_layout);
        rightLayout = (LinearLayout) findViewById(R.id.title_bar_right_layout);
        leftImage = (ImageView) findViewById(R.id.iv_left_image);
        rightImage = (ImageView) findViewById(R.id.iv_right_image);
        leftText = (TextView) findViewById(R.id.tv_left_text);
        rightText = (TextView) findViewById(R.id.tv_right_text);
        centerText = (TextView) findViewById(R.id.tv_title);
        leftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
        parseStyle(context, attrs);
    }

    /**
     * 设置右侧图标的显示和隐藏
     *
     * @param visibility
     */
    public void setRightIvLayoutVisible(int visibility) {
        rightImage.setVisibility(visibility);
    }

    /**
     * 设置右侧文字的显示和隐藏
     *
     * @param visibility
     */
    public void setRightTvLayoutVisible(int visibility) {
        rightText.setVisibility(visibility);
    }


    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseTitleBar);
            String title = ta.getString(R.styleable.EaseTitleBar_titleBarValue);
            centerText.setText(title);

            Drawable leftDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarLeftImg);
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable);
            }

            Drawable rightDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarLeftImg);
            if (null != rightDrawable) {
                rightImage.setBackground(rightDrawable);
            }


            Drawable background = ta.getDrawable(R.styleable.EaseTitleBar_titleBarBg);
            if (null != background) {
                rootLayout.setBackgroundDrawable(background);
            }

            ta.recycle();
        }
    }

    public void setLeftImageResource(int resId) {
        leftImage.setImageResource(resId);
    }


    public void setLeftLayoutClickListener(OnClickListener listener) {
        leftLayout.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility) {
        leftLayout.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility) {
        rightLayout.setVisibility(visibility);
    }

    public void setRightLayoutOnClick(OnClickListener listener) {
        rightLayout.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        centerText.setText(title);
    }

    public void setTitle(int resId) {
        centerText.setText(getResources().getString(resId));
    }

    @Override
    public void setBackgroundColor(int color) {
        rootLayout.setBackgroundColor(color);
    }

    public void setRightText(String text) {
        setRightLayoutVisibility(VISIBLE);
        setRightTvLayoutVisible(VISIBLE);
        rightText.setText(text);
        rightText.setTextColor(Color.WHITE);
    }

    public void setRightImageResource(int resId) {
        setRightLayoutVisibility(VISIBLE);
        setRightIvLayoutVisible(VISIBLE);
        rightImage.setImageResource(resId);
    }

    public LinearLayout getLeftLayout() {
        return leftLayout;
    }

    public LinearLayout getRightLayout() {
        return rightLayout;
    }

    public TextView getLeftTextView(){
        return leftText;
    }
    public ImageView getLeftImageView(){
        return leftImage;
    }

    public String getRightText() {
        return rightText.getText().toString();
    }

    public String getTitle() {
        return centerText.getText().toString();
    }
}
