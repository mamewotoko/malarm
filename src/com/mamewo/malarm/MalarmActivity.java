package com.mamewo.malarm;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.mamewo.malarm.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
//import android.webkit.WebView.WebViewTransport;
import android.webkit.WebViewClient;
import android.widget.*;
import android.webkit.*;
import android.net.Uri;
import android.net.http.*;

public class MalarmActivity extends Activity implements OnClickListener, OnSharedPreferenceChangeListener {
	/** Called when the activity is first created. */
	private static final String PACKAGE_NAME = MalarmActivity.class.getPackage().getName();
	public static final String WAKEUP_ACTION = PACKAGE_NAME + ".WAKEUP_ACTION";
	public static final String WAKEUPAPP_ACTION = PACKAGE_NAME + ".WAKEUPAPP_ACTION";
	public static final String SLEEP_ACTION = PACKAGE_NAME + ".SLEEP_ACTION";
	
	public static class MalarmState implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Calendar _target;
		public MalarmState(Calendar target) {
			_target = target;
		}
	}
	
	private static final Integer DEFAULT_HOUR = new Integer(7);
	private static final Integer DEFAULT_MIN = new Integer(0);
	private static Vibrator _vibrator = null;
	
	private MalarmState _state = null;
	private Button _next_button;
	private Button _sleep_wakeup_button;
	private TimePicker _time_picker;
	private TextView _time_label;
	private WebView _webview;
//	private WebView _subwebview;
	private Button _alarm_button;
	
	@SuppressWarnings("unused")
	private PhoneStateListener _calllistener;
	private static boolean _PREF_USE_NATIVE_PLAYER;
	private static boolean _PREF_VIBRATE;
	
	private static String _NATIVE_PLAYER_KEY = "nativeplayer";
	
	private static final int DOW_INDEX[] = {
		Calendar.SUNDAY, 
		Calendar.MONDAY, 
		Calendar.TUESDAY, 
		Calendar.WEDNESDAY, 
		Calendar.THURSDAY, 
		Calendar.FRIDAY, 
		Calendar.SATURDAY, 
	};
	
	public class MyCallListener extends PhoneStateListener {
		MalarmActivity _activity;
		public MyCallListener(MalarmActivity context) {
			_activity = context;
			TelephonyManager telmgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			telmgr.listen(this, LISTEN_CALL_STATE);
		}

		public void onCallStateChanged (int state, String incomingNumber) {
			//TODO: restart music when phone call is killed?
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				//stop vibration
				if (_vibrator != null) {
					_vibrator.cancel();
				}
				//native player is OK
				Player.stopMusic();
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(PACKAGE_NAME, "onCreate is called");
		super.onCreate(savedInstanceState);
		Log.i(PACKAGE_NAME, PACKAGE_NAME);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		_time_picker = (TimePicker) findViewById(R.id.timePicker1);
		_time_picker.setIs24HourView(true);
		if (savedInstanceState != null) {
			_state = (MalarmState)savedInstanceState.get("state");
		} else {
			_state = null;
		}
		_sleep_wakeup_button = (Button) findViewById(R.id.play_button);
		_sleep_wakeup_button.setOnClickListener(this);
		_next_button = (Button) findViewById(R.id.next_button);
		_next_button.setOnClickListener(this);
		_time_label = (TextView) findViewById(R.id.target_time_label);
		_webview = (WebView)findViewById(R.id.webView1);
//		_subwebview = new WebView(this);
		_alarm_button = (Button)findViewById(R.id.play_button);
		WebSettings config = _webview.getSettings();
		//to display twitter...
		config.setDomStorageEnabled(true);
		config.setJavaScriptEnabled(true);
		config.setSupportZoom(true);
//		config.setSupportMultipleWindows(true);
		_webview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				_webview.requestFocus();
				return false;
			}
		});
		
		final Activity activity = this;
		_webview.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading (WebView view, String url) {
//				Log.i(PACKAGE_NAME, "shouldOverrideUrlLoading: " + url);
//				return false;
//			}
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onLoadResource (WebView view, String url) {
				//Log.i(PACKAGE_NAME, "loading: " + url);
				//addhoc polling...
				int height = view.getContentHeight();
				if ((url.indexOf("bijint") > 0 || url.indexOf("bijo-linux") > 0) && height > 400) {
					//TODO: get precise position....
					if(url.indexOf("binan") > 0 && height > 420) {
						view.scrollTo(0, 420);
					} else if (url.indexOf("bijo-linux") > 0 && height > 100) {
						//TODO: forbid vertical scroll?
						//TODO: open next page in same tab
						int x = view.getLeft();
						view.scrollTo(x, 100);
					} else if (height > 960) {
						view.scrollTo(0, 960);
					}
				}
			}

			@Override
			public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
				Toast.makeText(activity, "SSL error " + error, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(PACKAGE_NAME, "onPageFinshed: " + url);
			}
		});

		//TODO: fix
