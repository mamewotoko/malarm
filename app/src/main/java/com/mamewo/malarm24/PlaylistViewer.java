package com.mamewo.malarm24;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mamewo.malarm24.MalarmPlayerService.PlayerStateListener;

import java.io.IOException;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

public final class PlaylistViewer
        extends AppCompatActivity
        implements OnItemLongClickListener,
        OnItemClickListener,
        OnClickListener,
        ServiceConnection,
        PlayerStateListener {
    private ListView listView_;
    //private ArrayAdapter<String> adapter_;
    private MusicAdapter adapter_;
    private M3UPlaylist playlist_;
    private MalarmPlayerService player_;
    private ToggleButton playButton_;

    //R.array.tune_operation
    static private final int UP_INDEX = 0;
    static private final int DOWN_INDEX = 1;
    static private final int DELETE_INDEX = 2;
    static final
    private String TAG = "malarm";
    private String playlistName_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_viewer);
        listView_ = (ListView) findViewById(R.id.play_list_view);
        listView_.setLongClickable(true);
        listView_.setOnItemLongClickListener(this);
        listView_.setOnItemClickListener(this);
        playButton_ = (ToggleButton) findViewById(R.id.play_button);
        playButton_.setOnClickListener(this);
        playButton_.setEnabled(false);
        player_ = null;
        Intent i = getIntent();
        playlistName_ = i.getStringExtra("playlist");
        Intent intent = new Intent(this, MalarmPlayerService.class);
        startService(intent);
        //TODO: handle failure of bindService
        boolean result = bindService(intent, this, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService: " + result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_playlist:
                try {
                    playlist_.save();
                    MalarmActivity.showMessage(this, getString(R.string.saved));
                } catch (IOException e) {
                    MalarmActivity.showMessage(this, getString(R.string.failed) + ": " + e.getMessage());
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
        final String title = (String) adapter_view.getItemAtPosition(pos);
        //TODO: use showDialog
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
                            adapter_.insert(title, pos - 1);

                            playlist_.remove(pos);
                            playlist_.insert(pos - 1, title);
                        }
                        else if (which == DOWN_INDEX) {
                            if (pos == adapter_.getCount() - 1) {
                                //TODO: disable item
                                return;
                            }
                            adapter_.remove(title);
                            adapter_.insert(title, pos + 1);

                            playlist_.remove(pos);
                            playlist_.insert(pos + 1, title);
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

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
        if (null == player_) {
            return;
        }
        Playlist currentList = player_.getCurrentPlaylist();
        if (player_.isPlaying()
                && currentList == playlist_
                && currentList.getCurrentPosition() == pos) {
            player_.pauseMusic();
        }
        else {
            player_.playMusic(playlist_, pos, true);
        }
        updateUI();
    }

    final private class MusicAdapter
            extends ArrayAdapter<String> {
        public MusicAdapter(Context context) {
            super(context, R.layout.playlist_item);
        }

        public MusicAdapter(Context context, List<String> list) {
            super(context, R.layout.playlist_item, list);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (null == convertView) {
                view = View.inflate(PlaylistViewer.this, R.layout.playlist_item, null);
            }
            else {
                view = convertView;
            }
            String title = getItem(position);
            TextView titleView = (TextView) view.findViewById(R.id.title_view);
            titleView.setText(title);
            ImageView playIcon = (ImageView) view.findViewById(R.id.play_icon);
            Playlist currentList = player_.getCurrentPlaylist();
            if (currentList == playlist_ && currentList.getCurrentPosition() == position) {
                if (player_.isPlaying()) {
                    playIcon.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    playIcon.setImageResource(android.R.drawable.ic_media_pause);
                }
                playIcon.setVisibility(View.VISIBLE);
            } else {
                playIcon.setVisibility(View.GONE);
            }
            return view;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Log.d(TAG, "onServiceConnected");
        player_ = ((MalarmPlayerService.LocalBinder) binder).getService();
        player_.addPlayerStateListener(this);
        playButton_.setEnabled(true);
        int titleID;
        String playlistPath;
        if ("sleep".equals(playlistName_)) {
            if (null == MalarmPlayerService.sleepPlaylist_) {
                player_.loadPlaylist();
            }
            playlist_ = MalarmPlayerService.sleepPlaylist_;
            titleID = R.string.sleep_playlist_viewer_title;
        }
        else {
            if (null == MalarmPlayerService.wakeupPlaylist_) {
                player_.loadPlaylist();
            }
            playlist_ = MalarmPlayerService.wakeupPlaylist_;
            titleID = R.string.wakeup_playlist_viewer_title;
        }
        adapter_ = new MusicAdapter(this, playlist_.toList());
        listView_.setAdapter(adapter_);
        //setTitle(titleID);

        ActionBar toolbar = getSupportActionBar();
        //toolbar.setHomeAsUpIndicator(R.drawale.ic_arrow_left);
        toolbar.setTitle(titleID);
        toolbar.setSubtitle(playlist_.getPlaylistFile().getAbsolutePath());
        updateUI();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        player_.removePlayerStateListener(this);
        player_ = null;
    }

    private void updateUI() {
        if (null != player_) {
            playButton_.setChecked(player_.isPlaying());
        }
        adapter_.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view == playButton_) {
            if (null == player_) {
                return;
            }
            if (player_.isPlaying()) {
                player_.pauseMusic();
            }
            else {
                player_.setCurrentPlaylist(playlist_);
                player_.playMusic();
            }
        }
    }

    @Override
    public void onStartMusic(String title) {
        updateUI();
    }

    @Override
    public void onStopMusic() {
        updateUI();
    }
}
