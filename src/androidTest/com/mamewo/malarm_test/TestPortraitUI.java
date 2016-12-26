package com.mamewo.malarm_test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm24.*;
import com.mamewo.malarm24.R;

public class TestPortraitUI
	extends ActivityInstrumentationTestCase2<MalarmActivity>
{
	static final
	private int PORT = 3333;
	//TODO: get from device
	static final
	private int SCREEN_HEIGHT = 800;
	static final
	private int SCREEN_WIDTH = 480;

	private final static String TAG = "malarm_test";
	protected Solo solo_;
	private BufferedWriter _bw;
	private Socket _sock;
	private String _hostname = "192.168.0.20";
	//set true to capture screen (it requires CaptureServer in mimicj)
	private boolean _support_capture = false;

	public void initCapture() throws IOException {
		if (!_support_capture) {
			return;
		}
		_sock = new Socket(_hostname, PORT);
		_bw = new BufferedWriter(new OutputStreamWriter(_sock.getOutputStream()));
	}
	
	public void finalizeCapture() {
		if (!_support_capture) {
			return;
		}
		Log.i("malam_test", "finalizeCapture is called");
		try {
			_bw.close();
			_sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void captureScreen(String filename) {
		if (!_support_capture) {
			return;
		}
		try {
			_bw.write(filename + "\n");
			_bw.flush();
			//TODO: wait until captured
			Thread.sleep(1000);
		}
		catch (IOException e) {
			Log.i ("malarm_test", "capture failed: " + filename);
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public TestPortraitUI() {
		super("com.mamewo.malarm24", MalarmActivity.class);
	}
	
//	final static
//	private int[] TITLE_ID_LIST = {
//			R.string.pref_webview_url,
//			R.string.playlist_path_title,
//			R.string.pref_sleep_playlist,
//			R.string.pref_wakeup_playlist,
//			R.string.pref_reload_playlist,
//			R.string.pref_create_playlist_title,
//			R.string.pref_sleep_volume_title,
//			R.string.pref_wakeup_volume_title,
//			R.string.pref_clear_webview_cache_title,
//			R.string.use_native_player_title,
//			R.string.help_title,
//			R.string.malarm_version_title
//	};

	@Override
	public void setUp() throws Exception {
		solo_ = new Solo(getInstrumentation(), getActivity());
		initCapture();
	}

	@Override
	public void tearDown() throws Exception {
		System.out.println("tearDown is called");
		try {
			//Robotium will finish all the activities that have been opened
			solo_.finalize();
            solo_ = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finalizeCapture();
		getActivity().finish();
		super.tearDown();
	} 

	public boolean selectPreference(int titleId) {
		String targetTitle = solo_.getString(titleId);

		TextView view = null;
		do {
			ArrayList<TextView> list = solo_.getCurrentTextViews(null);
			for (TextView listText : list) {
				Log.i(TAG, "listtext: " + listText.getText());
				if(targetTitle.equals(listText.getText())){
					view = listText;
					break;
				}
			}
		}
		while(null == view && solo_.scrollDownList(0));
		if (view == null) {
			return false;
		}
		solo_.clickOnView(view);
		return true;
	}

	private void startPreferenceActivity() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.waitForActivity("MalarmPreference");
		solo_.sleep(500);
	}
	
	@Smoke
	public void testSetAlarm() {
		Date now = new Date(System.currentTimeMillis() + 60 * 1000);
		solo_.setTimePicker(0, now.getHours(), now.getMinutes());
		solo_.clickOnView(solo_.getView(R.id.alarm_button));
		solo_.sleep(2000);
		TextView targetTimeLabel = (TextView)solo_.getView(R.id.target_time_label);
		TextView sleepTimeLabel = (TextView)solo_.getView(R.id.sleep_time_label);
		Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() > 0);
		Assert.assertTrue("check sleep label", sleepTimeLabel.getText().length() > 0);
		solo_.goBack();
		solo_.sleep(61 * 1000);
		Assert.assertTrue("Switch alarm button wording", solo_.searchToggleButton(solo_.getString(R.string.stop_alarm)));
		Assert.assertTrue("Correct alarm toggle button state", solo_.isToggleButtonChecked(solo_.getString(R.string.stop_alarm)));
		Assert.assertTrue("check sleep label after wakeup", sleepTimeLabel.getText().length() == 0);
		//TODO: check music?
		//TODO: check vibration
		//TODO: check notification
		solo_.clickOnButton(solo_.getString(R.string.stop_alarm));
		solo_.sleep(1000);
		captureScreen("test_setAlarmTest.png");
		Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() == 0);
		Assert.assertTrue("check sleep label after alarm is stopped", sleepTimeLabel.getText().length() == 0);
		Assert.assertTrue("Alarm stopped", !solo_.isToggleButtonChecked(solo_.getString(R.string.set_alarm)));
	}
	
	@Smoke
	public void testSetNow() {
		//cannot get timepicker of Xperia acro...
		//TimePicker picker = solo_.getCurrentTimePickers().get(0);
		TimePicker picker = (TimePicker)solo_.getView(R.id.timePicker1);
		solo_.clickOnButton(solo_.getString(R.string.set_now_short));
		//umm... yield to target activity
		Calendar now = new GregorianCalendar();
		solo_.sleep(200);
		Assert.assertEquals((int)now.get(Calendar.HOUR_OF_DAY), (int)picker.getCurrentHour());
		Assert.assertEquals((int)now.get(Calendar.MINUTE), (int)picker.getCurrentMinute());
		captureScreen("test_setNow.png");
	}

	//TODO: voice button?
	public void testNextTuneShort() {
		View nextButton = solo_.getView(R.id.next_button);
		solo_.clickOnView(nextButton);
		solo_.sleep(2000);
		//speech recognition dialog
		//capture
		solo_.sendKey(Solo.DELETE);
	}

	public void testNextTuneLong() {
		View nextButton = solo_.getView(R.id.next_button);
		solo_.clickLongOnView(nextButton);
		solo_.sleep(2000);
		TextView view = (TextView)solo_.getView(R.id.sleep_time_label);
		String text = view.getText().toString();
		Log.i(TAG, "LongPressNext: text = " + text);
		Assert.assertTrue(text != null);
		//TODO: check preference value...
		Assert.assertTrue(text.length() > 0);
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_music));
		solo_.sleep(2000);
		String afterText = view.getText().toString();
		Assert.assertTrue(afterText == null || afterText.length() == 0);
	}

	@Smoke
	public void testStopVibrationMenu() {
		//TODO: cannot select menu by japanese, why?
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_vibration));
		solo_.sleep(2000);
	}
	
	@Smoke
	public void testPlayMenu() {
		solo_.clickOnMenuItem(solo_.getString(R.string.play_wakeup));
		solo_.sleep(5000);
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_music));
		solo_.sleep(1000);
	}
	
	/////////////////
	//config screen
	@Smoke
	public void testSitePreference() {
		startPreferenceActivity();
		selectPreference(R.string.playlist_path_title);
		//TODO: add more specific assert
		captureScreen("test_Preference.png");
	}
	
	@Smoke
	public void testCreatePlaylists() {
		startPreferenceActivity();
		selectPreference(R.string.pref_create_playlist_title);
		solo_.sleep(5000);
	}
	
	@Smoke
	public void testSleepVolume() {
		startPreferenceActivity();
		selectPreference(R.string.pref_sleep_volume_title);
	}

	@Smoke
	public void testWakeupVolume() {
		startPreferenceActivity();
		selectPreference(R.string.pref_wakeup_volume_title);
	}

	@Smoke
	public void testVolumeDown(){
		startPreferenceActivity();
		selectPreference(R.string.pref_wakeup_volume_title);
		solo_.clickOnButton("-");
		solo_.clickOnButton("OK");
		//TODO: check volume
	}

	@Smoke
	public void testVolumeUp(){
		startPreferenceActivity();
		selectPreference(R.string.pref_wakeup_volume_title);
		solo_.clickOnButton("\\+");
		solo_.clickOnButton("OK");
		//TODO: check volume
	}
	
	//TODO: add to test number / alphabet into edit box of volume preference
	
	//add double tap test of webview
	
	@Smoke
	public void testDefaultTimePreference() {
		startPreferenceActivity();
		selectPreference(R.string.pref_default_time_title);
	}

	@Smoke
	public void testVibration() {
		startPreferenceActivity();
		selectPreference(R.string.pref_vibration);
		solo_.sleep(1000);
	}

	@Smoke
	public void testSleepPlaylist() {
		startPreferenceActivity();
		selectPreference(R.string.pref_sleep_playlist);
		solo_.waitForActivity("PlaylistViewer");
		solo_.assertCurrentActivity("Playlist viewer should start", PlaylistViewer.class);
	}

	@Smoke
	public void testWakeupPlaylist() {
		startPreferenceActivity();
		selectPreference(R.string.pref_wakeup_playlist);
		solo_.waitForActivity("PlaylistViewer");
		//TODO: check title
		solo_.assertCurrentActivity("Playlist viewer should start", PlaylistViewer.class);
	}

	@Smoke
	public void testPlaylistLong() {
		startPreferenceActivity();
		selectPreference(R.string.pref_sleep_playlist);
		solo_.waitForActivity("PlaylistViewer");
		solo_.clickLongInList(0);
		//TODO: add check
		captureScreen("test_playlistlong.png");
	}
	

	@Smoke
	public void testReloadPlaylist() {
		startPreferenceActivity();
		selectPreference(R.string.pref_reload_playlist);
	}
	
	@Smoke
	public void testClearCache() {
		startPreferenceActivity();
		selectPreference(R.string.pref_clear_webview_cache_title);
		solo_.sleep(500);
	}
	
	@Smoke
	public void testHelp() {
		startPreferenceActivity();
		selectPreference(R.string.help_title);
		solo_.sleep(4000);
		captureScreen("test_Help.png");
	}
	
	public void testDummyListScroll() {
		startPreferenceActivity();
		solo_.scrollDown();
		solo_.sleep(500);
		View targetView = null;
		for (TextView view : solo_.getCurrentTextViews(null)) {
			Log.i(TAG, "title: " + view.getText());
			if(view.getText().equals("Sleep tunes volume")){
				targetView = view;
			}
		}
		if (targetView != null) {
			solo_.clickOnView(targetView);
		}
		solo_.sleep(5000);
	}
	
	@Smoke
	public void testVersion() {
		startPreferenceActivity();
		selectPreference(R.string.malarm_version_title);
		ImageView view = solo_.getImage(1);
		solo_.clickOnView(view);
		//TODO: check that browser starts
		captureScreen("test_Version.png");
	}

