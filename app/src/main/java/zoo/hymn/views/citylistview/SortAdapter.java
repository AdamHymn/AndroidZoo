package zoo.hymn.views.citylistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;



import java.util.List;

import com.bm.wb.R;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<CityBean> list = null;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<CityBean> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<CityBean> list){
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return this.list.size();
	}

	@Override
    public Object getItem(int position) {
		return list.get(position);
	}

	@Override
    public long getItemId(int position) {
		return position;
	}

	@Override
    public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final CityBean mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.city_item, null);
			viewHolder.tvName = (TextView) view.findViewById(R.id.name);
			viewHolder.tvPhone = (TextView) view.findViewById(R.id.phone);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.chr);
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvName.setText(this.list.get(position).CITY_NAME);
		viewHolder.tvPhone.setVisibility(View.GONE);
		
		return view;

	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvName;
		TextView tvPhone;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
    public int getSectionForPosition(int position) {
		return list.get(position).chr.charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
    public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).chr;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}
}