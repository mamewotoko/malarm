package com.mamewo.malarm;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import com.mamewo.malarm.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;

public class MyPreference extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final PreferenceActivity activity = this;
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		Preference version_pref = (Preference) findPreference("malarm_version");
		version_pref.setSummary(MalarmActivity.VERSION);
		Preference help = findPreference("help");
		help.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Uri url = Uri.parse(getString(R.string.help_url));
				activity.startActivity(new Intent(Intent.ACTION_VIEW, url));
				return true;
			}
		});
	}
}
