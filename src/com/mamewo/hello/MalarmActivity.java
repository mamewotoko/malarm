package com.mamewo.hello;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.webkit.*;
import android.net.Uri;
import android.net.http.*;

public class MalarmActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	//TODO: add to resource?
	public static final String WAKEUP_ACTION = "com.mamewo.hello.WAKEUP_ACTION";
	public static final String WAKEUPAPP_ACTION = "com.mamewo.hello.WAKEUPAPP_ACTION";
	public static final String SLEEP_ACTION = "com.mamewo.hello.SLEEP_ACTION";
	
	private static final Integer DEFAULT_HOUR = new Integer(7);
	private static final Integer DEFAULT_MIN = new Integer(0);
	
	private Button _next_button;
	private Button _sleep_wakeup_button;
	private TimePicker _time_picker;
	private TextView _time_label;
	private WebView _webview;
	//TODO: add vibration preference
	private Vibrator _vibrator;
	@SuppressWarnings("unused")
	private PhoneStateListener _calllistener;
	private static boolean _SCHEDULED = false;
	private static boolean _PREF_USE_NATIVE_PLAYER;
	private static boolean _PREF_VIBRATE;
	
    public class MyCallListener extends PhoneStateListener{
    	MalarmActivity _activity;
    	public MyCallListener(MalarmActivity context) {
    		_activity = context;
    		TelephonyManager telmgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    		telmgr.listen(this, LISTEN_CALL_STATE);
    	}
    	
    	public void onCallStateChanged (int state, String incomingNumber) {
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
		Log.i("Hello", "onCreate is called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_time_picker = (TimePicker) findViewById(R.id.timePicker1);
		_time_picker.setIs24HourView(true);
		_sleep_wakeup_button = (Button) findViewById(R.id.play_button);
		_sleep_wakeup_button.setOnClickListener(this);
		_next_button = (Button) findViewById(R.id.next_button);
		_next_button.setOnClickListener(this);
		_time_label = (TextView) findViewById(R.id.target_time_label);
		// set default time
		_vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		_webview = (WebView)findViewById(R.id.webView1);
		WebSettings config = _webview.getSettings();
		config.setJavaScriptEnabled(true);
		//to display twitter...
		config.setDomStorageEnabled(true);
		config.setJavaScriptEnabled(true);
		config.setSupportZoom(true);
		_webview.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				_webview.requestFocus();
				return false;
			}
		});
		final Activity activity = this;
		_webview.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		   //for debug
		   public void onLoadResource (WebView view, String url) {
			   Log.i("Hello", "loading: " + url);
			   //addhoc polling...
			   if (url.indexOf("bijint") > 0 && view.getContentHeight() > 400) {
				   //disable touch event on view?
				   //for normal layout
				   //view.scrollTo(480, 330);
				   //TODO: get precise position....
				   if(url.indexOf("binan") > 0) {
					   //TODO: fix scroll problem
					   view.scrollTo(0, 420);
				   } else {
					   //Log.i("Hello", "bijin");
					   view.scrollTo(0, 960);
				   }
			   }
		   }
		   public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
			 Toast.makeText(activity, "SSL error " + error, Toast.LENGTH_SHORT).show();
		   }
		   public void onPageFinished(WebView view, String url) {
			   Log.i("Hello", "onPageFinshed: " + url);
		   }
		 });
		//stop alarm when phone call
		_calllistener = new MyCallListener(this);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		_PREF_USE_NATIVE_PLAYER = pref.getBoolean("use_native_player", false);
		_PREF_VIBRATE = pref.getBoolean("vibrate", false);

		pref.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals("use_native_player")) {
					_PREF_USE_NATIVE_PLAYER = sharedPreferences.getBoolean(key, false);
				} else if (key.equals("vibrate")) {
					_PREF_VIBRATE = sharedPreferences.getBoolean(key, true);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		//start tokei
		Log.i("Hello", "onPause is called, start JavaScript");
		super.onResume();
		//WebView.onResume is hidden, why!?!?
		_webview.getSettings().setJavaScriptEnabled(true);
		loadWebPage(_webview);
	}

	@Override
	protected void onPause() {
		Log.i("Hello", "onPause is called, stop JavaScript");
		super.onPause();
		//stop tokei
		_webview.getSettings().setJavaScriptEnabled(false);
	}

	@Override
	protected void onStart () {
		Log.i("Hello", "onStart is called");
		super.onStart();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		_PREF_USE_NATIVE_PLAYER = pref.getBoolean("use_native_player", false);
		_PREF_VIBRATE = pref.getBoolean("vibrate", false);

		if (_time_picker.isEnabled()) {
			_time_picker.setCurrentHour(DEFAULT_HOUR);
			_time_picker.setCurrentMinute(DEFAULT_MIN);
		}
	}

	private void loadWebPage (WebView view) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String url = pref.getString("url", "http://twitter.com/");
		WebSettings config = _webview.getSettings();
		if (url.indexOf("bijint") > 0) {
			config.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			config.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		} else {
			config.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		}
		showMessage(this, "Loading...");
		_webview.loadUrl(url);
	}
	
	protected void onNewIntent (Intent intent) {
		String action = intent.getAction();
		if (action != null && action.equals(WAKEUPAPP_ACTION)) {
			if (_PREF_VIBRATE && _vibrator != null) {
				long pattern[] = { 10, 2000, 500, 1500, 1000, 2000 };
				_vibrator.vibrate(pattern, 1);
			}
			_time_picker.setEnabled(true);
		}
		if (_time_picker.isEnabled()) {
			_time_picker.setCurrentHour(DEFAULT_HOUR);
			_time_picker.setCurrentMinute(DEFAULT_MIN);
		}
	}
	
	private PendingIntent makePintent(String action) {
		Intent i = new Intent(this, Player.class);
		i.setAction(action);
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

    //TODO: cancel or stop? (playing alarm?)
    private void cancelAlarm () {
		PendingIntent p = makePintent(WAKEUP_ACTION);
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// TODO: adjust time of _time_picker
		_time_picker.setEnabled(true);
		_time_label.setText("");
		mgr.cancel(p);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
    	case R.id.set_now:
    		Calendar now = new GregorianCalendar();
    		_time_picker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
    		_time_picker.setCurrentMinute(now.get(Calendar.MINUTE));
    		break;
       	case R.id.stop_vibration:
    		if (_vibrator != null) {
    			_vibrator.cancel();
    		}
    		break;
    	case R.id.play_wakeup:
    		Player.playWakeupMusic(this);
    		break;
    	case R.id.pref:
    		startActivity(new Intent(this, MyPreference.class));
    		break;
    	case R.id.stop_music:
    		Player.stopMusicNativePlayer(this);
    		break;
    	default:
    		Log.i("Hello", "Unknown menu");
    		return false;
    	}
    	return true;
    }
	
	public void startAlarm() {
		Log.i("Hello", "scheduleToPlaylist is called");
		//TODO: hide keyboard?
		if (_SCHEDULED) {
			cancelAlarm();
			if (_vibrator != null) {
				_vibrator.cancel();
			}
			//TODO: fix design
			if (_PREF_USE_NATIVE_PLAYER) {
				Player.stopMusicNativePlayer(this);
			} else {
				Player.stopMusic();
			}
			//TODO: what's happen if now playing alarm sound?
			showMessage(this, getString(R.string.music_stopped));
			_SCHEDULED = false;
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
			tommorow = " (" + getString(R.string.tomorrow) + ")";
		}
		//TODO: make function
		_time_picker.setEnabled(false);
		_time_label.setText(String.format("%2d/%2d %02d:%02d", target.get(Calendar.MONTH)+1, target.get(Calendar.DATE), target_hour, target_min));
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = makePintent(WAKEUP_ACTION);
		mgr.set(AlarmManager.RTC_WAKEUP, target_millis, pendingIntent);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int min = Integer.valueOf(pref.getString("sleeptime", "60"));
		Player.playSleepMusic(this, min);
		long sleep_time_millis = min * 60 * 1000;
		//TODO: localize
		String sleeptime_str = String.valueOf(min) + " min";
		if (target_millis - now_millis >= sleep_time_millis) {
			PendingIntent sleepIntent = makePintent(SLEEP_ACTION);
			mgr.set(AlarmManager.RTC_WAKEUP, now_millis+sleep_time_millis, sleepIntent);
		}
		showMessage(this, getString(R.string.alarm_set) + tommorow + " " + sleeptime_str);
		_SCHEDULED = true;
	}

	public void onClick(View v) {
		if (v == _next_button) {
			Player.playNext();
		} else if (v == _sleep_wakeup_button) {
			startAlarm();
		} else {
			showMessage(v.getContext(), getString(R.string.unknown_button));
		}
	}

	private static void showMessage(Context c, String message) {
		Toast.makeText(c, message, Toast.LENGTH_LONG).show();
	}

	//TODO: implement music player as Service to play long time
	public static class Player extends BroadcastReceiver {
		private static final String MUSIC_PATH = Playlist.MUSIC_PATH;
		private static final String[] SLEEP_PLAYLIST = Playlist.SLEEP_PLAYLIST;
		private static final String[] WAKEUP_PLAYLIST = Playlist.WAKEUP_PLAYLIST;

		private static String[] current_playlist = SLEEP_PLAYLIST;
		private static MediaPlayer _player = null;
		private static int _index = 0;

		enum PLAYER_KIND { NATIVE, NOT_NATIVE };
		
		public static boolean isPlaying() {
			return _player != null && _player.isPlaying();
		}

		public static void reset () {
			_index = 0;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// AppWidgetManager mgr = AppWidgetManager.getInstance(context);
			Log.i("Hello", "action: " + intent.getAction());
			if (intent.getAction().equals(WAKEUP_ACTION)) {
				Log.i("Hello", "Wakeup action");
				if (Player.isPlaying()) {
					stopMusic();
				}
				AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				//TODO: add volume to pref
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				Intent i = new Intent(context, MalarmActivity.class);
				i.setAction(WAKEUPAPP_ACTION);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				playWakeupMusic(context);
			} else if (intent.getAction().equals(SLEEP_ACTION)) {
				stopMusic();
				showMessage(context, context.getString(R.string.goodnight));
				//TODO: power off?
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
			PLAYER_KIND _kind;
			Context _context;
			
			public SleepThread(Context context, long sleeptime, PLAYER_KIND kind) {
				Log.i("Hello", "SleepThread is created");
				_context = context;
				_sleeptime = sleeptime;
				_kind = kind;
			}

			public void run() {
				Log.i("Hello", "SleepThread run");
				try {
					Thread.sleep(_sleeptime);
					if (_kind == PLAYER_KIND.NATIVE) {
						Player.stopMusicNativePlayer(_context);
					} else {
						Player.stopMusic();
					}
					// TODO: sleep device?
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
				Log.i("Hello", "No stop play list is found");
				return;
			}
			playMusicNativePlayer(context, f);
		}

		//TODO: fix design
		public static void playWakeupMusic(Context context) {
			File f = new File(Playlist.WAKEUP_PLAYLIST_PATH);
			if (_PREF_USE_NATIVE_PLAYER && f.isFile()) {
				playMusicNativePlayer(context, f);
				//TODO: show tokei
			} else {
				Player.reset();
				playMusic(WAKEUP_PLAYLIST);
			}
		}

		public static void playSleepMusic(Context context, int min) {
			Log.i("Hello", "start sleep music and stop");
			// TODO: use Alarm instead of Thread
			long playtime_millis = min * 60 * 1000;
			reset();
			File f = new File(Playlist.SLEEP_PLAYLIST_PATH);
			SleepThread t;
			if (_PREF_USE_NATIVE_PLAYER && f.isFile()) {
				Log.i("Hello", "playSleepMusic: NativePlayer");
				playMusicNativePlayer(context, f);
				t = new SleepThread(context, playtime_millis, PLAYER_KIND.NATIVE);
			} else {
				//TODO: use native player
				Log.i("Hello", "playSleepMusic: MediaPlayer");
				t = new SleepThread(context, playtime_millis, PLAYER_KIND.NOT_NATIVE);
				playMusic(SLEEP_PLAYLIST);
			}
			t.start();
		}

		public static void playNext() {
			_index++;
			Log.i("Hello", "playNext is called: " + _index);
			if (Player.isPlaying()) {
				stopMusic();
			}
			playMusic(current_playlist);
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

		public static void playMusic(String[] playlist) {
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
	}
}
