package zoo.hymn.utils;

/**
 * ClassName: DateTimePicker
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/2/28
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import cn.addapp.pickers.adapter.ArrayWheelAdapter;
import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.WheelPicker;
import cn.addapp.pickers.util.DateUtils;
import cn.addapp.pickers.util.LogUtils;
import cn.addapp.pickers.widget.WheelListView;
import cn.addapp.pickers.widget.WheelView;

/**
 * 修复了bug;
 * 1.设置默认值的时分无效的bug
 * 2.获取时间的时分无效的bug
 */
public class DateTimePicker extends WheelPicker {
    public static final int NONE = -1;
    public static final int YEAR_MONTH_DAY = 0;
    public static final int YEAR_MONTH = 1;
    public static final int MONTH_DAY = 2;
    public static final int HOUR_24 = 3;
    public static final int HOUR_12 = 4;
    private ArrayList<String> years;
    private ArrayList<String> months;
    private ArrayList<String> days;
    private ArrayList<String> hours;
    private ArrayList<String> minutes;
    private String yearLabel;
    private String monthLabel;
    private String dayLabel;
    private String hourLabel;
    private String minuteLabel;
    private int selectedYearIndex;
    private int selectedMonthIndex;
    private int selectedDayIndex;
    private int selectedHourIndex;
    private int selectedMinuteIndex;
    private String selectedHour;
    private String selectedMinute;
    private OnWheelListener onWheelListener;
    private OnDateTimePickListener onDateTimePickListener;
    private int dateMode;
    private int timeMode;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public DateTimePicker(Activity activity, int timeMode) {
        this(activity, 0, timeMode);
    }

    public DateTimePicker(Activity activity, int dateMode, int timeMode) {
        super(activity);
        this.years = new ArrayList();
        this.months = new ArrayList();
        this.days = new ArrayList();
        this.hours = new ArrayList();
        this.minutes = new ArrayList();
        this.yearLabel = "年";
        this.monthLabel = "月";
        this.dayLabel = "日";
        this.hourLabel = "时";
        this.minuteLabel = "分";
        this.selectedYearIndex = 0;
        this.selectedMonthIndex = 0;
        this.selectedDayIndex = 0;
        this.selectedHourIndex = 0;
        this.selectedMinuteIndex = 0;
        this.selectedHour = "";
        this.selectedMinute = "";
        this.dateMode = 0;
        this.timeMode = 3;
        this.startYear = 2010;
        this.startMonth = 1;
        this.startDay = 1;
        this.endYear = 2020;
        this.endMonth = 12;
        this.endDay = 31;
        this.startMinute = 0;
        this.endMinute = 59;
        if(dateMode == -1 && timeMode == -1) {
            throw new IllegalArgumentException("The modes are NONE at the same time");
        } else {
            if(dateMode == 0 && timeMode != -1) {
                if(this.screenWidthPixels < 720) {
                    this.textSize = 14;
                } else if(this.screenWidthPixels < 480) {
                    this.textSize = 12;
                }
            }

            this.dateMode = dateMode;
            if(timeMode == 4) {
                this.startHour = 1;
                this.endHour = 12;
            } else {
                this.startHour = 0;
                this.endHour = 23;
            }

            this.timeMode = timeMode;
        }
    }

    public void setDateRangeStart(int startYear, int startMonth, int startDay) {
        if(this.dateMode == -1) {
            throw new IllegalArgumentException("Date mode invalid");
        } else {
            this.startYear = startYear;
            this.startMonth = startMonth;
            this.startDay = startDay;
        }
    }

    public void setDateRangeEnd(int endYear, int endMonth, int endDay) {
        if(this.dateMode == -1) {
            throw new IllegalArgumentException("Date mode invalid");
        } else {
            this.endYear = endYear;
            this.endMonth = endMonth;
            this.endDay = endDay;
            this.initYearData();
        }
    }

    public void setDateRangeStart(int startYearOrMonth, int startMonthOrDay) {
        if(this.dateMode == -1) {
            throw new IllegalArgumentException("Date mode invalid");
        } else if(this.dateMode == 0) {
            throw new IllegalArgumentException("Not support year/month/day mode");
        } else {
            if(this.dateMode == 1) {
                this.startYear = startYearOrMonth;
                this.startMonth = startMonthOrDay;
            } else if(this.dateMode == 2) {
                int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                this.startYear = this.endYear = year;
                this.startMonth = startYearOrMonth;
                this.startDay = startMonthOrDay;
            }

        }
    }

