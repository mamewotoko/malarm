package com.mamewo.malarm24;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
	private TimePicker mTimePicker;
	private String mTime = "7:00";
	
	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.time_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mTimePicker = (TimePicker) view.findViewById(R.id.time_pref_time_picker);
		mTimePicker.setIs24HourView(true);
		final String[] split_timestr = mTime.split(":");
		mTimePicker.setCurrentHour(Integer.valueOf(split_timestr[0]));
		mTimePicker.setCurrentMinute(Integer.valueOf(split_timestr[1]));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			mTime = mTimePicker.getCurrentHour() + ":" + mTimePicker.getCurrentMinute();
			setTime(mTime);
		}
	}
	
	private void setTime(String result) {
		if (callChangeListener(result)) {
			mTime = result;
			persistString(result);
		}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		String time;
		if (restoreValue) {
			time = getPersistedString(mTime);
		} else {
			time = (String) defaultValue;
		}
		setTime(time);
	}
}
