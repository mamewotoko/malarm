package com.mamewo.malarm24;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference
        extends DialogPreference {
    private TimePicker timePicker_;
    private String time_ = "7:00";

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.time_preference);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        timePicker_ = (TimePicker) view.findViewById(R.id.time_pref_time_picker);
        timePicker_.setIs24HourView(true);
        String[] split_timestr = time_.split(":");
        timePicker_.setCurrentHour(Integer.valueOf(split_timestr[0]));
        timePicker_.setCurrentMinute(Integer.valueOf(split_timestr[1]));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            time_ = timePicker_.getCurrentHour() + ":" + timePicker_.getCurrentMinute();
            setTime(time_);
        }
    }

    private void setTime(String result) {
        if (callChangeListener(result)) {
            time_ = result;
            persistString(result);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;
        if (restoreValue) {
            time = getPersistedString(time_);
        }
        else {
            time = (String) defaultValue;
        }
        setTime(time);
    }
}