    public void setDateRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        if(this.dateMode == -1) {
            throw new IllegalArgumentException("Date mode invalid");
        } else if(this.dateMode == 0) {
            throw new IllegalArgumentException("Not support year/month/day mode");
        } else {
            if(this.dateMode == 1) {
                this.endYear = endYearOrMonth;
                this.endMonth = endMonthOrDay;
            } else if(this.dateMode == 2) {
                this.endMonth = endYearOrMonth;
                this.endDay = endMonthOrDay;
            }

            this.initYearData();
        }
    }

    public void setTimeRangeStart(int startHour, int startMinute) {
        if(this.timeMode == -1) {
            throw new IllegalArgumentException("Time mode invalid");
        } else {
            boolean illegal = false;
            if(startHour < 0 || startMinute < 0 || startMinute > 59) {
                illegal = true;
            }

            if(this.timeMode == 4 && (startHour == 0 || startHour > 12)) {
                illegal = true;
            }

            if(this.timeMode == 3 && startHour >= 24) {
                illegal = true;
            }

            if(illegal) {
                throw new IllegalArgumentException("Time out of range");
            } else {
                this.startHour = startHour;
                this.startMinute = startMinute;
            }
        }
    }

    public void setTimeRangeEnd(int endHour, int endMinute) {
        if(this.timeMode == -1) {
            throw new IllegalArgumentException("Time mode invalid");
        } else {
            boolean illegal = false;
            if(endHour < 0 || endMinute < 0 || endMinute > 59) {
                illegal = true;
            }

            if(this.timeMode == 4 && (endHour == 0 || endHour > 12)) {
                illegal = true;
            }

            if(this.timeMode == 3 && endHour >= 24) {
                illegal = true;
            }

            if(illegal) {
                throw new IllegalArgumentException("Time out of range");
            } else {
                this.endHour = endHour;
                this.endMinute = endMinute;
                this.initHourData();
            }
        }
    }

    public void setLabel(String yearLabel, String monthLabel, String dayLabel, String hourLabel, String minuteLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
        this.hourLabel = hourLabel;
        this.minuteLabel = minuteLabel;
    }

    public void setSelectedItem(int year, int month, int day, int hour, int minute) {
        if(this.dateMode != 0) {
            throw new IllegalArgumentException("Date mode invalid");
        } else {
            LogUtils.verbose(this, "change months and days while set selected");
            this.changeMonthData(year);
            this.changeDayData(year, month);
            this.selectedYearIndex = this.findItemIndex(this.years, year);
            this.selectedMonthIndex = this.findItemIndex(this.months, month);
            this.selectedDayIndex = this.findItemIndex(this.days, day);
            if(this.timeMode != -1) {
                this.selectedHourIndex = hour;
                this.selectedMinuteIndex = minute;
            }

        }
    }

    public void setSelectedItem(int yearOrMonth, int monthOrDay, int hour, int minute) {
        if(this.dateMode == 0) {
            throw new IllegalArgumentException("Date mode invalid");
        } else {
            if(this.dateMode == 2) {
                LogUtils.verbose(this, "change months and days while set selected");
                int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                this.startYear = this.endYear = year;
                this.changeMonthData(year);
                this.changeDayData(year, yearOrMonth);
                this.selectedMonthIndex = this.findItemIndex(this.months, yearOrMonth);
                this.selectedDayIndex = this.findItemIndex(this.days, monthOrDay);
            } else if(this.dateMode == 1) {
                LogUtils.verbose(this, "change months while set selected");
                this.changeMonthData(yearOrMonth);
                this.selectedYearIndex = this.findItemIndex(this.years, yearOrMonth);
                this.selectedMonthIndex = this.findItemIndex(this.months, monthOrDay);
            }

            if(this.timeMode != -1) {
                this.selectedHourIndex = hour;
                this.selectedMinuteIndex = minute;
            }

        }
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnDateTimePickListener(OnDateTimePickListener listener) {
        this.onDateTimePickListener = listener;
    }

    public String getSelectedYear() {
        if(this.dateMode != 0 && this.dateMode != 1) {
            return "";
        } else {
            if(this.years.size() <= this.selectedYearIndex) {
                this.selectedYearIndex = this.years.size() - 1;
            }

            return (String)this.years.get(this.selectedYearIndex);
        }
    }

    public String getSelectedMonth() {
        if(this.dateMode != -1) {
            if(this.months.size() <= this.selectedMonthIndex) {
                this.selectedMonthIndex = this.months.size() - 1;
            }

            return (String)this.months.get(this.selectedMonthIndex);
        } else {
            return "";
        }
    }

    public String getSelectedDay() {
        if(this.dateMode != 0 && this.dateMode != 2) {
            return "";
        } else {
            if(this.days.size() <= this.selectedDayIndex) {
                this.selectedDayIndex = this.days.size() - 1;
            }

            return (String)this.days.get(this.selectedDayIndex);
        }
    }

    public String getSelectedHour() {
        return this.timeMode != -1?this.hours.get(selectedHourIndex):"";
    }

    public String getSelectedMinute() {
        return this.timeMode != -1?this.minutes.get(selectedMinuteIndex):"";
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        if((this.dateMode == 0 || this.dateMode == 1) && this.years.size() == 0) {
            LogUtils.verbose(this, "init years before make view");
            this.initYearData();
        }

        int layout;
        if(this.dateMode != -1 && this.months.size() == 0) {
            LogUtils.verbose(this, "init months before make view");
            layout = DateUtils.trimZero(this.getSelectedYear());
            this.changeMonthData(layout);
        }

        if((this.dateMode == 0 || this.dateMode == 2) && this.days.size() == 0) {
            LogUtils.verbose(this, "init days before make view");
            if(this.dateMode == 0) {
                layout = DateUtils.trimZero(this.getSelectedYear());
            } else {
                layout = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            }

            int wheelViewParams = DateUtils.trimZero(this.getSelectedMonth());
            this.changeDayData(layout, wheelViewParams);
        }

        if(this.timeMode != -1 && this.hours.size() == 0) {
            LogUtils.verbose(this, "init hours before make view");
            this.initHourData();
        }

        if(this.timeMode != -1 && this.minutes.size() == 0) {
            LogUtils.verbose(this, "init minutes before make view");
            this.changeMinuteData(DateUtils.trimZero(this.selectedHour));
        }

        LinearLayout layout1 = new LinearLayout(this.activity);
        layout1.setOrientation(LinearLayout.HORIZONTAL);
        layout1.setGravity(17);
        layout1.setWeightSum(5.0F);
        LinearLayout.LayoutParams wheelViewParams1;
        if(this.weightEnable) {
            wheelViewParams1 = new LinearLayout.LayoutParams(0, -2);
            wheelViewParams1.weight = 1.0F;
        } else {
            wheelViewParams1 = new LinearLayout.LayoutParams(-2, -2);
        }

        LinearLayout.LayoutParams labelViewParams = new LinearLayout.LayoutParams(-2, -2);
        TextView labelView;
        if(this.wheelModeEnable) {
            WheelView yearView = new WheelView(this.activity);
            final WheelView monthView = new WheelView(this.activity);
            final WheelView dayView = new WheelView(this.activity);
            WheelView hourView = new WheelView(this.activity);
            final WheelView minuteView = new WheelView(this.activity);
            if(this.dateMode == 0 || this.dateMode == 1) {
                yearView.setCanLoop(this.canLoop);
                yearView.setTextSize((float)this.textSize);
                yearView.setSelectedTextColor(this.textColorFocus);
                yearView.setUnSelectedTextColor(this.textColorNormal);
                yearView.setAdapter(new ArrayWheelAdapter(this.years));
                yearView.setCurrentItem(this.selectedYearIndex);
                yearView.setDividerType(LineConfig.DividerType.FILL);
                yearView.setLayoutParams(wheelViewParams1);
                yearView.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        selectedYearIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onYearWheeled(selectedYearIndex, item);
                        }

                        LogUtils.verbose(this, "change months after year wheeled");
                        selectedMonthIndex = 0;
                        selectedDayIndex = 0;
                        int selectedYear = DateUtils.trimZero(item);
                        changeMonthData(selectedYear);
                        monthView.setAdapter(new ArrayWheelAdapter(months));
                        monthView.setCurrentItem(selectedMonthIndex);
                        changeDayData(selectedYear, DateUtils.trimZero((String) months.get(selectedMonthIndex)));
                        dayView.setAdapter(new ArrayWheelAdapter(days));
                        dayView.setCurrentItem(selectedDayIndex);
                    }
                });
                layout1.addView(yearView);
                if(!TextUtils.isEmpty(this.yearLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setText(this.yearLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.dateMode != -1) {
                monthView.setCanLoop(this.canLoop);
                monthView.setTextSize((float)this.textSize);
                monthView.setSelectedTextColor(this.textColorFocus);
                monthView.setUnSelectedTextColor(this.textColorNormal);
                monthView.setAdapter(new ArrayWheelAdapter(months));
                monthView.setCurrentItem(this.selectedMonthIndex);
                monthView.setDividerType(LineConfig.DividerType.FILL);
                monthView.setLayoutParams(wheelViewParams1);
                monthView.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        selectedMonthIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onMonthWheeled(selectedMonthIndex, item);
                        }

                        if(dateMode == 0 || dateMode == 2) {
                            LogUtils.verbose(this, "change days after month wheeled");
                            selectedDayIndex = 0;
                            int selectedYear;
                            if(dateMode == 0) {
                                selectedYear = DateUtils.trimZero(getSelectedYear());
                            } else {
                                selectedYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                            }

                            changeDayData(selectedYear, DateUtils.trimZero(item));
                            dayView.setAdapter(new ArrayWheelAdapter(days));
                            dayView.setCurrentItem(selectedDayIndex);
                        }

                    }
                });
                layout1.addView(monthView);
                if(!TextUtils.isEmpty(this.monthLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setText(this.monthLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.dateMode == 0 || this.dateMode == 2) {
                dayView.setCanLoop(this.canLoop);
                dayView.setTextSize((float)this.textSize);
                dayView.setSelectedTextColor(this.textColorFocus);
                dayView.setUnSelectedTextColor(this.textColorNormal);
                dayView.setAdapter(new ArrayWheelAdapter(this.days));
                dayView.setCurrentItem(this.selectedDayIndex);
                dayView.setDividerType(LineConfig.DividerType.FILL);
                dayView.setLayoutParams(wheelViewParams1);
                dayView.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        selectedDayIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onDayWheeled(index, item);
                        }

                    }
                });
                layout1.addView(dayView);
                if(!TextUtils.isEmpty(this.dayLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setText(this.dayLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.timeMode != -1) {
                hourView.setCanLoop(this.canLoop);
                hourView.setTextSize((float)this.textSize);
                hourView.setSelectedTextColor(this.textColorFocus);
                hourView.setUnSelectedTextColor(this.textColorNormal);
                hourView.setDividerType(LineConfig.DividerType.FILL);
                hourView.setAdapter(new ArrayWheelAdapter(this.hours));
                hourView.setCurrentItem(this.selectedHourIndex);
                hourView.setLayoutParams(wheelViewParams1);
                hourView.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        selectedHourIndex = index;
                        selectedMinuteIndex = 0;
                        selectedHour = item;
                        if(onWheelListener != null) {
                            onWheelListener.onHourWheeled(index, item);
                        }

                        changeMinuteData(DateUtils.trimZero(item));
                        minuteView.setAdapter(new ArrayWheelAdapter(minutes));
                        minuteView.setCurrentItem(selectedMinuteIndex);
                    }
                });
                layout1.addView(hourView);
                if(!TextUtils.isEmpty(this.hourLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setText(this.hourLabel);
                    layout1.addView(labelView);
                }

                minuteView.setCanLoop(this.canLoop);
                minuteView.setTextSize((float)this.textSize);
                minuteView.setSelectedTextColor(this.textColorFocus);
                minuteView.setUnSelectedTextColor(this.textColorNormal);
                minuteView.setAdapter(new ArrayWheelAdapter(this.minutes));
                minuteView.setCurrentItem(this.selectedMinuteIndex);
                minuteView.setDividerType(LineConfig.DividerType.FILL);
                minuteView.setLayoutParams(wheelViewParams1);
                layout1.addView(minuteView);
                minuteView.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        selectedMinuteIndex = index;
                        selectedMinute = item;
                        if(onWheelListener != null) {
                            onWheelListener.onMinuteWheeled(index, item);
                        }

                    }
                });
                if(!TextUtils.isEmpty(this.minuteLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setText(this.minuteLabel);
                    layout1.addView(labelView);
                }
            }
        } else {
            WheelListView yearView1 = new WheelListView(this.activity);
            final WheelListView monthView1 = new WheelListView(this.activity);
            final WheelListView dayView1 = new WheelListView(this.activity);
            WheelListView hourView1 = new WheelListView(this.activity);
            final WheelListView minuteView1 = new WheelListView(this.activity);
            if(this.dateMode == 0 || this.dateMode == 1) {
                yearView1.setLayoutParams(wheelViewParams1);
                yearView1.setTextSize(this.textSize);
                yearView1.setSelectedTextColor(this.textColorFocus);
                yearView1.setUnSelectedTextColor(this.textColorNormal);
                yearView1.setLineConfig(this.lineConfig);
                yearView1.setOffset(this.offset);
                yearView1.setCanLoop(this.canLoop);
                yearView1.setItems(this.years, this.selectedYearIndex);
                yearView1.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                    @Override
                    public void onItemSelected(boolean isUserScroll, int index, String item) {
                        selectedYearIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onYearWheeled(selectedYearIndex, item);
                        }

                        if(isUserScroll) {
                            LogUtils.verbose(this, "change months after year wheeled");
                            selectedMonthIndex = 0;
                            selectedDayIndex = 0;
                            int selectedYear = DateUtils.trimZero(item);
                            changeMonthData(selectedYear);
                            monthView1.setItems(months, selectedMonthIndex);
                            changeDayData(selectedYear, DateUtils.trimZero((String) months.get(selectedMonthIndex)));
                            dayView1.setItems(days, selectedDayIndex);
                        }
                    }
                });
                layout1.addView(yearView1);
                if(!TextUtils.isEmpty(this.yearLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setText(this.yearLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.dateMode != -1) {
                monthView1.setLayoutParams(wheelViewParams1);
                monthView1.setTextSize(this.textSize);
                monthView1.setSelectedTextColor(this.textColorFocus);
                monthView1.setUnSelectedTextColor(this.textColorNormal);
                monthView1.setLineConfig(this.lineConfig);
                monthView1.setOffset(this.offset);
                monthView1.setCanLoop(this.canLoop);
                monthView1.setItems(this.months, this.selectedMonthIndex);
                monthView1.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                    @Override
                    public void onItemSelected(boolean isUserScroll, int index, String item) {
                        selectedMonthIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onMonthWheeled(selectedMonthIndex, item);
                        }

                        if(isUserScroll) {
                            if(dateMode == 0 || dateMode == 2) {
                                LogUtils.verbose(this, "change days after month wheeled");
                                selectedDayIndex = 0;
                                int selectedYear;
                                if(dateMode == 0) {
                                    selectedYear = DateUtils.trimZero(getSelectedYear());
                                } else {
                                    selectedYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                                }

                                changeDayData(selectedYear, DateUtils.trimZero(item));
                                dayView1.setItems(days, selectedDayIndex);
                            }

                        }
                    }
                });
                layout1.addView(monthView1);
                if(!TextUtils.isEmpty(this.monthLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setText(this.monthLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.dateMode == 0 || this.dateMode == 2) {
                dayView1.setLayoutParams(wheelViewParams1);
                dayView1.setTextSize(this.textSize);
                dayView1.setSelectedTextColor(this.textColorFocus);
                dayView1.setUnSelectedTextColor(this.textColorNormal);
                dayView1.setLineConfig(this.lineConfig);
                dayView1.setOffset(this.offset);
                dayView1.setCanLoop(this.canLoop);
                dayView1.setItems(this.days, this.selectedDayIndex);
                dayView1.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                    @Override
                    public void onItemSelected(boolean isUserScroll, int index, String item) {
                        selectedDayIndex = index;
                        if(onWheelListener != null) {
                            onWheelListener.onDayWheeled(selectedDayIndex, item);
                        }

                    }
                });
                layout1.addView(dayView1);
                if(!TextUtils.isEmpty(this.dayLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setText(this.dayLabel);
                    layout1.addView(labelView);
                }
            }

            if(this.timeMode != -1) {
                hourView1.setLayoutParams(wheelViewParams1);
                hourView1.setTextSize(this.textSize);
                hourView1.setSelectedTextColor(this.textColorFocus);
                hourView1.setUnSelectedTextColor(this.textColorNormal);
                hourView1.setLineConfig(this.lineConfig);
                hourView1.setCanLoop(this.canLoop);
                hourView1.setItems(this.hours, this.selectedHour);
                hourView1.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                    @Override
                    public void onItemSelected(boolean isUserScroll, int index, String item) {
                        selectedHourIndex = index;
                        selectedMinuteIndex = 0;
                        selectedHour = item;
                        if(onWheelListener != null) {
                            onWheelListener.onHourWheeled(index, item);
                        }

                        if(isUserScroll) {
                            LogUtils.verbose(this, "change minutes after hour wheeled");
                            changeMinuteData(DateUtils.trimZero(item));
                            minuteView1.setItems(minutes, selectedMinuteIndex);
                        }
                    }
                });
                layout1.addView(hourView1);
                if(!TextUtils.isEmpty(this.hourLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setText(this.hourLabel);
                    layout1.addView(labelView);
                }

                minuteView1.setLayoutParams(wheelViewParams1);
                minuteView1.setTextSize(this.textSize);
                minuteView1.setSelectedTextColor(this.textColorFocus);
                minuteView1.setUnSelectedTextColor(this.textColorNormal);
                minuteView1.setLineConfig(this.lineConfig);
                minuteView1.setOffset(this.offset);
                minuteView1.setCanLoop(this.canLoop);
                minuteView1.setItems(this.minutes, this.selectedMinute);
                minuteView1.setOnWheelChangeListener(new WheelListView.OnWheelChangeListener() {
                    @Override
                    public void onItemSelected(boolean isUserScroll, int index, String item) {
                        selectedMinuteIndex = index;
                        selectedMinute = item;
                        if(onWheelListener != null) {
                            onWheelListener.onMinuteWheeled(index, item);
                        }

                    }
                });
                layout1.addView(minuteView1);
                if(!TextUtils.isEmpty(this.minuteLabel)) {
                    labelView = new TextView(this.activity);
                    labelView.setLayoutParams(labelViewParams);
                    labelView.setTextSize((float)this.textSize);
                    labelView.setTextColor(this.textColorFocus);
                    labelView.setText(this.minuteLabel);
                    layout1.addView(labelView);
                }
            }
        }

        return layout1;
    }

    @Override
    protected void onSubmit() {
        if(this.onDateTimePickListener != null) {
            String year = this.getSelectedYear();
            String month = this.getSelectedMonth();
            String day = this.getSelectedDay();
            String hour = this.getSelectedHour();
            String minute = this.getSelectedMinute();
            switch(this.dateMode) {
                case -1:
                    ((OnTimePickListener)this.onDateTimePickListener).onDateTimePicked(hour, minute);
                    break;
                case 0:
                    ((OnYearMonthDayTimePickListener)this.onDateTimePickListener).onDateTimePicked(year, month, day, hour, minute);
                    break;
                case 1:
                    ((OnYearMonthTimePickListener)this.onDateTimePickListener).onDateTimePicked(year, month, hour, minute);
                    break;
                case 2:
                    ((OnMonthDayTimePickListener)this.onDateTimePickListener).onDateTimePicked(month, day, hour, minute);
            }

        }
    }

    private int findItemIndex(ArrayList<String> items, int item) {
        int index = Collections.binarySearch(items, Integer.valueOf(item), new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String lhsStr = lhs.toString();
                String rhsStr = rhs.toString();
                lhsStr = lhsStr.startsWith("0")?lhsStr.substring(1):lhsStr;
                rhsStr = rhsStr.startsWith("0")?rhsStr.substring(1):rhsStr;
                return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
            }
        });
        if(index < 0) {
            throw new IllegalArgumentException("Item[" + item + "] out of range");
        } else {
            return index;
        }
    }

    private void initYearData() {
        this.years.clear();
        if(this.startYear == this.endYear) {
            this.years.add(String.valueOf(this.startYear));
        } else {
            int i;
            if(this.startYear < this.endYear) {
                for(i = this.startYear; i <= this.endYear; ++i) {
                    this.years.add(String.valueOf(i));
                }
            } else {
                for(i = this.startYear; i >= this.endYear; --i) {
                    this.years.add(String.valueOf(i));
                }
            }
        }

    }

    private void changeMonthData(int selectedYear) {
        this.months.clear();
        if(this.startMonth >= 1 && this.endMonth >= 1 && this.startMonth <= 12 && this.endMonth <= 12) {
            int i;
            if(this.startYear == this.endYear) {
                if(this.startMonth > this.endMonth) {
                    for(i = this.endMonth; i >= this.startMonth; --i) {
                        this.months.add(DateUtils.fillZero(i));
                    }
                } else {
                    for(i = this.startMonth; i <= this.endMonth; ++i) {
                        this.months.add(DateUtils.fillZero(i));
                    }
                }
            } else if(selectedYear == this.startYear) {
                for(i = this.startMonth; i <= 12; ++i) {
                    this.months.add(DateUtils.fillZero(i));
                }
            } else if(selectedYear == this.endYear) {
                for(i = 1; i <= this.endMonth; ++i) {
                    this.months.add(DateUtils.fillZero(i));
                }
            } else {
                for(i = 1; i <= 12; ++i) {
                    this.months.add(DateUtils.fillZero(i));
                }
            }

        } else {
            throw new IllegalArgumentException("Month out of range [1-12]");
        }
    }

    private void changeDayData(int selectedYear, int selectedMonth) {
        int maxDays = DateUtils.calculateDaysInMonth(selectedYear, selectedMonth);
        this.days.clear();
        int i;
        if(selectedYear == this.startYear && selectedMonth == this.startMonth && selectedYear == this.endYear && selectedMonth == this.endMonth) {
            for(i = this.startDay; i <= this.endDay; ++i) {
                this.days.add(DateUtils.fillZero(i));
            }
        } else if(selectedYear == this.startYear && selectedMonth == this.startMonth) {
            for(i = this.startDay; i <= maxDays; ++i) {
                this.days.add(DateUtils.fillZero(i));
            }
        } else if(selectedYear == this.endYear && selectedMonth == this.endMonth) {
            for(i = 1; i <= this.endDay; ++i) {
                this.days.add(DateUtils.fillZero(i));
            }
        } else {
            for(i = 1; i <= maxDays; ++i) {
                this.days.add(DateUtils.fillZero(i));
            }
        }

    }

    private void initHourData() {
        for(int i = this.startHour; i <= this.endHour; ++i) {
            String hour = DateUtils.fillZero(i);
            this.hours.add(hour);
        }

        if(this.hours.indexOf(this.selectedHour) == -1) {
            this.selectedHour = (String)this.hours.get(0);
        }

    }

    private void changeMinuteData(int selectedHour) {
        int i;
        if(this.startHour == this.endHour) {
            if(this.startMinute > this.endMinute) {
                i = this.startMinute;
                this.startMinute = this.endMinute;
                this.endMinute = i;
            }

            for(i = this.startMinute; i <= this.endMinute; ++i) {
                this.minutes.add(DateUtils.fillZero(i));
            }
        } else if(selectedHour == this.startHour) {
            for(i = this.startMinute; i <= 59; ++i) {
                this.minutes.add(DateUtils.fillZero(i));
            }
        } else if(selectedHour == this.endHour) {
            for(i = 0; i <= this.endMinute; ++i) {
                this.minutes.add(DateUtils.fillZero(i));
            }
        } else {
            for(i = 0; i <= 59; ++i) {
                this.minutes.add(DateUtils.fillZero(i));
            }
        }

        if(this.minutes.indexOf(this.selectedMinute) == -1) {
            this.selectedMinute = (String)this.minutes.get(0);
        }

    }

    public interface OnTimePickListener extends OnDateTimePickListener {
        void onDateTimePicked(String var1, String var2);
    }

    public interface OnMonthDayTimePickListener extends OnDateTimePickListener {
        void onDateTimePicked(String var1, String var2, String var3, String var4);
    }

    public interface OnYearMonthTimePickListener extends OnDateTimePickListener {
        void onDateTimePicked(String var1, String var2, String var3, String var4);
    }

    public interface OnYearMonthDayTimePickListener extends OnDateTimePickListener {
        void onDateTimePicked(String var1, String var2, String var3, String var4, String var5);
    }

    protected interface OnDateTimePickListener {
    }

    public interface OnWheelListener {
        void onYearWheeled(int var1, String var2);

        void onMonthWheeled(int var1, String var2);

        void onDayWheeled(int var1, String var2);

        void onHourWheeled(int var1, String var2);

        void onMinuteWheeled(int var1, String var2);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface DateMode {
    }
}
