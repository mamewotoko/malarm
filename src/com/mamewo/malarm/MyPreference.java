package com.mamewo.malarm;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;

import com.mamewo.malarm.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

public class MyPreference extends PreferenceActivity implements OnPreferenceClickListener, View.OnClickListener, FileFilter {

	private Preference _help;
	private Preference _version;
	private Preference _create_playlist;
	private static String TAG = "malarm_pref";

	@Override
	public boolean accept(File pathname) {
		String filename = pathname.getName();
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
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
			TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
			text.setText(getString(R.string.git_url));
			text.setOnClickListener(this);
			dialog.setTitle(R.string.dialog_title);
			dialog.show();
			result = true;
		} else if (preference == _create_playlist) {
			String [] playlists = { MalarmActivity.WAKEUP_PLAYLIST_FILENAME, MalarmActivity.SLEEP_PLAYLIST_FILENAME };
			Log.i(TAG, "playlist pref is clicked");
			for (String filename : playlists) {
				File file = new File(MalarmActivity.PLAYLIST_PATH, filename);
				if (file.exists()) {
					//show confirm dialog?
					Log.i(TAG, "playlist file exists: " + filename);
					//TODO: localize
					MalarmActivity.showMessage(this, filename + " already exists");
					continue;
				}
				createDefaultPlaylist(file);
				//TODO: localize
				MalarmActivity.showMessage(this, filename + " created");
			}
			//TODO: refresh preference view
			MalarmActivity.loadPlaylist();
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
		_create_playlist = findPreference("create_playlist");
		_create_playlist.setOnPreferenceClickListener(this);
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		//get playlist path
		String path = pref.getString("playlist_path", MalarmActivity.DEFAULT_PLAYLIST_PATH);

		File wakeup_file = new File(path, MalarmActivity.WAKEUP_PLAYLIST_FILENAME);
		CheckBoxPreference wakeup_playlist = (CheckBoxPreference) findPreference("wakeup_playlist");
		wakeup_playlist.setChecked(wakeup_file.exists());
		File sleep_file = new File(path, MalarmActivity.SLEEP_PLAYLIST_FILENAME);
		CheckBoxPreference sleep_playlist = (CheckBoxPreference) findPreference("sleep_playlist");
		sleep_playlist.setChecked(sleep_file.exists());
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_url)));
		startActivity(i);
	}
}