//		_webview.setWebChromeClient(new WebChromeClient() {
//			@Override
//			public boolean onCreateWindow (WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
//				Log.i(PACKAGE_NAME, "onCreateWindow: " + dialog + " " + userGesture + " " + resultMsg.obj);
//				((WebViewTransport)resultMsg.obj).setWebView(_subwebview);
//				resultMsg.sendToTarget();
//				return true;
//			}
//		});

		//stop alarm when phone call
		_calllistener = new MyCallListener(this);
		if (_vibrator == null) {
			_vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		}
	}
	
	@Override
	protected void onResume() {
		//start tokei
		Log.i(PACKAGE_NAME, "onPause is called, start JavaScript");
		super.onResume();
		//WebView.onResume is hidden, why!?!?
		_webview.getSettings().setJavaScriptEnabled(true);
		loadWebPage(_webview);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		_PREF_USE_NATIVE_PLAYER = pref.getBoolean("use_native_player", false);
		_PREF_VIBRATE = pref.getBoolean("vibrate", false);

		pref.registerOnSharedPreferenceChangeListener(this);
		if (_time_picker.isEnabled()) {
			_time_picker.setCurrentHour(DEFAULT_HOUR);
			_time_picker.setCurrentMinute(DEFAULT_MIN);
		}
	}

	@Override
	protected void onPause() {
		Log.i(PACKAGE_NAME, "onPause is called, stop JavaScript");
		super.onPause();
		//stop tokei
		_webview.getSettings().setJavaScriptEnabled(false);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStart () {
		Log.i(PACKAGE_NAME, "onStart is called");
		super.onStart();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		_PREF_USE_NATIVE_PLAYER = pref.getBoolean("use_native_player", false);
		_PREF_VIBRATE = pref.getBoolean("vibrate", false);
		if (_state != null) {
			updateAlarmUI(_state._target);
		}
		_alarm_button.requestFocus();
	}
	
	//Avoid finishing activity not to lost _state
	@Override
	public void onBackPressed() {
		moveTaskToBack(false);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("malarm", "onSaveInstanceState is called");
		outState.putSerializable("state", _state);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		//TODO: show value when url and sleeptime is changed
		if (key.equals("use_native_player")) {
			_PREF_USE_NATIVE_PLAYER = sharedPreferences.getBoolean(key, false);
		} else if (key.equals("vibrate")) {
			_PREF_VIBRATE = sharedPreferences.getBoolean(key, true);
		}
	}

	private void loadWebPage (WebView view) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String url = pref.getString("url", "http://twitter.com/");
		WebSettings config = _webview.getSettings();
		if (url.indexOf("bijo-linux") > 0) {
			config.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		} else {
			config.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		}
		if (url.indexOf("bijo-linux") > 0) {
			config.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else {
			config.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		}
		showMessage(this, "Loading...");
		_webview.loadUrl(url);
	}
	
	private void clearAlarmUI() {
		_time_picker.setEnabled(true);
		_time_label.setText("");
		_time_picker.setCurrentHour(DEFAULT_HOUR);
		_time_picker.setCurrentMinute(DEFAULT_MIN);
	}

    private void cancelAlarm () {
    	Log.i(PACKAGE_NAME, "cancelAlarm");
    	//TODO: second parameter is correct?
		PendingIntent p = makePlayPintent(WAKEUP_ACTION, true);
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		clearAlarmUI();
		_state = null;
		mgr.cancel(p);
    }

	protected void onNewIntent (Intent intent) {
		String action = intent.getAction();
		if (action != null && action.equals(WAKEUPAPP_ACTION)) {
			if (_PREF_VIBRATE && _vibrator != null) {
				long pattern[] = { 10, 2000, 500, 1500, 1000, 2000 };
				_vibrator.vibrate(pattern, 1);
			}
		}
	}
	
	private PendingIntent makePlayPintent(String action, boolean use_native) {
		Intent i = new Intent(this, Player.class);
		i.setAction(action);
		i.putExtra(_NATIVE_PLAYER_KEY, use_native);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		return pendingIntent;
	}

	//add menu to cancel alarm
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu which can gather user input for switching camera
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, menu);
        return true;
    }

    private void updateAlarmUI (Calendar target) {
    	String dow_str = "";
		int dow_int = target.get(Calendar.DAY_OF_WEEK);
		String[] dow_name_table = getResources().getStringArray(R.array.day_of_week);
		for (int i = 0; i < DOW_INDEX.length; i++) {
			if (DOW_INDEX[i] == dow_int) {
				dow_str = dow_name_table[i];
				break;
			}
		}
		_time_label.setText(String.format("%2d/%2d %02d:%02d (%s)",
										target.get(Calendar.MONTH)+1,
										target.get(Calendar.DATE),
										target.get(Calendar.HOUR_OF_DAY),
										target.get(Calendar.MINUTE),
										dow_str));
		_time_picker.setCurrentHour(target.get(Calendar.HOUR_OF_DAY));
		_time_picker.setCurrentMinute(target.get(Calendar.MINUTE));
		_time_picker.setEnabled(false);
    }
    
	public void setAlarm() {
		Log.i(PACKAGE_NAME, "scheduleToPlaylist is called");
		//TODO: hide keyboard?
		if (_state != null) {
			cancelAlarm();
			if (_vibrator != null) {
				_vibrator.cancel();
			}
			if (! _PREF_USE_NATIVE_PLAYER) {
				Player.stopMusic();
				//TODO: what's happen if now playing alarm sound?
				showMessage(this, getString(R.string.music_stopped));
			}
			return;
		}
		//set timer
		Calendar now = new GregorianCalendar();
		int target_hour = _time_picker.getCurrentHour().intValue();
		int target_min = _time_picker.getCurrentMinute().intValue();
		Calendar target = new GregorianCalendar(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH), now.get(Calendar.DATE), target_hour, target_min, 0);
		long target_millis = target.getTimeInMillis();
		String tommorow ="";
		long now_millis = System.currentTimeMillis();
		if (target_millis <= now_millis) {
			//tomorrow
			target_millis += 24 * 60 * 60 * 1000;
			target.setTimeInMillis(target_millis);
			tommorow = " (" + getString(R.string.tomorrow) + ")";
		}
		_state = new MalarmState(target);
		updateAlarmUI(target);

		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = makePlayPintent(WAKEUP_ACTION, false);
		mgr.set(AlarmManager.RTC_WAKEUP, target_millis, pendingIntent);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int min = Integer.valueOf(pref.getString("sleeptime", "60"));
		Player.playSleepMusic(this, min);
		long sleep_time_millis = min * 60 * 1000;
		//TODO: localize
		String sleeptime_str = String.valueOf(min) + " min";
		if (target_millis - now_millis >= sleep_time_millis) {
			PendingIntent sleepIntent = makePlayPintent(SLEEP_ACTION, _PREF_USE_NATIVE_PLAYER);
			mgr.set(AlarmManager.RTC_WAKEUP, now_millis+sleep_time_millis, sleepIntent);
		}
		//TODO: add alarm time as Notification
		showMessage(this, getString(R.string.alarm_set) + tommorow + " " + sleeptime_str);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.set_now:
    		if (_time_picker.isEnabled()) {
    			Calendar now = new GregorianCalendar();
    			_time_picker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
    			_time_picker.setCurrentMinute(now.get(Calendar.MINUTE));
    		}
    		break;
    	case R.id.stop_vibration:
    		if (_vibrator != null) {
    			_vibrator.cancel();
    		}
    		break;
