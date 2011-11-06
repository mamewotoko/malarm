package com.mamewo.malarm;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import com.mamewo.malarm.R;

import android.os.Bundle;
import android.preference.*;
import android.util.Log;

public class MyPreference extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		Preference version_pref = (Preference) findPreference("malarm_version");
		version_pref.setSummary(MalarmActivity.VERSION);
	}
}
