package com.mamewo.malarm;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

//TODO: now do not work
public final class PlaylistViewer extends ListActivity implements OnClickListener {
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private Button mUpButton;
	private Button mDownButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mListView = getListView();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//TODO: ummm
		MalarmActivity.loadPlaylist();
		final Intent i = getIntent();
		final String which = i.getStringExtra("playlist");
		final M3UPlaylist playlist;
		int title_id = 0;
		if ("sleep".equals(which)) {
			playlist = MalarmActivity.sleep_playlist;
			title_id = R.string.sleep_playlist_viewer_title;
		} else {
			playlist = MalarmActivity.wakeup_playlist;
			title_id = R.string.wakeup_playlist_viewer_title;
		}
		mAdapter = new ArrayAdapter<String>(this, R.layout.playlist_item, R.id.playlist_title, playlist.toList());
		setListAdapter(mAdapter);
		setTitle(title_id);
		mUpButton = (Button) findViewById(R.id.playlist_item_up);
		mUpButton.setOnClickListener(this);
		mDownButton = (Button) findViewById(R.id.playlist_item_down);
		mDownButton.setOnClickListener(this);
		mListView.setLongClickable(true);
		//TODO: implement undo?
		//TODO: add selected effect
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter_view, View view, int position, long id) {
				final int pos = position;
				final String title = (String)adapter_view.getItemAtPosition(pos);
				//TODO: show remove dialog (add dialog)
				new AlertDialog.Builder(PlaylistViewer.this)
				//TODO: localize
				//TODO: show title
				.setTitle("remove this tune?\n" + title)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//TODO: check pos?
						mAdapter.remove(playlist.toList().get(pos));
					}
				})
				.setNegativeButton(R.string.no, null)
				.create()
				.show();
				return false;
			}
		});
	}

	@Override
	public void onClick(View view) {
		//TODO: implement
		if (view == mUpButton) {
			MalarmActivity.showMessage(this, "up");
		} else {
			MalarmActivity.showMessage(this, "down");
		}
	}
}
