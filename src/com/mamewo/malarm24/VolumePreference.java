package com.mamewo.malarm24;

import java.text.MessageFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VolumePreference
	extends DialogPreference
	implements OnClickListener
{
	private int maxVolume_;
	private int volume_ = 0;
	private EditText editText_;
	private TextView dialogText_;
	private Button minusButton_;
	private Button plusButton_;
	
	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		maxVolume_ = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setDialogLayoutResource(R.layout.volume_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		editText_ = (EditText) view.findViewById(R.id.volume_pref_value);
		dialogText_ = (TextView) view.findViewById(R.id.volume_pref_text);
		editText_.setText(Integer.toString(volume_));
		minusButton_ = (Button) view.findViewById(R.id.volume_minus_button);
		minusButton_.setOnClickListener(this);
		plusButton_ = (Button) view.findViewById(R.id.volume_plus_button);
		plusButton_.setOnClickListener(this);
		//TODO: add API to set string 
		final Context context = view.getContext();
		final AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final int current_volume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		final MessageFormat mf = new MessageFormat(context.getString(R.string.volume_format));
		dialogText_.setText(mf.format(new Object[] { Integer.valueOf(maxVolume_), Integer.valueOf(current_volume)}));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			String strvolume = editText_.getText().toString();
			volume_ = Integer.parseInt(strvolume);
			setVolume(volume_);
		}
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}
	
	private void setVolume(int v) {
		int value = v;
		if (value > maxVolume_) {
			value = maxVolume_;
		} else if (value < 0) {
			value = 0;
		}
		if (callChangeListener(value)) {
			volume_ = value;
			persistString(Integer.toString(value));
		}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		int volume;
		
		if (restoreValue) {
			volume = Integer.parseInt(getPersistedString(Integer.toString(volume_)));
		}
		else {
			volume = Integer.parseInt((String) defaultValue);
		}
		setVolume(volume);
	}

	@Override
	public void onClick(View view) {
		int volume_value = 0;
		try {
			volume_value = Integer.valueOf(editText_.getText().toString());
		}
		catch (NumberFormatException e) {
			//do nothing
		}
		if (view == minusButton_) {
			volume_value--;
		}
		else if (view == plusButton_) {
			volume_value++;
		}
		if (volume_value < 0) {
			volume_value = 0;
		}
		else if (volume_value > maxVolume_) {
			volume_value = maxVolume_;
		}
		editText_.setText(Integer.toString(volume_value));
	}
}
