package com.mamewo.hello;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import android.os.Bundle;
import android.preference.*;

public class MyPreference extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
