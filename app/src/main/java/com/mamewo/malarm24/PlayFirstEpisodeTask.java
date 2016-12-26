package com.mamewo.malarm24;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.mamewo.lib.podcast_parser.BaseGetPodcastTask;
import com.mamewo.lib.podcast_parser.EpisodeInfo;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class PlayFirstEpisodeTask
        extends BaseGetPodcastTask {
    static final
    private int TIMEOUT_SEC = 30;
    EpisodeInfo info_;
    MediaPlayer player_;
    static final
    private String TAG = "malarm";

    public PlayFirstEpisodeTask(Context context, OkHttpClient client, MediaPlayer player) {
        super(context, client, 1);
        player_ = player;
        info_ = null;
    }

    @Override
    protected void onProgressUpdate(EpisodeInfo... values) {
        if (null != values) {
            info_ = values[0];
            Log.d(TAG, "onProgress: url " + info_.getURL());
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (null == info_) {
            Log.d(TAG, "no episode found...");
            return;
        }
        try {
            Log.d(TAG, "Start playing podcast:" + info_.getURL());
            player_.reset();
            player_.setDataSource(info_.getURL());
            Log.d(TAG, "prepareAsync");
            player_.prepareAsync();
        } catch (IOException e) {
            //
            Log.d(TAG, "error set data source: ", e);
        }
    }

//	@Override
//	protected void onCancelled() {
//		onFinished();
//	}
}
