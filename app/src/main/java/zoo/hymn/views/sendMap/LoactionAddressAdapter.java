package zoo.hymn.views.sendMap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wb.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LoactionAddressAdapter
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/24
 */


public class LoactionAddressAdapter extends RecyclerView.Adapter<LoactionAddressAdapter.LoactionAddressViewHolder> {

    private List<LocationAddress> locationAddressList = new ArrayList<>();
    private LayoutInflater inflater;

    public LoactionAddressAdapter(Context mContext, List<LocationAddress> locationAddressList) {
        this.locationAddressList = locationAddressList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public LoactionAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LoactionAddressViewHolder(inflater.inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(LoactionAddressViewHolder holder, int position) {

        Log.e("onBindViewHolder", "getTitle: "+locationAddressList.get(position).getPoiItem().getTitle() );
        Log.e("onBindViewHolder", "getAdName: "+locationAddressList.get(position).getPoiItem().getAdName() );
        Log.e("onBindViewHolder", "getSnippet: "+locationAddressList.get(position).getPoiItem().getSnippet() );
        Log.e("onBindViewHolder", "getBusinessArea: "+locationAddressList.get(position).getPoiItem().getBusinessArea() );
        Log.e("onBindViewHolder", "getCityName: "+locationAddressList.get(position).getPoiItem().getCityName() );
        Log.e("onBindViewHolder", "getProvinceName: "+locationAddressList.get(position).getPoiItem().getProvinceName() );
        holder.detail.setText(locationAddressList.get(position).getPoiItem().getProvinceName()
                +locationAddressList.get(position).getPoiItem().getCityName()
                +locationAddressList.get(position).getPoiItem().getAdName()
                +locationAddressList.get(position).getPoiItem().getBusinessArea()
                +locationAddressList.get(position).getPoiItem().getSnippet()
        );
        holder.title.setText(locationAddressList.get(position).getPoiItem().getTitle());
    }

    @Override
    public int getItemCount() {
        return locationAddressList.size();
    }


    public static class LoactionAddressViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView detail;

        public LoactionAddressViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            detail = (TextView) itemView.findViewById(R.id.tv_detail);
        }
    }

}
