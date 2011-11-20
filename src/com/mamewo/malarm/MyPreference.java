package com.mamewo.malarm;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import com.mamewo.malarm.R;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.ImageView;

public class MyPreference extends PreferenceActivity implements OnPreferenceClickListener {

	Preference _help;
	Preference _version;
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean result = false;
		Log.i("malarm", "onPreferenceClick is called");
		if (preference == _help) {
			Uri url = Uri.parse(getString(R.string.help_url));
			startActivity(new Intent(Intent.ACTION_VIEW, url));
			result = true;
		} else if (preference == _version) {
			Log.i("malarm", "onPreferenceClick: version");
			//show dialog
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog);
			ImageView image = (ImageView) dialog.findViewById(R.id.dialog_image_view);
			image.setImageResource(R.drawable.git_download);
			dialog.setTitle(R.string.dialog_title);
			dialog.show();
			result = true;
		}
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: check playpath existence and show message
		addPreferencesFromResource(R.xml.preference);
		_version = findPreference("malarm_version");
		_version.setSummary(MalarmActivity.VERSION);
		_version.setOnPreferenceClickListener(this);
		_help = findPreference("help");
		_help.setOnPreferenceClickListener(this);
	}
}
