/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zoo.hymn.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import cn.addapp.pickers.picker.DatePicker;
import zoo.hymn.views.EditText.ClearEditText;

/**
 * 日期处理类.
 */
public class DateUtil {


    public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String dateFormatYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";
    public static final String dateFormatYMD = "yyyy-MM-dd";
    public static final String dateFormatYM = "yyyy-MM";
    public static final String dateFormatMDHM = "MM-dd HH:mm";
    public static final String dateFormatMD = "MM/dd";
    public static final String dateFormatHMS = "HH:mm:ss";
    public static final String dateFormatHM = "HH:mm";
    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";
    private static SimpleDateFormat sdf = new SimpleDateFormat();
    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟
    public static final String[] zodiacArr = {"猴", "鸡", "狗", "猪", "鼠",
            "牛", "虎", "兔", "龙", "蛇", "马", "羊"};

    /**
     * 根据日期获取生肖
     *
     * @param @param  time
     * @param @return
     * @return String
     * @throws
     * @Title: date2Zodica
     * @Description: TODO
     */
    public static String date2Zodica(Calendar time) {
        return zodiacArr[time.get(Calendar.YEAR) % 12];
    }

    /**
     * 根据生日获取星座
     *
     * @param @param  birth
     * @param @return
     * @return String
     * @throws
     * @Title: getAstro
     * @Description: TODO
     */
    public static String getAstro(String birth) {
        if (!isDate(birth)) {
            birth = "2000" + birth;
        }
        if (!isDate(birth)) {
            return "";
        }
        int month = Integer.parseInt(birth.substring(
                birth.indexOf("-") + 1, birth.lastIndexOf("-")));
        int day = Integer
                .parseInt(birth.substring(birth.lastIndexOf("-") + 1));
        String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
        int[] arr = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
        int start = month * 2 - (day < arr[month - 1] ? 2 : 0);
        return s.substring(start, start + 2) + "座";
    }

    /**
     * 判断日期是否有效,包括闰年的情况
     *
     * @param @param  date
     * @param @return
     * @return boolean
     * @throws
     * @Title: isDate
     * @Description: TODO
     */
    public static boolean isDate(String date) {
        StringBuffer reg = new StringBuffer(
                "^((\\d{2}(([02468][048])|([13579][26]))-?((((0?");
        reg.append("[13578])|(1[02]))-?((0?[1-9])|([1-2][0-9])|(3[01])))");
        reg.append("|(((0?[469])|(11))-?((0?[1-9])|([1-2][0-9])|(30)))|");
        reg.append("(0?2-?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12");
        reg.append("35679])|([13579][01345789]))-?((((0?[13578])|(1[02]))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(30)))|(0?2-?((0?[");
        reg.append("1-9])|(1[0-9])|(2[0-8]))))))");
        Pattern p = Pattern.compile(reg.toString());
        return p.matcher(date).matches();
    }

