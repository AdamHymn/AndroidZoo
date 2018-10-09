package zoo.hymn.views.welcome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
	/** The m fragment list. */
	private List<Fragment> mFragmentList = null;

	/**
	 * 
	 * @param mFragmentManager the m fragment manager
	 * @param fragmentList the fragment list
	 */
	public FragmentAdapter(FragmentManager mFragmentManager, List<Fragment> fragmentList) {
		super(mFragmentManager);
		mFragmentList = fragmentList;
	}

	/**
	 * 描述：
	 *
	 * @return the count
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	/**
	 * 描述：获取索引位置的Fragment.
	 *
	 * @param position the position
	 * @return the item
	 * @see FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;
		if (position < mFragmentList.size()){
			fragment = mFragmentList.get(position);
		}else{
			fragment = mFragmentList.get(0);
		}
		return fragment;

	}}
