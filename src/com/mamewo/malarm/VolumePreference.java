package com.mamewo.malarm;

import android.content.Context;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class VolumePreference extends DialogPreference {
	private int mMaxVolume;
	private int mVolume = 0;
	private EditText mEditText;
	private TextView mDialogText;
	
	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setDialogLayoutResource(R.layout.volume_preference);
	}
	
	public VolumePreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mEditText = (EditText) view.findViewById(R.id.volume_pref_value);
		mDialogText = (TextView) view.findViewById(R.id.volume_pref_text);
		mEditText.setText(Integer.toString(mVolume));
		//TODO: localize
		AudioManager mgr = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
		int current_volume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		mDialogText.setText("set volume 0 - " + mMaxVolume + " current: " + current_volume);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			String strvolume = mEditText.getText().toString();
			mVolume = Integer.parseInt(strvolume);
			setVolume(mVolume);
		}
	}
	
	private void setVolume(int v) {
		int value = v;
		if (value > mMaxVolume) {
			Log.i("malarm", "too large value: " + value);
			value = mMaxVolume;
		} else if (value < 0) {
			Log.i("malarm", "negative value: " + value);
			value = 0;
		}
		if (callChangeListener(value)) {
			mVolume = value;
			persistString(Integer.toString(value));
			//TODO: notify?
		}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		int volume = 3;
		if (restoreValue) {
			volume = Integer.parseInt(getPersistedString(Integer.toString(mVolume)));
		} else {
			volume = Integer.parseInt((String) defaultValue);
		}
		setVolume(volume);
	}
}
