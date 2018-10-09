package zoo.hymn.base.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bm.wb.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * ClassName: ZOOBaseAdapter
 * Function : 所有列表适配器的基础类
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/2/13
 */

public abstract class ZOOBaseAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDataList;
    private int mLayoutId;

    public ZOOBaseAdapter(Context mContext, List<T> mDataList, int mLayoutId) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mLayoutId = mLayoutId;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
        this.notifyDataSetChanged();
    }

    public List<T> getDataList(){
        return this.mDataList;
    }

    @Override
    public int getCount() {
        return this.mDataList == null?0:this.mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return this.mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(mContext,convertView,mLayoutId,position);
        Convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    /**
     * 实现入口
     */
    public abstract void Convert(ViewHolder viewHolder, T item);

    /**
     * 感谢http://blog.csdn.net/lmj623565791/article/details/38902805这位兄弟的思想
     */
    public static class ViewHolder {

        private SparseArray<View> mViews;
        private View mConvertView;
        private int mPosition;
        private Context mContext;

        private ViewHolder(Context context, int layoutId, int position) {
            this.mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
            this.mViews = new SparseArray<>();
            this.mConvertView.setTag(this);
            this.mPosition = position;
            this.mContext = context;
        }

        public static ViewHolder getViewHolder(Context context,View convertView, int layoutId, int position) {
            if (convertView == null) {
                return new ViewHolder(context, layoutId, position);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.setPosition(position);
            return holder;
        }

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }
        public int getPosition() {
            return mPosition;
        }
        public View getConvertView() {
            return mConvertView;
        }
        public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
            ImageView view = getViewById(viewId);
            view.setImageBitmap(bm);
            return this;
        }

        /**
         * 获取元素对象
         * @param viewId
         * @param <V>
         * @return
         */
        public <V extends View> V getViewById(int viewId) {
            View childView = this.mViews.get(viewId);
            if(childView == null) {
                childView = mConvertView.findViewById(viewId);
                this.mViews.put(viewId, childView);
            }

            return (V)childView;
        }

        /**
         * 给元素赋值
         * @param viewId
         * @param data
         */
        public void setValueById(int viewId, Object data,int placeHolder) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            if (data == null) {
                return;
            }

            if(TextView.class.isAssignableFrom(view.getClass())) {
                ((TextView) view).setText(data.toString());
            } else if(Button.class.isAssignableFrom(view.getClass())) {
                ((Button)view).setText(data.toString());
            } else if(CheckBox.class.isAssignableFrom(view.getClass())) {
                ((CheckBox)view).setText(data.toString());
            } else if(RadioButton.class.isAssignableFrom(view.getClass())) {
                ((RadioButton)view).setText(data.toString());
            } else if(EditText.class.isAssignableFrom(view.getClass())) {
                ((EditText)view).setText(data.toString());
            }else if (ImageView.class.isAssignableFrom(view.getClass())) {
                if (Integer.class == data.getClass()
                        || int.class == data.getClass()) {
                    ((ImageView) view).setImageResource(Integer.valueOf(data.toString()));
                } else {
                    // 图片下载，可以在此集成第三方异步加载图片网络库
                    try {
                        RequestOptions options = new RequestOptions();
                        options.circleCrop();
                        options.placeholder(placeHolder);
                        Glide.with(mContext).load(data.toString()).apply(options).into(((ImageView) view));
                        Glide.with(mContext).clear(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /**
         * 给元素赋值
         * @param viewId
         * @param data
         */
        public void setValueById(int viewId, Object data) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            if (data == null) {
                return;
            }

            if(TextView.class.isAssignableFrom(view.getClass())) {
                ((TextView) view).setText(data.toString());
            } else if(Button.class.isAssignableFrom(view.getClass())) {
                ((Button)view).setText(data.toString());
            } else if(CheckBox.class.isAssignableFrom(view.getClass())) {
                ((CheckBox)view).setText(data.toString());
            } else if(RadioButton.class.isAssignableFrom(view.getClass())) {
                ((RadioButton)view).setText(data.toString());
            } else if(EditText.class.isAssignableFrom(view.getClass())) {
                ((EditText)view).setText(data.toString());
            }else if (ImageView.class.isAssignableFrom(view.getClass())) {
                if (Integer.class == data.getClass()
                        || int.class == data.getClass()) {
                    ((ImageView) view).setImageResource(Integer.valueOf(data.toString()));
                } else {
                    // 图片下载，可以在此集成第三方异步加载图片网络库
                    try {
                        Glide.with(mContext).load(data.toString()).into(((ImageView) view));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}

