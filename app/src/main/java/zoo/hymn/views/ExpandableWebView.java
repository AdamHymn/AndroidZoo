package zoo.hymn.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ListView;

public class ExpandableWebView extends WebView {

    public ExpandableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableWebView(Context context) {
        super(context);
    }

    public ExpandableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); 
    } 

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec); 
    } 
}
