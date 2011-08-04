package com.mamewo.hello;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HelloActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public static final String WAKEUP_ACTION = "com.mamewo.hello.WAKEUP_ACTION";
	// 1 hour
	public static long SLEEP_TIME = 60 * 60 * 1000;

	private Button _next_button;
	private Button _sleep_wakeup_button;
	private static final int REQUEST_ENABLE_BT = 10;
	private BluetoothAdapter _adapter;
	private TimePicker _time_picker;
	private TextView _time_label;
	
	// private Player _player;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_time_picker = (TimePicker) findViewById(R.id.timePicker1);
		_time_picker.setIs24HourView(true);
		_sleep_wakeup_button = (Button) findViewById(R.id.play_button);
		_sleep_wakeup_button.setOnClickListener(this);
		_next_button = (Button) findViewById(R.id.next_button);
		_next_button.setOnClickListener(this);
		_time_label = (TextView) findViewById(R.id.target_time_label);
		// _player = new Player();
	}

	// TODO: add time parameter
	public void scheduleToPlaylist() {
		Log.i("Hello", "scheduleToPlaylist is called");
		//TODO: hide keyboard?
		//TODO: use different button?
		if (Player.isPlaying()) {
			Player.stopMusic();
			showMessage(this, "Stop Music");
			return;
		}
		Player.startSleepMusic();
		//set timer
		Calendar now = new GregorianCalendar();
		int target_hour = _time_picker.getCurrentHour().intValue();
		int target_min = _time_picker.getCurrentMinute().intValue();
		Calendar target = new GregorianCalendar(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH), now.get(Calendar.DATE), target_hour, target_min, 0);
		long target_millis = target.getTimeInMillis();
		String tommorow ="";
		if (target_millis <= System.currentTimeMillis()) {
			//tomorrow
			target_millis =  + 24 * 60 * 60 * 1000;
			tommorow = " (tomorrow)";
		}
		_time_label.setText(String.format("%02d:%02d", target_hour, target_min));
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this, Player.class);
		i.setAction(WAKEUP_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		mgr.set(AlarmManager.RTC_WAKEUP, target_millis, pendingIntent);
		showMessage(this, "Alarm set!" + tommorow);
	}

	void displayBluetoothDevices() {
		Set<BluetoothDevice> devices = _adapter.getBondedDevices();
		String message = "";
		for (BluetoothDevice dev : devices) {
			message += dev.getName() + ": " + dev.getAddress() + "\n";
		}
		showMessage(this.getBaseContext(), message);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("DEBUG", "onActivityResult: " + requestCode + ": " + resultCode);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode != RESULT_OK) {
				return;
			}
			displayBluetoothDevices();
		}
	}

	public void onClick(View v) {
		if (v == _next_button) {
			Player.playNext();
		} else if (v == _sleep_wakeup_button) {
			scheduleToPlaylist();
		} else {
			showMessage(v.getContext(), "Unknown button!");
		}
	}

	private static void showMessage(Context c, String message) {
		Toast.makeText(c, message, Toast.LENGTH_LONG).show();
	}

	public static class Player extends BroadcastReceiver {
		private static final String MUSIC_PATH = "/sdcard/music/";
		private static MediaPlayer _player = null;
		private static int _index = 0;

		//put music into music folder of SD card
		protected static final String[] WAKEUP_PLAYLIST = {
				"01 Seasons Of Love.m4a", "08 ブルーモーメント.m4a", "01 M☆gic.m4a",
				"2-08 103.m4a" };

		private static final String[] SLEEP_PLAYLIST = {
				"03 帰れない二人.m4a",
				"07 竹田の子守唄.m4a" };

		private static String[] current_playlist = SLEEP_PLAYLIST;

		public static boolean isPlaying() {
			return _player != null && _player.isPlaying();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// AppWidgetManager mgr = AppWidgetManager.getInstance(context);
			Log.i("Hello", "action: " + intent.getAction());
			if (intent.getAction().equals(WAKEUP_ACTION)) {
				if (Player.isPlaying()) {
					stopMusic();
				}
				Log.i("Hello", "Wakeup action");
				startMusic(WAKEUP_PLAYLIST);
			}
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
				_sleeptime = sleeptime;
			}

			public void run() {
				try {
					Thread.sleep(_sleeptime);
					Player.stopMusic();
					// TODO: sleep device?
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public static void startSleepMusic() {
			Log.i("Hello", "start sleep music and stop");
			long playtime_millis = System.currentTimeMillis() + 60 * 60 * 1000;
			// TODO: use Alarm instead of Thread
			SleepThread t = new SleepThread(playtime_millis);
			t.start();
			startMusic(SLEEP_PLAYLIST);
		}

		public static void playNext() {
			_index++;
			Log.i("Hello", "playNext is called: " + _index);
			if (Player.isPlaying()) {
				stopMusic();
			}
			startMusic(current_playlist);
		}

		public static class MusicCompletionListener implements
				MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
			public void onCompletion(MediaPlayer mp) {
				// TODO: manage index....(sleep / wakeup)
				Log.i("Hello", "onCompletion listener is called");
				Player.playNext();
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
			// TODO ... check....
			try {
				_player.reset();
				_player.setDataSource(path);
				_player.prepare();
				_player.start();
			} catch (IOException e) {

			}
		}
	}
	//obsolete code...
	public void discoverBluetoothDevices(View v) {
		_adapter = BluetoothAdapter.getDefaultAdapter();
		if (_adapter == null) {
			showMessage(v.getContext(), "no bluetooth");
			return;
		}
		showMessage(v.getContext(), "start bluetooth");
		if (!_adapter.isEnabled()) {
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(i, REQUEST_ENABLE_BT);
		}
		showMessage(v.getContext(), "request bluetooth");
		displayBluetoothDevices();
	}
}
