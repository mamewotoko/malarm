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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

public class MalarmPreference extends PreferenceActivity implements OnPreferenceClickListener, View.OnClickListener, FileFilter {

	private Preference _help;
	private Preference _version;
	private Preference _create_playlist;
	private CheckBoxPreference _sleep_playlist;
	private CheckBoxPreference _wakeup_playlist;
	private static final String TAG = "malarm";
	
	@Override
	public boolean accept(File pathname) {
		final String filename = pathname.getName();
		//TODO: other formats?
		return filename.endsWith(".mp3") || filename.endsWith(".m4a");
	}

	private void createDefaultPlaylist(File file) {
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(file);
			//find music files
			for (File music_file : file.getParentFile().listFiles(this)) {
				fw.append(music_file.getName() + "\n");
			}
		} catch (IOException e) {
			Log.i(TAG, "cannot write: " + e.getMessage());
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					Log.i(TAG, "cannot close: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean result = false;
		if (preference == _help) {
			final Uri url = Uri.parse(getString(R.string.help_url));
			startActivity(new Intent(Intent.ACTION_VIEW, url));
			result = true;
		} else if (preference == _version) {
			Log.i(TAG, "onPreferenceClick: version");
			//show dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog);
			final ImageView image = (ImageView) dialog.findViewById(R.id.dialog_image_view);
			image.setImageResource(R.drawable.git_download);
			final TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
			text.setText(getString(R.string.git_url));
			text.setOnClickListener(this);
			dialog.setTitle(R.string.dialog_title);
			dialog.show();
			result = true;
		} else if (preference == _create_playlist) {
			final String [] playlists = { MalarmActivity.WAKEUP_PLAYLIST_FILENAME, MalarmActivity.SLEEP_PLAYLIST_FILENAME };
			Log.i(TAG, "playlist pref is clicked");
			for (String filename : playlists) {
				final File file = new File(MalarmActivity.pref_playlist_path, filename);
				if (file.exists()) {
					//show confirm dialog?
					Log.i(TAG, "playlist file exists: " + filename);
					MessageFormat mf = new MessageFormat(getString(R.string.file_exists_format));
					MalarmActivity.showMessage(this, mf.format(new Object[]{ filename }));
					continue;
				}
				createDefaultPlaylist(file);
				//TODO: localize
				MessageFormat mf = new MessageFormat(getString(R.string.playlist_created_format));
				MalarmActivity.showMessage(this, mf.format(new Object[] { filename }));
			}
			result = true;
		} else if (preference == _sleep_playlist || preference == _wakeup_playlist) {
			Log.i(TAG, "View Sleep Playlist from check");
			//TODO: move check code to Playlist class
			String pref_key;
			String playlist_filename;

			if (preference == _sleep_playlist) {
				pref_key = "sleep";
				playlist_filename = MalarmActivity.SLEEP_PLAYLIST_FILENAME;
			} else {
				pref_key = "wakeup";
				playlist_filename = MalarmActivity.WAKEUP_PLAYLIST_FILENAME;
			}
			final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			//get playlist path
			final String path = pref.getString("playlist_path", MalarmActivity.DEFAULT_PLAYLIST_PATH.getAbsolutePath());

			//TODO: add dependency from playlist path
			if (! (new File(path, playlist_filename)).exists()) {
				//TODO: localize
				MalarmActivity.showMessage(this, getString(R.string.pref_playlist_does_not_exist));
				((CheckBoxPreference) preference).setChecked(false);
			} else {
				final Intent i = new Intent(this, PlaylistViewer.class);
				//TODO: define key as constant
				i.putExtra("playlist", pref_key);
				startActivity(i);
			}
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
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(MalarmActivity.PACKAGE_NAME, 0);
			_version.setSummary(pi.versionName);
		} catch (NameNotFoundException e) {
			_version.setSummary("unknown");
		}
		_version.setOnPreferenceClickListener(this);
		_help = findPreference("help");
		_help.setOnPreferenceClickListener(this);
		_create_playlist = findPreference("create_playlist");
		_create_playlist.setOnPreferenceClickListener(this);
		_sleep_playlist = (CheckBoxPreference) findPreference("sleep_playlist");
		_sleep_playlist.setOnPreferenceClickListener(this);
		_wakeup_playlist = (CheckBoxPreference) findPreference("wakeup_playlist");
		_wakeup_playlist.setOnPreferenceClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		//get playlist path
		final String path = pref.getString("playlist_path", MalarmActivity.DEFAULT_PLAYLIST_PATH.getAbsolutePath());

		//TODO: move check code to Playlist class
		final File sleep_file = new File(path, MalarmActivity.SLEEP_PLAYLIST_FILENAME);
		_sleep_playlist.setChecked(sleep_file.exists());

		//TODO: move check code to Playlist class
		final File wakeup_file = new File(path, MalarmActivity.WAKEUP_PLAYLIST_FILENAME);
		_wakeup_playlist.setChecked(wakeup_file.exists());
	}
	
	@Override
	public void onClick(View v) {
		final Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_url)));
		startActivity(i);
	}
}
