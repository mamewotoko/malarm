package com.mamewo.malarm24;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;

public class MalarmPreference
	extends PreferenceActivity
	implements OnPreferenceClickListener,
		View.OnClickListener,
		FileFilter,
		OnSharedPreferenceChangeListener
{
	private Preference help_;
	private Preference version_;
	private Preference createPlaylist_;
	private Preference sleepTime_;
	private Preference wakeupTime_;
	private Preference sleepVolume_;
	private Preference wakeupVolume_;
	private Preference clearWebviewCache_;
	private CheckBoxPreference sleepPlaylist_;
	private CheckBoxPreference wakeupPlaylist_;
	private static final String TAG = "malarm";

	//move to property file?
	//TODO: use XML file to set default value
	public static final boolean DEFAULT_VIBRATION = true;
	public static final String DEFAULT_SLEEPTIME = "60";
	public static final String DEFAULT_WAKEUP_TIME = "7:00";
	public static final String DEFAULT_SLEEP_VOLUME = "3";
	public static final String DEFAULT_WAKEUP_VOLUME = "7";
	//TODO: manage default value in one file
	public static final String DEFAULT_WEB_LIST = 
		"http://bijo-linux.com/!http://twitter.com/!http://www.bijint.com/jp/!http://www.google.com/mail/"
		+ "!https://www.google.com/calendar/!http://www.okuiaki.com/mobile/login.php";
	//e.g. /sdcard/music
	public static final File DEFAULT_PLAYLIST_PATH =
			new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MUSIC);

	private SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public boolean accept(File pathname) {
		final String filename = pathname.getName();
		
		//TODO: other formats? mp4, m4v...
		return filename.endsWith(".mp3")
				|| filename.endsWith(".m4a")
				|| filename.endsWith(".ogg");
	}

	private void createDefaultPlaylist(File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			//find music files
			for (File music_file : file.getParentFile().listFiles(this)) {
				fw.append(music_file.getName() + "\n");
			}
		}
		catch (IOException e) {
			Log.i(TAG, "cannot write: " + e.getMessage());
		}
		finally {
			if (fw != null) {
				try {
					fw.close();
				}
				catch (IOException e) {
					Log.i(TAG, "cannot close: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean result = false;
		if (preference == help_) {
			final Uri url = Uri.parse(getString(R.string.help_url));
			startActivity(new Intent(Intent.ACTION_VIEW, url));
			result = true;
		}
		else if (preference == version_) {
			Log.i(TAG, "onPreferenceClick: version");
			//show dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog);
			final View logo = dialog.findViewById(R.id.dialog_logo);
			logo.setOnClickListener(this);
			dialog.setTitle(R.string.dialog_title);
			dialog.show();
			result = true;
		}
		else if (preference == createPlaylist_) {
			final String [] playlists = 
				{ MalarmActivity.WAKEUP_PLAYLIST_FILENAME,
					MalarmActivity.SLEEP_PLAYLIST_FILENAME };
			Log.i(TAG, "playlist pref is clicked");
			for (String filename : playlists) {
				final File file = new File(MalarmActivity.prefPlaylistPath, filename);
				if (file.exists()) {
					//show confirm dialog?
					Log.i(TAG, "playlist file exists: " + filename);
					String msg = MessageFormat.format(getString(R.string.file_exists_format), filename);
					MalarmActivity.showMessage(this, msg);
					continue;
				}
				createDefaultPlaylist(file);
				//TODO: localize
				String msg = MessageFormat.format(getString(R.string.playlist_created_format), filename);
				MalarmActivity.showMessage(this, msg);
			}
			result = true;
		}
		else if (preference == sleepPlaylist_ || preference == wakeupPlaylist_) {
			Log.i(TAG, "View Sleep Playlist from check");
			//TODO: move check code to Playlist class
			String pref_key;
			String playlist_filename;

			if (preference == sleepPlaylist_) {
				pref_key = "sleep";
				playlist_filename = MalarmActivity.SLEEP_PLAYLIST_FILENAME;
			}
			else {
				pref_key = "wakeup";
				playlist_filename = MalarmActivity.WAKEUP_PLAYLIST_FILENAME;
			}
			final SharedPreferences pref = getPrefs();
			//get playlist path
			final String path = 
					pref.getString("playlist_path", DEFAULT_PLAYLIST_PATH.getAbsolutePath());

			//TODO: add dependency from playlist path
			if (! (new File(path, playlist_filename)).exists()) {
				//TODO: localize
				MalarmActivity.showMessage(this, getString(R.string.pref_playlist_does_not_exist));
				((CheckBoxPreference) preference).setChecked(false);
			}
			else {
				final Intent i = new Intent(this, PlaylistViewer.class);
				//TODO: define key as constant
				i.putExtra("playlist", pref_key);
				startActivity(i);
			}
			result = true;
		}
		else if (preference == clearWebviewCache_) {
			//TODO: use more clear way
			String prefKey = preference.getKey();
			final SharedPreferences pref = preference.getSharedPreferences();
			Log.i(TAG, "clear pref is clicked: " + prefKey + " " + " hasKey " + pref.contains(prefKey));
			final SharedPreferences.Editor editor = preference.getEditor();
			editor.putBoolean(prefKey, !pref.getBoolean(prefKey, false));
			//compatibility: apply is not available in 7
			editor.apply();
			result = true;
		}
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: check playpath existence and show message
		addPreferencesFromResource(R.xml.preference);
		version_ = findPreference("malarm_version");
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(MalarmActivity.PACKAGE_NAME, 0);
			version_.setSummary(pi.versionName);
		}
		catch (NameNotFoundException e) {
			version_.setSummary("unknown");
		}
		version_.setOnPreferenceClickListener(this);
		help_ = findPreference("help");
		help_.setOnPreferenceClickListener(this);
		createPlaylist_ = findPreference("create_playlist");
		createPlaylist_.setOnPreferenceClickListener(this);
		sleepTime_ = findPreference("sleeptime");
		wakeupTime_ = findPreference("default_time");
		wakeupVolume_ = findPreference("wakeup_volume");
		sleepVolume_ = findPreference("sleep_volume");
		sleepPlaylist_ = (CheckBoxPreference) findPreference("sleep_playlist");
		sleepPlaylist_.setOnPreferenceClickListener(this);
		wakeupPlaylist_ = (CheckBoxPreference) findPreference("wakeup_playlist");
		wakeupPlaylist_.setOnPreferenceClickListener(this);
		
		clearWebviewCache_ = findPreference("clear_webview_cache");
		clearWebviewCache_.setOnPreferenceClickListener(this);

		final SharedPreferences pref = getPrefs();
		pref.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy() {
		final SharedPreferences pref = getPrefs();
		pref.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		final SharedPreferences pref = getPrefs();
		//get playlist path
		final String path = 
				pref.getString("playlist_path", DEFAULT_PLAYLIST_PATH.getAbsolutePath());

		//TODO: move check code to Playlist class
		final File sleepFile = new File(path, MalarmActivity.SLEEP_PLAYLIST_FILENAME);
		sleepPlaylist_.setChecked(sleepFile.exists());

		//TODO: move check code to Playlist class
		final File wakeupFile = new File(path, MalarmActivity.WAKEUP_PLAYLIST_FILENAME);
		wakeupPlaylist_.setChecked(wakeupFile.exists());
	}
	
	@Override
	public void onClick(View v) {
		final Intent i = 
				new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_url)));
		startActivity(i);
	}

	@Override
	public void onResume() {
		super.onResume();
		final SharedPreferences pref = getPrefs();
		updateSummary(pref, "ALL");
	}

	private void setVolumeSummary(SharedPreferences pref, String key, Preference prefUI, String defaultVolumeStr) {
		int volume =
				Integer.valueOf(pref.getString(key, defaultVolumeStr));
		final AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		final int maxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < volume; i++){
			sb.append("|");
		}
		for(int i = 0; i < maxVolume - volume; i++){
			sb.append(".");
		}
		prefUI.setSummary(String.format("%s (%s/%d)", sb.toString(), volume, maxVolume));
	}
	
	public void updateSummary(SharedPreferences pref, String key) {
		final boolean update_all = "ALL".equals(key);
		if (update_all || "default_time".equals(key)) {
			final String timestr = pref.getString("default_time", MalarmPreference.DEFAULT_WAKEUP_TIME);
			String[] hourminStr = timestr.split(":");
			String hour = hourminStr[0];
			String min = hourminStr[1];
			if(min.length() == 1) {
				min = "0" + min;
			}
			wakeupTime_.setSummary(hour + ":" + min);
		}
		if (update_all || "sleeptime".equals(key)) {
			final String sleepTime = pref.getString("sleeptime", MalarmPreference.DEFAULT_SLEEPTIME);
			String summary = MessageFormat.format(getString(R.string.unit_min), sleepTime);
			sleepTime_.setSummary(summary);
		}
		if (update_all || "sleep_volume".equals(key)) {
			setVolumeSummary(pref, "sleep_volume", sleepVolume_, DEFAULT_SLEEP_VOLUME);
		}
		if (update_all || "wakeup_volume".equals(key)) {
			setVolumeSummary(pref, "wakeup_volume", wakeupVolume_, DEFAULT_WAKEUP_VOLUME);
		}
		//url_list has no summary
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		updateSummary(pref, key);
	}
}
