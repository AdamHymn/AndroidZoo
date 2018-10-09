package zoo.hymn.views.citylistview;


import android.os.Parcel;
import android.os.Parcelable;

public class CityBean implements Parcelable {
 
	/**
	 * 大写首字母
	 */
	public String chr;

	/**
	 * PROVINCE_ID : 110000
	 * CITY_ID : 110100
	 * CITY_NAME : 北京市
	 */

	public String PROVINCE_ID;
	public String CITY_ID;
	public String CITY_NAME;


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.CITY_NAME);
		dest.writeString(this.chr);
	}

	public CityBean() {
	}

	protected CityBean(Parcel in) {
		this.CITY_NAME = in.readString();
		this.chr = in.readString();
		this.PROVINCE_ID = in.readString();
		this.CITY_ID = in.readString();
	}

	public static final Creator<CityBean> CREATOR = new Creator<CityBean>() {
		@Override
		public CityBean createFromParcel(Parcel source) {
			return new CityBean(source);
		}

		@Override
		public CityBean[] newArray(int size) {
			return new CityBean[size];
		}
	};

	
}
