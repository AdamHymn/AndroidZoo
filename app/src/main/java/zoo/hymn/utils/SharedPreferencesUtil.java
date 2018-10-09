package zoo.hymn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class SharedPreferencesUtil {

	/**
	 * 清空数据
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean clear(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.clear();
		return editor.commit(); // 提交
	}

	/**
	 * 获取配置
	 *
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return boolean
	 */
	public static boolean get(Context context, String name, boolean defaultValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(name, defaultValue);
		return value;
	}

	/**
	 * 获取配置
	 * 
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return int
	 */
	public static int get(Context context, String name, int defaultValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int value = prefs.getInt(name, defaultValue);
		return value;
	}

	/**
	 * 获取配置
	 * 
	 * @param context
	 * @param name
	 * @return String
	 */
	public static String get(Context context, String name) {
		return get(context,name,"");
	}
	/**
	 * 获取配置
	 *
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return String
	 */
	public static String get(Context context, String name, String defaultValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String value = prefs.getString(name, defaultValue);
		return value;
	}

	/**
	 * 获取配置
	 * 
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return float
	 */
	public static float get(Context context, String name, float defaultValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		float value = prefs.getFloat(name, defaultValue);
		return value;
	}

	private static SharedPreferences getCustomSP(Context context){
		return context.getSharedPreferences("CUS",Context.MODE_PRIVATE);
	}
	/**
	 * 保存用户配置
	 * 不清理，除非APP卸载
	 *
	 * @param context
	 * @param name
	 * @param value
	 * @return boolean
	 */
	public static boolean setCustomSPBool(Context context, String name, boolean value) {
		SharedPreferences prefs = getCustomSP(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit(); // 提交
	}

	/**
	 * 获取用户配置
	 * 不清理，除非APP卸载
	 *
	 * @param context
	 * @param name
	 * @param value
	 * @return boolean
	 */
	public static boolean getCustomSPBool(Context context, String name, boolean value) {
		SharedPreferences prefs = getCustomSP(context);
		return prefs.getBoolean(name, value);
	}
	/**
	 * 保存用户配置
	 *
	 * @param context
	 * @param name
	 * @param value
	 * @return boolean
	 */
	public static boolean set(Context context, String name, boolean value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit(); // 提交
	}

	/**
	 * 保存用户配置
	 * 
	 * @param context
	 * @param name
	 * @param value
	 *            int类型
	 * @return
	 */
	public static boolean set(Context context, String name, int value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putInt(name, value);
		return editor.commit(); // 提交
	}

	/**
	 * 保存用户配置
	 * 
	 * @param context
	 * @param name
	 * @param value
	 *            String类型
	 * @return
	 */
	public static boolean set(Context context, String name, String value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(name, value);
		return editor.commit(); // 提交
	}

	/**
	 * 保存用户配置
	 * 
	 * @param context
	 * @param name
	 * @param value
	 *            float类型
	 * @return
	 */
	public static boolean set(Context context, String name, float value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putFloat(name, value);
		return editor.commit(); // 提交
	}

	/**
	 * 获取当前版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 切换语言（中-英）
	 */
	public static void switchLanguage(Context context,String language) {
		//设置应用语言类型
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		if ("en".equals(language)) {
			config.locale = Locale.ENGLISH;
		} else {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}
		resources.updateConfiguration(config, dm);

		//保存设置语言的类型
		set(context,"language", language);
	}

	public static void removeKey(Context context,String key){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}

}
