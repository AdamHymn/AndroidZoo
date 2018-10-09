package zoo.hymn.views.citylistview;

import java.util.Comparator;

import zoo.hymn.views.citylistview.CityBean;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<CityBean> {

	@Override
    public int compare(CityBean o1, CityBean o2) {
		if ("@".equals(o1.chr)
				|| "#".equals(o2.chr)) {
			return -1;
		} else if ("#".equals(o1.chr)
				|| "@".equals(o2.chr)) {
			return 1;
		} else {
			return o1.chr.compareTo(o2.chr);
		}
	}

}
