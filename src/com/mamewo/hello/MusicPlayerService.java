package com.mamewo.hello;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

//TODO: implement music player as Service to play long time
public class MusicPlayerService extends Service {
	public static final String MUSIC_PATH = "/sdcard/music/";

	private static MediaPlayer _player = null;
	private static int _index = 0;
	private static String[] current_playlist = null;
	private LocalBinder binder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		MusicPlayerService getService() {
			return (MusicPlayerService.this);
		}
	}
	
	public static boolean isPlaying() {
		return _player != null && _player.isPlaying();
	}

	public static void reset() {
		_index = 0;
	}

	public static void stopMusic() {
		if (_player == null) {
			return;
		}
		_player.stop();
		// TODO: delete?
	}

	private static class SleepThread extends Thread {
		long _sleeptime;

		public SleepThread(long sleeptime) {
			Log.i("Hello", "SleepThread is created");
			_sleeptime = sleeptime;
		}

		public void run() {
			Log.i("Hello", "SleepThread run");
			try {
				Thread.sleep(_sleeptime);
				MusicPlayerService.stopMusic();
				// TODO: sleep device?
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void startMusicWithSleep(String[] playlist, long playtime_millis) {
		Log.i("Hello", "start sleep music and stop");
		// TODO: use Alarm instead of Thread
		SleepThread t = new SleepThread(playtime_millis);
		t.start();
		reset();
		startMusic(playlist);
	}

	public static void playNext() {
		_index++;
		Log.i("Hello", "playNext is called: " + _index);
		if (MusicPlayerService.isPlaying()) {
			stopMusic();
		}
		startMusic(current_playlist);
	}

	public static class MusicCompletionListener implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
		public void onCompletion(MediaPlayer mp) {
			// TODO: manage index....(sleep / wakeup)
			Log.i("Hello", "onCompletion listener is called");
			MusicPlayerService.playNext();
		}

		// This method is not called when DRM error is occured
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO: notify error
			Log.i("Hello", "onError is called");
			return false;
		}
	}

	public static void startMusic(String[] playlist) {
		current_playlist = playlist;
		Log.i("Hello", "startMusic");
		if (_player == null) {
			_player = new MediaPlayer();
			MusicCompletionListener l = new MusicCompletionListener();
			_player.setOnCompletionListener(l);
			_player.setOnErrorListener(l);
		}
		if (_player.isPlaying()) {
			return;
		}
		String path = "";
		for (int i = 0; i < playlist.length; i++) {
			path = MUSIC_PATH + playlist[_index];
			File f = new File(path);
			// ....
			if ((!path.endsWith(".m4p")) && f.exists()) {
				break;
			}
			// TODO: use resource music
			_index++;
			if (_index >= playlist.length) {
				_index = 0;
			}
		}
		// TODO: handle error
		try {
			_player.reset();
			_player.setDataSource(path);
			_player.prepare();
			_player.start();
		} catch (IOException e) {

		}
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		_player = new MediaPlayer();
		this.setForeground(false);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		_player = null;
	}
	
	@Override
	public IBinder onBind(Intent i) {
		return binder;
	}
}
