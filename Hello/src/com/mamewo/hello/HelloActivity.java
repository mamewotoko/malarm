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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;

public class HelloActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public static final String WAKEUP_ACTION = "com.mamewo.hello.WAKEUP_ACTION";
	public static final String SLEEP_ACTION = "com.mamewo.hello.SLEEP_ACTION";
	// 1 hour
	public static long SLEEP_TIME = 60 * 60 * 1000;
//	public static long SLEEP_TIME = 10 * 1000;
	private static final Integer DEFAULT_HOUR = new Integer(7);
	private static final Integer DEFAULT_MIN = new Integer(0);
	
	private Button _next_button;
	private Button _sleep_wakeup_button;
	private TimePicker _time_picker;
	private TextView _time_label;
	private WebView _webview;

	private static final int REQUEST_ENABLE_BT = 10;
	private BluetoothAdapter _adapter;
	
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
		// set default time
		_webview = (WebView)findViewById(R.id.webView1);
	}

	protected void onStart () {
		super.onStart();
		_time_picker.setCurrentHour(DEFAULT_HOUR);
		_time_picker.setCurrentMinute(DEFAULT_MIN);
		//TODO: embed twitter screen?
		_webview.loadData("<html><head><title>link pane</title></head><body style=\"foreground: white;\"><br><br><h1><a href=\"http://twitter.com/\">Twitter</a></h1></body></html>", "text/html", "UTF-8");
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
    	case R.id.cancel_alarm:
    		cancelAlarm();
    		break;
    	default:
    		Log.i("Hello", "Unknown menu");
    		return false;
    	}
    	return true;
    }
	
	// TODO: implement alarm cancel
	public void scheduleToPlaylist() {
		Log.i("Hello", "scheduleToPlaylist is called");
		//TODO: hide keyboard?
		//TODO: use different button?
		if (Player.isPlaying()) {
			Player.stopMusic();
			//TODO: what's happen if now playing alarm sound?
			cancelAlarm();
			showMessage(this, getString(R.string.stop_music));
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
		_time_label.setText(String.format("%02d:%02d", target_hour, target_min));
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = makePintent(WAKEUP_ACTION);
		mgr.set(AlarmManager.RTC_WAKEUP, target_millis, pendingIntent);
		showMessage(this, getString(R.string.alarm_set) + tommorow);
		Player.startSleepMusic();
		//TODO: fix design...
		if (target_millis - now_millis >= SLEEP_TIME) {
			pendingIntent = makePintent(SLEEP_ACTION);
			mgr.set(AlarmManager.RTC_WAKEUP, now_millis+SLEEP_TIME, pendingIntent);
		}
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
			showMessage(v.getContext(), getString(R.string.unknown_button));
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
			"04 星間飛行(マクロスF) 1.m4a", "03 アスパラガス.m4a", "08 DAVID.m4a",
			"01 Seasons Of Love.m4a", "08 ブルーモーメント.m4a", "01 M☆gic.m4a",
			"03 銀河 [Album ver.].m4a", "1-17 新しいラプソディー.m4a",
			"1-12 The Longest Time.m4a", "10 いつでも夢を.m4a", "01 美しく燃える森.m4p",
			"06 Clap Your Hands Together.m4a", "05 たったった.m4a",
			"10 好きだって今日は言ったっけ_.m4a", "1-04 ロボッチ [Live].m4a",
			"1-03 Honesty.m4a", "01 ぶっ生き返す!!.m4a",
			"04 DEAR LIZ [Live].m4a", "02 maximum the hormone.m4a",
			"02 ROCK AND ROLL HERO.m4a", "10 虹.m4a", "15 御免ライダー.m4a",
			"02 心のファンファーレ.m4a", "16 Tong Poo.m4a", "03 my girl.m4a",
			"05 焔 -ほむら-.m4a", "01 ジ・エンターテイナー.m4a", "17 ヘヘヘイ.m4a",
			"17 与える男.m4a", "05 地平線を越えて.m4a",
			"2-05 Butterfly (Delaction Remix).m4a", "17 ハズムリズム.m4a",
			"1-02 大迷惑.m4a", "2-12 ヨイトマケの唄 [Live].m4a", "14 手引きのようなもの.m4a",
			"05 I Have Confidence.m4a", "07 ひとりカンタビレのテーマ.m4a",
			"04 Take Me In Your Arms.m4a", "02 道化師のソネット.m4p",
			"13 花 -Memento Mori-.m4a", "07 うそつき.m4a", "01 Voice.m4a",
			"05 Desert On The Moon.m4a", "07 あなたに会いにいこう.m4a",
			"04 ロボッチ.m4a", "13 夢の帆船.m4a", "13 恋のメガラバ.m4a",
			"02 BEAT IT.m4a", "09 はじめの一歩.m4a", "11 風の坂道.m4a",
			"05 威風堂々.m4a", "08 Eight Five Five.m4a", "01 月恋歌.m4a",
			"2-08 103.m4a", "01 鬱くしき人々のうた.m4a", "6-06 ターンAターン.m4a" };

	private static final String[] SLEEP_PLAYLIST = {
			"11 約束.m4a",
			"01 帰れない二人.m4a",
			"01 Adagio In G Minor.m4a",
			"07 白鳥.m4a",
			"09 BEATLES' MEDLEY (This boy ～ I'll be back).m4a",
			"09 Ave Maria (all . Kall).m4a",
			"1-07 グリーンスリーヴズによる幻想曲.m4a",
			"03 THERE IS A SHIP.m4a",
			"07 モン・アンファン.m4a",
			"03 吟遊詩人.m4a",
			"09 As Time Goes By.m4a",
			"14 THE GARDEN.m4a",
			"2-12 シシリエンヌ.m4a",
			"22 Some Day My Prince Will Come.m4a",
			"08 The Sheltering Sky.m4a",
			"13 タペストリー.m4a",
			"09 幻に誘われて.m4a",
			"08 NORWEGIAN WOOD.m4a",
			"07 浜辺の歌.m4a",
			"10 うたの素.m4a",
			"2-07 G線上のアリア.m4a",
			"1-06 ロンドンデリーの歌.m4a",
			"06 彩りの秋.m4a",
			"19 Ave Maria, Op. 52 No. 6.m4a",
			"1-12 ヴォカリーズ.m4a",
			"04 ゆりかご.m4a",
			"03 Traumerei.m4a",
			"2-05 愛の悲しみ.m4a",
			"04 RIVER TO OCEAN.m4a",
			"16 WHAT A WONDERFUL WORLD.m4a",
			"05 朝のうち曇り.m4a",
			"06 最後の子守歌.m4a",
			"03 G線上のアリア.m4a",
			"05 WOODSTOCK.m4a",
			"2-01 無伴奏チェロ組曲第1番～プレリュード_J.S.バッハ.m4a",
			"04 Misty.m4a",
			"05 Cry Me A River.m4a",
			"06 Merry Christmas Mr. Lawrence.m4a",
			"10 Unforgettable.m4a",
			"10 Candle On The Water.m4a",
			"05 Gymnopedies.m4a",
			"12 Deep River (all. Karr).m4a",
			"15 ジャパニーズ・ミュージック・ボックス (五木の子守唄).m4a",
			"07 THE DEEP.m4a",
			"07 Lullaby.m4a",
			"02 AMAZING GRACE.m4a",
			"10 YOU'D BE SO NICE TO COME HOME TO.m4a",
			"04 花音 ～カノン～.m4a",
			"11 put your hands up.m4a",
			"13 Bon Voyage.m4a",
			"03 アヴェ・マリア.m4a",
			"1-01 エトピリカ _ Etupirka.m4p",
			"02 SOMEWHERE SOMETIME.m4a",
			"03 ZERO LANDMINE-Piano+Cello version-.m4a",
			"15 Remembrance.m4a",
			"10 ウォーキング・イン・ジ・エアー.m4a",
			"10 潮騒 (2002 Live At Gloria Chapel).m4a",
			"02 Caribbean Blue.m4a",
			"09 Variations On The Kanon By Pachelbel.m4a",
			"10 だったん人の踊り.m4a",
			"01 Living In The Country.m4a",
			"02 Song.m4a",
			"01 Misty.m4a",
			"12 スノーマンズ・ミュージック・ボックス・ダンス.m4a",
			"05 トロイメライ.m4a",
			"07 ショパン_ 練習曲 ホ長調, 「別れの曲」.m4a",
			"03 夜桜 ～yozakura～.m4a",
			"41 三共『リゲインEB錠』「energy Flow」.m4a",
			"14 さよならの向う側.m4a",
			"01 SUMMER TIME ～HOUSE OF THE RISING SUN.m4a",
			"11 ワタスゲの原.m4a",
			"04 Air (arr Karr) From Orchestral Suite No. 3 In D Major. BWV..m4a",
			"10 The Holly And The Ivy.m4a",
			"06 A Charlie Brown Thanksgiving.m4a", "08 帰り道.m4a",
			"14 荒城の月 (all. Karr).m4a", "21 Reza (祈り).m4a",
			"07 Goyang-Goyang.m4a", "15 Angel.m4a", "06 Spring Creek.m4a",
			"08 Black Stallion.m4a", "01 Colors_Dance.m4a",
			"06 ゴリラがバナナをくれる日.m4a", "10 Early Morning Range.m4a",
			"09 Beethoven's Piano sonata No. 8, Pathetique.m4a",
			"02 Jesus, Jesus, Rest Your Head.m4a",
			"11 Living Without You.m4a", "07 竹田の子守唄.m4a" };

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
				AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				mgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				startMusic(WAKEUP_PLAYLIST);
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

			public SleepThread(long sleeptime) {
				Log.i("Hello", "SleepThread is created");
				_sleeptime = sleeptime;
			}

			public void run() {
				Log.i("Hello", "SleepThread run");
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
			//long playtime_millis = System.currentTimeMillis() + 60 * 60 * 1000;
			long playtime_millis = System.currentTimeMillis() + 10 * 1000;
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
		Context context = v.getContext();
		_adapter = BluetoothAdapter.getDefaultAdapter();
		if (_adapter == null) {
			showMessage(v.getContext(), context.getString(R.string.no_bluetooth));
			return;
		}
		showMessage(v.getContext(), context.getString(R.string.start_bluetooth));
		if (!_adapter.isEnabled()) {
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//TODO: use id!
			startActivityForResult(i, REQUEST_ENABLE_BT);
		}
		showMessage(context, context.getString(R.string.search_bluetooth));
		displayBluetoothDevices();
	}
}
