package zoo.hymn.views.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wb.R;
import com.bm.wb.ui.zoo.LoginActivity;


public class GuideFragment extends Fragment {

	private int resId;
	private boolean isEnd;
	private ImageView imageView;
	private Context mContext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View mView = inflater.inflate(R.layout.fragment_welcome, null);
		imageView = (ImageView) mView.findViewById(R.id.iv_guide);
		if(resId == 0){
			imageView.setImageResource(R.drawable.welcome);
		}else{
			imageView.setImageResource(resId);
		}

		if (isEnd) {
			TextView tv = (TextView) mView.findViewById(R.id.tv_welcome);
			tv.setVisibility(View.VISIBLE);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goToMain();
				}
			});
		}
		return mView;
	}

	/**
	 * 根据登录信息跳转到主界面
	 *
	 * @exception
	 * @since 1.0.0
	 */
	private void goToMain() {
		startActivity(new Intent(getActivity(), LoginActivity.class));
		((GuideAc)mContext).finish();
	}

	/**
	 * 创建一个新的实例 GuideFragment.
	 * 
	 */
public void setData(int resId, boolean isEnd){
	this.resId = resId;
	this.isEnd = isEnd;
}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
	}


}
