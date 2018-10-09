package zoo.hymn.views.welcome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.bm.wb.R;

import java.util.ArrayList;
import java.util.List;

import zoo.hymn.ZooConstant;
import zoo.hymn.utils.SharedPreferencesUtil;

public class GuideAc extends FragmentActivity {

    private ViewPager pager;
    private List<Fragment> fragmentList;
    private FragmentAdapter adapter;
    private int[] imageIds = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
    private CirclePageIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fg_welcome_circles);

        pager = (ViewPager) this.findViewById(R.id.welcome_circle_pager);
        mIndicator = (CirclePageIndicator) this.findViewById(R.id.welcome_circle_page_indicator);

        fragmentList = new ArrayList<>();
        for (int i = 0; i < imageIds.length; i++) {
            GuideFragment welcome = new GuideFragment();
            if (i == (imageIds.length - 1)) {
                welcome.setData(imageIds[i], true);
            } else {
                welcome.setData(imageIds[i], false);
            }
            fragmentList.add(welcome);
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        mIndicator.setViewPager(pager);
        mIndicator.setSnap(false);
        mIndicator.setStrokeWidth(0f);
        mIndicator.setFillColor(Color.parseColor("#00000000"));

        SharedPreferencesUtil.setCustomSPBool(this, ZooConstant.KEY_FIRST_LAUNCH, false);
    }


}