    /**
     * 根据时间戳获取描述性时间
     *
     * @param @param  timestamp 时间戳 单位为毫秒
     * @param @return
     * @return String 时间字符串
     * @throws
     * @Title: getDescriptionTimeFromTimestamp
     * @Description: TODO 如3分钟前，1天前
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        System.out.println("timeGap: " + timeGap);
        String timeStr = null;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年前";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 计算从以前的某个时间点到现在的时间长度
     */
    public static String getTimeFromBeforeToNow(long before) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - before) / 1000;// 与现在时间相差秒数
        System.out.println("timeGap: " + timeGap);
        String timeStr = null;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 距离截止日期还有多长时间
     *
     * @param @param  date 截至日期
     * @param @return
     * @return String
     * @throws
     * @Title: fromDeadline
     * @Description: TODO
     */
    public static String fromDeadline(Date date) {
        long deadline = date.getTime() / 1000;
        long now = (System.currentTimeMillis()) / 1000;
        long remain = deadline - now;
        if (remain <= HOUR) {
            return "只剩下" + remain / MINUTE + "分钟";
        } else if (remain <= DAY) {
            return "只剩下" + remain / HOUR + "小时" + (remain % HOUR / MINUTE)
                    + "分钟";
        } else {
            long day = remain / DAY;
            long hour = remain % DAY / HOUR;
            long minute = remain % DAY % HOUR / MINUTE;
            return "只剩下" + day + "天" + hour + "小时" + minute + "分钟";
        }

    }

    /**
     * 根据时间戳获取指定格式的时间
     *
     * @param @param  timestamp 时间戳 单位为毫秒
     * @param @param  format 指定格式 如果为null或空串则使用默认格式"yyyy-MM-dd HH:MM"
     * @param @return
     * @return String 时间字符串
     * @throws
     * @Title: getFormatTimeFromTimestamp
     * @Description: TODO 如2011-11-30 08:40
     */
    public static String getFormatTimeFromTimestamp(long timestamp,
                                                    String format) {
        if (format == null || "".equals(format.trim())) {
            sdf.applyPattern(FORMAT_DATE);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = Integer.valueOf(sdf.format(new Date(timestamp))
                    .substring(0, 4));
            System.out.println("currentYear: " + currentYear);
            System.out.println("year: " + year);
            if (currentYear == year) {// 如果为今年则不显示年份
                sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
            } else {
                sdf.applyPattern(FORMAT_DATE_TIME);
            }
        } else {
            sdf.applyPattern(format);
        }
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 根据时间戳获取时间字符串，并根据指定的时间分割数partionSeconds来自动判断返回描述性时间还是指定格式的时间
     *
     * @param @param  timestamp 时间戳 单位是毫秒
     * @param @param  partionSeconds
     *                时间分割线，当现在时间与指定的时间戳的秒数差大于这个分割线时则返回指定格式时间，否则返回描述性时间
     * @param @param  format
     * @param @return
     * @return String
     * @throws
     * @Title: getMixTimeFromTimestamp
     * @Description: TODO
     */
    public static String getMixTimeFromTimestamp(long timestamp,
                                                 long partionSeconds, String format) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        if (timeGap <= partionSeconds) {
            return getDescriptionTimeFromTimestamp(timestamp);
        } else {
            return getFormatTimeFromTimestamp(timestamp, format);
        }
    }

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param @param  format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @param @return
     * @return String
     * @throws
     * @Title: getCurrentTime
     * @Description: TODO
     */
    public static String getCurrentTime(String format) {
        if (format == null || "".equals(format.trim())) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    /**
     * 将日期字符串以指定格式转换为Date
     *
     * @param @param  timeStr 日期字符串
     * @param @param  format 指定的日期格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @param @return
     * @return Date
     * @throws
     * @Title: getTimeFromString
     * @Description: TODO
     */
    public static Date getTimeFromString(String timeStr, String format) {
        if (format == null || "".equals(format.trim())) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 将Date以指定格式转换为日期时间字符串
     *
     * @param @param  time 日期
     * @param @param  format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @param @return
     * @return String
     * @throws
     * @Title: getStringFromTime
     * @Description: TODO
     */
    public static String getStringFromTime(Date time, String format) {
        if (format == null || "".equals(format.trim())) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(time);
    }

    public static String getFetureDate(int past, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        if (format == null || "".equals(format.trim())) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(today);
    }

    /**
     * Description: 例如：获得当前时间之后40分钟后及后面每个15分钟的所有时间（但截至到20:30）
     *
     * @param endTime   当天截至时间 如"2030"
     * @param afterTime 当前时间多少分钟后开始计算 如40表示40分钟
     * @param spaceTime 每次计算的间隔 如： 如15表示15分钟
     * @throws Throwable
     */
    public static List<String> getTimeFrag(String endTime, int afterTime,
                                           int spaceTime) {

        List<String> list = new ArrayList<String>();
        // 分别获得
        try {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;// 从0开始计算的
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                    "yyyyMMddHHmm");
            String mTime = mSimpleDateFormat.format(new Date());

            Date now = mSimpleDateFormat.parse(mTime);
            String timeStr = year + "0" + month + "" + day + endTime;
            Date date = mSimpleDateFormat.parse(timeStr);
            long l = date.getTime() - now.getTime();
            long apartDay = l / (24 * 60 * 60 * 1000);
            long apartHour = (l / (60 * 60 * 1000) - apartDay * 24);
            long apartMin = ((l / (60 * 1000)) - apartDay * 24 * 60 - apartHour * 60);
            long s = (l / 1000 - apartDay * 24 * 60 * 60 - apartHour * 60
                    * 60 - apartMin * 60);
            System.out.println("相距" + apartDay + "天" + apartHour + "小时"
                    + apartMin + "分" + s + "秒");
            // 从小时得到的条数
            int timeFragSum = (int) (apartHour * (int) Math
                    .round(60 / spaceTime));
            // 从分钟里获得条数
            int haveSum = (int) Math.round(apartMin / spaceTime);
            // 剩下不足15分钟的时间数
            // int remainMin = Math.round(apartMin % spaceTime);
            int sum = timeFragSum + haveSum
                    - (int) Math.round(afterTime / spaceTime);// 40分钟之后时间,约3次

            String mHour = null;
            String mMinute = null;
            if (hour < 20) {
                int laterTime = minute + afterTime;
                for (int i = 0; i < sum; i++) {
                    if (i != 0) {
                        laterTime = spaceTime + laterTime;
                    }
                    if (laterTime > 60) {// 小时进1位
                        int addHour = (int) Math.round(laterTime / 60);
                        // 剩下不足15分钟的时间数
                        int addMin = Math.round(laterTime % 60);
                        hour = hour + addHour;
                        laterTime = addMin;
                    }
                    if (hour < 10) {
                        mHour = "0" + hour;
                    } else {
                        mHour = "" + hour;
                    }

                    if (laterTime < 10) {
                        mMinute = "0" + laterTime;
                    } else {
                        mMinute = "" + laterTime;
                    }
                    String object = mHour + ":" + mMinute;
                    list.add(object);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将一个时间变成一个友好的计时格式
     *
     * @param time 时间 毫秒
     * @return 时：分：秒：毫秒
     */
    public static String getFormatTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second).substring(("00" + second)
                .length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute).substring(("00" + minute)
                .length() - 2);
        // 时显示两位
        String strHour = ("00" + hour)
                .substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;
        // + strMillisecond;
    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    public static String getCurrentDate(String format) {
        Log.d("DateUtil.class", "getCurrentDate:" + format);
        String curDateTime = null;
        if (TextUtils.isEmpty(format)) {
            format = dateFormatYMDHMS;
        }
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Calendar c = new GregorianCalendar();
            c.setTimeZone(getShangHaiArea());
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    public static String getCurrentDate() {
        String str = "";
        try {
            String format = dateFormatYMDHMS;
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Date data = new Date();
            str = mSimpleDateFormat.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @return String String类型的当前日期时间
     */
    public static String getStrTime(long time) {
        String format = dateFormatYMDHMS;
        String curDateTime = "";
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Date date = new Date(time);
            curDateTime = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }


    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @return String String类型的当前日期时间
     */
    public static String getStrMDHM(long time) {
        String format = dateFormatYMDHM;
        String curDateTime = "";
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Date date = new Date(time);
            curDateTime = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;
    }


    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @return String String类型的当前日期时间
     */
    public static String getJiFenStrTime(long time) {
        String format = dateFormatYYMMDDHHMMSS;
        String curDateTime = "";
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Date date = new Date(time);
            curDateTime = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;
    }

    public static TimeZone getShangHaiArea() {
        String newTimeZoneID = "Asia/Shanghai"; //
        TimeZone timeZone = TimeZone.getTimeZone(newTimeZoneID);
        TimeZone.setDefault(timeZone);
        return timeZone;
    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @return String String类型的当前日期时间
     */
    public static String getStrTimeByMines(long time) {
        String format = dateFormatYMD;
        String curDateTime = "";
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(getShangHaiArea());
            Date date = new Date(time);
            curDateTime = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }


    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(java.util.Date dateDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(getShangHaiArea());
    }

    /**
     * 弹出年月日选择器
     *
     * @param activity
     * @param view
     * @param startYear 例如：当年的前30年 30
     * @param endYear   例如：当年的后30年 30
     */
    public static void onYearMonthDayPicker(Activity activity, final View view, int startYear, int endYear) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        DatePicker picker = new DatePicker(activity);
        picker.setDateRangeStart(year - 30, 1, 1);
        picker.setDateRangeEnd(year, 12, 31);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                if (view instanceof EditText) {
                    ((EditText) view).setText(year + "-" + month + "-" + day);
                }
                if (view instanceof TextView) {
                    ((TextView) view).setText(year + "-" + month + "-" + day);
                }
                if (view instanceof Button) {
                    ((Button) view).setText(year + "-" + month + "-" + day);
                }
            }
        });
        picker.show();
    }

    /**
     * 年月日时分选择器
     * 可以设置前后年份范围
     *
     * @param activity
     * @param view
     * @param startYear 例如：当年的前30年 30
     * @param endYear   例如：当年的后30年 30
     */
    public static void onYearMonthDayTimePicker(Activity activity, final View view, int startYear, int endYear) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            if (view instanceof EditText) {
                date = DateUtil.getTimeFromString(((EditText) view).getText().toString(), "");
            }
            if (view instanceof TextView) {
                date = DateUtil.getTimeFromString(((TextView) view).getText().toString(), "");
            }
            if (view instanceof Button) {
                date = DateUtil.getTimeFromString(((Button) view).getText().toString(), "");
            }

            if (date != null) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        DateTimePicker picker = new DateTimePicker(activity, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(year - startYear, 1, 1);
        picker.setDateRangeEnd(year + endYear, 12, 31);
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setSelectedItem(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                if (view instanceof EditText) {
                    ((EditText) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }
                if (view instanceof TextView) {
                    ((TextView) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }
                if (view instanceof Button) {
                    ((Button) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }
            }
        });
        picker.show();
    }

    /**
     * 年月日时分选择器
     *
     * @param activity
     * @param view
     */
    public static void onYearMonthDayTimePicker(Activity activity, final View view) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = null;
            if (view instanceof EditText) {
                date = DateUtil.getTimeFromString(((EditText) view).getText().toString(), "");
            }
            if (view instanceof TextView) {
                date = DateUtil.getTimeFromString(((TextView) view).getText().toString(), "");
            }
            if (view instanceof Button) {
                date = DateUtil.getTimeFromString(((Button) view).getText().toString(), "");
            }

            if (date != null) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int year = calendar.get(Calendar.YEAR);
        DateTimePicker picker = new DateTimePicker(activity, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(year - 20, 1, 1);
        picker.setDateRangeEnd(year + 2, 12, 31);
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setSelectedItem(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                if (view instanceof EditText) {
                    ((EditText) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }
                if (view instanceof TextView) {
                    ((TextView) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }
                if (view instanceof Button) {
                    ((Button) view).setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                }

            }
        });
        picker.show();
    }

    /**
     * 年月日时分选择器
     * 自己处理选中后的监听时间
     *
     * @param activity
     * @param view
     * @param listener
     */
    public static void onYearMonthDayTimePicker(Activity activity, final View view, DateTimePicker.OnYearMonthDayTimePickListener listener) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = null;
            if (view instanceof EditText) {
                date = DateUtil.getTimeFromString(((EditText) view).getText().toString(), "");
            }
            if (view instanceof TextView) {
                date = DateUtil.getTimeFromString(((TextView) view).getText().toString(), "");
            }
            if (view instanceof Button) {
                date = DateUtil.getTimeFromString(((Button) view).getText().toString(), "");
            }

            if (date != null) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int year = calendar.get(Calendar.YEAR);
        DateTimePicker picker = new DateTimePicker(activity, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(year - 20, 1, 1);
        picker.setDateRangeEnd(year + 2, 12, 31);
        picker.setTimeRangeStart(0, 0);
        picker.setTimeRangeEnd(23, 59);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setSelectedItem(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        picker.setOnDateTimePickListener(listener);
        picker.show();
    }

}
