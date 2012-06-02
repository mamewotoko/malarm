package com.mamewo.malarm24;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class PlaylistViewer
	extends ListActivity
	implements OnItemLongClickListener
{
	private ListView listView_;
	private ArrayAdapter<String> adapter_;
	private M3UPlaylist playlist_;

	//R.array.tune_operation
	static private final int UP_INDEX = 0;
	static private final int DOWN_INDEX = 1;
	static private final int DELETE_INDEX = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.i("malrm", "onCreate in playlistview");
		super.onCreate(savedInstanceState);
		listView_ = getListView();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.i("malrm", "onStart in playlistview");
		Intent i = getIntent();
		String which = i.getStringExtra("playlist");
		int title_id = 0;
		if ("sleep".equals(which)) {
			playlist_ = MalarmPlayerService.sleepPlaylist_;
			title_id = R.string.sleep_playlist_viewer_title;
		}
		else {
			playlist_ = MalarmPlayerService.wakeupPlaylist_;
			title_id = R.string.wakeup_playlist_viewer_title;
		}
		adapter_ = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_.toList());
		setListAdapter(adapter_);
		setTitle(title_id);
		Log.i("malrm", "onStart in playlistview(after getting adapter3)");

		listView_.setLongClickable(true);
		listView_.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.playlist_viewer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.save_playlist:
			try {
				playlist_.save();
				MalarmActivity.showMessage(this, "saved");
			} catch (IOException e) {
				MalarmActivity.showMessage(this, "failed: " + e.getMessage());
			}
			break;
		default:
			break;
		}
		return true;
	}

	//TODO: implement undo?
	//TODO: add selected effect
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter_view,
									View view, int position, long id) {
		final int pos = position;
		final String title = (String)adapter_view.getItemAtPosition(pos);
		new AlertDialog.Builder(PlaylistViewer.this)
		.setTitle(title)
		//TODO: show detail of tune
		.setItems(R.array.tune_operation, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == UP_INDEX) {
					if (pos == 0) {
						//TODO: disable item
						return;
					}
					adapter_.remove(title);
					adapter_.insert(title, pos-1);

					playlist_.remove(pos);
					playlist_.insert(pos-1, title);
				}
				else if (which == DOWN_INDEX) {
					if (pos == adapter_.getCount()-1) {
						//TODO: disable item
						return;
					}
					adapter_.remove(title);
					adapter_.insert(title, pos + 1);

					playlist_.remove(pos);
					playlist_.insert(pos+1, title);
				}
				else if (which == DELETE_INDEX) {
					adapter_.remove(playlist_.toList().get(pos));
					playlist_.remove(pos);
				}
			}
		})
		.setNegativeButton(R.string.cancel, null)
		.create()
		.show();
		return true;
	}
}
