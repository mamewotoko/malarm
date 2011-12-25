package com.mamewo.malarm;

import java.text.MessageFormat;

import android.content.Context;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class VolumePreference extends DialogPreference {
	private int mMaxVolume;
	private int mVolume = 0;
	private EditText mEditText;
	private TextView mDialogText;
	
	//TODO: add +/- button
	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setDialogLayoutResource(R.layout.volume_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mEditText = (EditText) view.findViewById(R.id.volume_pref_value);
		mDialogText = (TextView) view.findViewById(R.id.volume_pref_text);
		mEditText.setText(Integer.toString(mVolume));
		//TODO: add API to set string 
		final Context context = view.getContext();
		final AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final int current_volume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		final MessageFormat mf = new MessageFormat(context.getString(R.string.volume_format));
		mDialogText.setText(mf.format(new Object[] { Integer.valueOf(mMaxVolume), Integer.valueOf(current_volume)}));
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
			value = mMaxVolume;
		} else if (value < 0) {
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
		int volume;
		if (restoreValue) {
			volume = Integer.parseInt(getPersistedString(Integer.toString(mVolume)));
		} else {
			volume = Integer.parseInt((String) defaultValue);
		}
		setVolume(volume);
	}
}
