package com.mamewo.malarm24;

import java.io.File;
import java.io.IOException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 * http://www002.upp.so-net.ne.jp/mamewo/
 */
public class MalarmPlayerService
	extends Service
{
	final static
	public String PACKAGE_NAME = MalarmActivity.class.getPackage().getName();
	final static
	public String WAKEUP_ACTION = PACKAGE_NAME + ".WAKEUP_ACTION";
	final static
	public String WAKEUPAPP_ACTION = PACKAGE_NAME + ".WAKEUPAPP_ACTION";

	final static
	private String TAG = "malarm";
	final static
	private long VIBRATE_PATTERN[] = { 10, 1500, 500, 1500, 500, 1500, 500, 1500, 500 };
	private final IBinder binder_ = new LocalBinder();
	private Playlist currentPlaylist_;
	private MediaPlayer player_;

	public MalarmPlayerService(){
		super();
		currentPlaylist_ = null;
		player_ = new MediaPlayer();
		MusicCompletionListener l = new MusicCompletionListener();
		player_.setOnCompletionListener(l);
		player_.setOnErrorListener(l);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		String action = intent.getAction();
		if(WAKEUPAPP_ACTION.equals(action)){
			Log.i(TAG, "onStartCommand: wakeup!: " + MalarmActivity.wakeupPlaylist);
			MalarmActivity.loadPlaylist();
			final SharedPreferences pref = 
					PreferenceManager.getDefaultSharedPreferences(this);
			int volume = Integer.valueOf(pref.getString("wakeup_volume", "5"));
			AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			mgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
			stopMusic();
			playMusic(MalarmActivity.wakeupPlaylist);
			boolean vibrate = 
					pref.getBoolean("vibrate", MalarmPreference.DEFAULT_VIBRATION);
			if(vibrate){
				startVibrator();
			}
		}
		//TODO: add sleep action and stop music
		return START_STICKY;
	}
	
	public boolean isPlaying() {
		return player_ != null && player_.isPlaying();
	}

	public void setPlaylist(Playlist list) {
		currentPlaylist_ = list;
	}

	//not used..
	public void playMusicNativePlayer(Context context, File f) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(f), "audio/*");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	public void playNext() {
		Log.i(TAG, "playNext is called: ");
		if (isPlaying()) {
			stopMusic();
		}
		playMusic(currentPlaylist_);
	}

	public class MusicCompletionListener
	implements MediaPlayer.OnCompletionListener,
				MediaPlayer.OnErrorListener
	{
		public void onCompletion(MediaPlayer mp) {
			Log.i(TAG, "onCompletion listener is called");
			playNext();
		}

		// This method is not called when DRM error occurs
		public boolean onError(MediaPlayer mp, int what, int extra) {
			//TODO: show error message to GUI
			Log.i(TAG, "onError is called, cannot play this media");
			playNext();
			return true;
		}
	}

	public boolean playMusic(Playlist playlist) {
		currentPlaylist_ = playlist;
		return playMusic();
	}

	public boolean playMusic() {
		Log.i(TAG, "playMusic");
		if (currentPlaylist_ == null || currentPlaylist_.isEmpty()) {
			Log.i(TAG, "playMusic: playlist is null");
			return false;
		}
		if (player_.isPlaying()) {
			return false;
		}
		String path = "";
		//skip unsupported files filtering by filename ...
		for (int i = 0; i < 10; i++) {
			path = currentPlaylist_.next();
			File f = new File(path);
			// ... m4p is protected audio file
			if ((!path.endsWith(".m4p")) && f.exists()) {
				break;
			}
		}
		try {
			player_.reset();
			player_.setDataSource(path);
			player_.prepare();
			player_.start();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

//	private void setNotification(String title, String text) {
//		final Notification note =
//				new Notification(R.drawable.icon, title, System.currentTimeMillis());
//		
//		final Intent ni = new Intent(this, MalarmActivity.class);
//		final PendingIntent npi = PendingIntent.getActivity(this, 0, ni, 0);
//		note.setLatestEventInfo(this, title, text, npi);
//		final NotificationManager notify_mgr =
//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		notify_mgr.notify(TAG, 0, note);
//	}

//	private void clearNotification(){
//		final NotificationManager notify_mgr =
//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		notify_mgr.cancel(TAG, 0);
//	}
	
	public void stopMusic() {
		player_.stop();
	}

	public void pauseMusic() {
		Log.i(TAG, "pause music is called");
		try {
			player_.pause();
		}
		catch (Exception e) {
			//do nothing
		}
		//clearNotification();
	}
	
	public void quit(){
		stopSelf();
	}

	public void startVibrator() {
		final Vibrator vibrator = 
				(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrator == null) {
			return;
		}
		vibrator.vibrate(VIBRATE_PATTERN, 1);
	}
	
	public void stopVibrator() {
		final Vibrator vibrator =
				(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrator == null) {
			return;
		}
		vibrator.cancel();
	}
	
	public class LocalBinder
		extends Binder
	{
		public MalarmPlayerService getService() {
			return MalarmPlayerService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder_;
	}

	final static
	public class Receiver
		extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(null == action){
				return;
			}
			if(WAKEUP_ACTION.equals(action)){
				Log.i(TAG, "onReceive is called(malarm24): action: " + action);
				Intent i = new Intent(context, MalarmPlayerService.class);
				i.setAction(WAKEUPAPP_ACTION);
				context.startService(i);
			}
		}
	}
}