//TODO: remove
//    	case R.id.play_wakeup:
//    		Player.playWakeupMusic(this, _PREF_USE_NATIVE_PLAYER);
//    		break;
    	case R.id.pref:
    		startActivity(new Intent(this, MyPreference.class));
    		break;
    	case R.id.stop_music:
    		Player.stopMusic();
    		break;
    	default:
    		Log.i(PACKAGE_NAME, "Unknown menu");
    		return false;
    	}
    	return true;
    }

	public void onClick(View v) {
		if (v == _next_button) {
			Player.playNext();
		} else if (v == _sleep_wakeup_button) {
			setAlarm();
		} else {
			showMessage(v.getContext(), getString(R.string.unknown_button));
		}
	}

	private static void showMessage(Context c, String message) {
		Toast.makeText(c, message, Toast.LENGTH_LONG).show();
	}

	//TODO: separate BroadcastReceiver
	//TODO: implement music player as Service to play long time
	public static class Player extends BroadcastReceiver {
		private static final String MUSIC_PATH = Playlist.MUSIC_PATH;
		private static final String[] SLEEP_PLAYLIST = Playlist.SLEEP_PLAYLIST;
		private static final String[] WAKEUP_PLAYLIST = Playlist.WAKEUP_PLAYLIST;

		private static String[] current_playlist = SLEEP_PLAYLIST;
		private static MediaPlayer _player = null;
		private static int _index = 0;

		public static boolean isPlaying() {
			return _player != null && _player.isPlaying();
		}

		public static void reset () {
			_index = 0;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// AppWidgetManager mgr = AppWidgetManager.getInstance(context);
			Log.i(PACKAGE_NAME, "action: " + intent.getAction());
			if (intent.getAction().equals(WAKEUP_ACTION)) {
				Log.i(PACKAGE_NAME, "Wakeup action");
				if (Player.isPlaying()) {
					stopMusic();
				}
				AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				//TODO: add volume pref
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				Intent i = new Intent(context, MalarmActivity.class);
				i.setAction(WAKEUPAPP_ACTION);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				
				//native player cannot start until lock screen is displayed
				Player.playWakeupMusic(context, false);
			} else if (intent.getAction().equals(SLEEP_ACTION)) {
				if (intent.getExtras().getBoolean(_NATIVE_PLAYER_KEY)) {
					Player.stopMusicNativePlayer(context);
				} else {
					stopMusic();
				}
				showMessage(context, context.getString(R.string.goodnight));
			}
		}

		public static void stopMusic() {
			if (_player == null) {
				return;
			}
			_player.stop();
		}

		public static void playMusicNativePlayer(Context context, File f) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(f), "audio/*");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
		
		public static void stopMusicNativePlayer(Context context) {
			File f = new File(Playlist.STOP_MUSIC);
			if(! f.isFile()) {
				Log.i(PACKAGE_NAME, "No stop play list is found");
				return;
			}
			playMusicNativePlayer(context, f);
		}

		public static void playWakeupMusic(Context context, boolean use_native) {
			File f = new File(Playlist.WAKEUP_PLAYLIST_PATH);
			if (use_native && f.isFile()) {
				playMusicNativePlayer(context, f);
			} else {
				reset();
				playMusic(WAKEUP_PLAYLIST);
			}
		}

		public static void playSleepMusic(Context context, int min) {
			Log.i(PACKAGE_NAME, "start sleep music and stop");
			File f = new File(Playlist.SLEEP_PLAYLIST_PATH);
			if (_PREF_USE_NATIVE_PLAYER && f.isFile()) {
				Log.i(PACKAGE_NAME, "playSleepMusic: NativePlayer");
				playMusicNativePlayer(context, f);
			} else {
				Log.i(PACKAGE_NAME, "playSleepMusic: MediaPlayer");
				reset();
				playMusic(SLEEP_PLAYLIST);
			}
		}

		/**
		 * MediaPlayer only
		 */
		public static void playNext() {
			_index++;
			Log.i(PACKAGE_NAME, "playNext is called: " + _index);
			if (Player.isPlaying()) {
				stopMusic();
			}
			playMusic(current_playlist);
		}

		public static class MusicCompletionListener implements
			MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
			public void onCompletion(MediaPlayer mp) {
				Log.i(PACKAGE_NAME, "onCompletion listener is called");
				Player.playNext();
			}

			// This method is not called when DRM error is occured
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO: handle error
				Log.i(PACKAGE_NAME, "onError is called");
				return false;
			}
		}

		public static void playMusic(String[] playlist) {
			current_playlist = playlist;
			Log.i(PACKAGE_NAME, "startMusic");
			if (playlist == null || playlist.length == 0) {
				//TODO: throw Exception?
				return;
			}
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
				//do nothing
			}
		}
	}
}
