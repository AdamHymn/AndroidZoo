package zoo.hymn.utils;

import android.app.Activity;

/**
 * ClassName: TimePicker
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/2/28
 */


public class TimePicker extends DateTimePicker {
    public TimePicker(Activity activity) {
        this(activity, 3);
    }

    public TimePicker(Activity activity, int mode) {
        super(activity, -1, mode);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setLabel(String yearLabel, String monthLabel, String dayLabel, String hourLabel, String minuteLabel) {
        super.setLabel(yearLabel, monthLabel, dayLabel, hourLabel, minuteLabel);
    }

    public void setLabel(String hourLabel, String minuteLabel) {
        super.setLabel("", "", "", hourLabel, minuteLabel);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setDateRangeStart(int startYear, int startMonth, int startDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setDateRangeEnd(int endYear, int endMonth, int endDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setDateRangeStart(int startYearOrMonth, int startMonthOrDay) {
        throw new UnsupportedOperationException("Date range nonsupport");
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setDateRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        throw new UnsupportedOperationException("Data range nonsupport");
    }

    /** @deprecated */
    @Override
    @Deprecated
    public void setTimeRangeStart(int startHour, int startMinute) {
        super.setTimeRangeStart(startHour, startMinute);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public void setTimeRangeEnd(int endHour, int endMinute) {
        super.setTimeRangeEnd(endHour, endMinute);
    }

    /** @deprecated */
    @Deprecated
    public void setRange(int startHour, int endHour) {
        super.setTimeRangeStart(startHour, 0);
        super.setTimeRangeEnd(endHour, 59);
    }

    public void setRangeStart(int startHour, int startMinute) {
        super.setTimeRangeStart(startHour, startMinute);
    }

    public void setRangeEnd(int endHour, int endMinute) {
        super.setTimeRangeEnd(endHour, endMinute);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setSelectedItem(int year, int month, int day, int hour, int minute) {
        super.setSelectedItem(year, month, day, hour, minute);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setSelectedItem(int yearOrMonth, int monthOrDay, int hour, int minute) {
        super.setSelectedItem(yearOrMonth, monthOrDay, hour, minute);
    }

    public void setSelectedItem(int hour, int minute) {
        super.setSelectedItem(0, 0, hour, minute);
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setOnWheelListener(DateTimePicker.OnWheelListener onWheelListener) {
        super.setOnWheelListener(onWheelListener);
    }

    public void setOnWheelListener(final TimePicker.OnWheelListener listener) {
        if(null != listener) {
            super.setOnWheelListener(new DateTimePicker.OnWheelListener() {
                @Override
                public void onYearWheeled(int index, String year) {
                }

                @Override
                public void onMonthWheeled(int index, String month) {
                }

                @Override
                public void onDayWheeled(int index, String day) {
                }

                @Override
                public void onHourWheeled(int index, String hour) {
                    listener.onHourWheeled(index, hour);
                }

                @Override
                public void onMinuteWheeled(int index, String minute) {
                    listener.onMinuteWheeled(index, minute);
                }
            });
        }
    }

    /** @deprecated */
    @Override
    @Deprecated
    public final void setOnDateTimePickListener(OnDateTimePickListener listener) {
        super.setOnDateTimePickListener(listener);
    }

    public void setOnTimePickListener(final TimePicker.OnTimePickListener listener) {
        if(null != listener) {
            super.setOnDateTimePickListener(new DateTimePicker.OnTimePickListener() {
                @Override
                public void onDateTimePicked(String hour, String minute) {
                    listener.onTimePicked(hour, minute);
                }
            });
        }
    }

    public interface OnWheelListener {
        void onHourWheeled(int var1, String var2);

        void onMinuteWheeled(int var1, String var2);
    }

    public interface OnTimePickListener {
        void onTimePicked(String var1, String var2);
    }
}