//	@Smoke
//	public void testDummy() {
//		solo_.sleep(10000);
//		solo_.clickOnScreen(20, 160);
//		solo_.sleep(500);
//		Log.i(TAG, "********** testDummy double click");
////		solo_.clickOnScreen(50, 250);
////		solo_.clickOnScreen(50, 250);
//		View view = solo_.getView(R.id.webView1);
//		solo_.clickOnView(view);
//		solo_.clickOnView(view);
//		solo_.sleep(10000);
//	}
	
	//TODO: fix!
	public void testDoubleTouchLeft() {
		float x = (float)(SCREEN_WIDTH / 6);
		float y = (float)(SCREEN_HEIGHT - 100);
		solo_.clickLongOnView(solo_.getView(R.id.loading_icon));
		View webview = solo_.getView(R.id.webView1);
		int[] pos = new int[2];
		webview.getLocationOnScreen(pos);
		Log.i("malarm_test", "view pos: " + pos[0] + " " + pos[1]);
		
		x = pos[0] + 40;
		y = pos[1] + 40;
		solo_.clickOnScreen(x, y);
		solo_.sleep(100);
		solo_.clickOnScreen(x, y);
		solo_.sleep(5000);
		//goto prev index
		solo_.finishOpenedActivities();
	}
	
	//TODO: fix!
	public void testDoubleTouchRight() {
		float x = (float)(SCREEN_WIDTH - (SCREEN_WIDTH / 6));
		float y = (float)(SCREEN_HEIGHT - 40);
		solo_.clickOnScreen(x, y);
		solo_.sleep(100);
		solo_.clickOnScreen(x, y);
		solo_.sleep(5000);
	}
	
	//TODO: default config test	
	//TODO: add test of widget
	//TODO: playlist edit
}
