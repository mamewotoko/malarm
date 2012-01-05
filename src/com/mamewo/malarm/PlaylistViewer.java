package com.mamewo.malarm;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

//TODO: now do not work
public final class PlaylistViewer extends ListActivity {
	@Override
	public void onStart() {
		super.onStart();
		//TODO: ummm
		MalarmActivity.loadPlaylist();
		final Intent i = getIntent();
		final String which = i.getStringExtra("playlist");
		M3UPlaylist playlist = null;
		int title_id = 0;
		if ("sleep".equals(which)) {
			playlist = MalarmActivity.sleep_playlist;
			title_id = R.string.sleep_playlist_viewer_title;
		} else {
			playlist = MalarmActivity.wakeup_playlist;
			title_id = R.string.wakeup_playlist_viewer_title;
		}
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist.toList()));
		setTitle(title_id);
	}
}
