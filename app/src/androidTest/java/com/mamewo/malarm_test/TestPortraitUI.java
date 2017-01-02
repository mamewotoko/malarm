package com.mamewo.malarm_test;

import android.test.ActivityInstrumentationTestCase2;
//import android.test.suitebuilder.annotation.Smoke;
import android.test.suitebuilder.annotation.SmallTest;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jraska.falcon.FalconSpoon;
import com.mamewo.malarm24.MalarmActivity;
import com.mamewo.malarm24.PlaylistViewer;
import com.mamewo.malarm24.R;
import com.robotium.solo.Solo;

import junit.framework.Assert;

import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.ActionMenuView;

public class TestPortraitUI
        extends ActivityInstrumentationTestCase2<MalarmActivity>
{
    //TODO: get from device
    static final
    private int SCREEN_HEIGHT = 800;
    static final
    private int SCREEN_WIDTH = 480;

    private final static String TAG = "malarm_test";
    protected Solo solo_;

    public TestPortraitUI() {
        super("com.mamewo.malarm24", MalarmActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo_ = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        System.out.println("tearDown is called");
        try {
            //Robotium will finish all the activities that have been opened
            solo_.finalize();
            solo_ = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getActivity().finish();
        super.tearDown();
    }

    public boolean selectPreference(int titleId) {
        String targetTitle = solo_.getString(titleId);
        solo_.clickOnText(targetTitle);
        return true;
    }

    private void clickOverflowMenu(int menuStringId){
        ActionMenuView menuview = solo_.getView(ActionMenuView.class, 0);
        int numChild = menuview.getChildCount();
        View overflowMenuIcon = menuview.getChildAt(numChild-1);
        solo_.clickOnView(overflowMenuIcon);
        solo_.sleep(500);
        solo_.clickOnText(solo_.getString(menuStringId));
    }
    
    private void startPreferenceActivity() {
        boolean mainActWait = solo_.waitForActivity("MalarmActivity");
        Log.d(TAG, "waitMainActivity: " + mainActWait);
        
        //solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
        clickOverflowMenu(R.string.pref_menu);
        
        solo_.waitForActivity("MalarmPreference");
    }

    ///////////////////////////////
    @SmallTest
    public void testSetAlarm() {
        Date now = new Date(System.currentTimeMillis() + 60 * 1000);
        solo_.setTimePicker(0, now.getHours(), now.getMinutes());
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_alarm");

        solo_.clickOnView(solo_.getView(R.id.alarm_button));
        solo_.sleep(2000);
        TextView targetTimeLabel = (TextView) solo_.getView(R.id.target_time_label);
        Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() > 0);
        //solo_.goBack();

        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_alarm");
        solo_.sleep(65 * 1000);
        SwitchCompat alarmSwitch = (SwitchCompat)solo_.getView(R.id.alarm_button);
        Assert.assertTrue("Correct alarm toggle button state", alarmSwitch.isChecked());
        //TODO: check music?
        //TODO: check vibration
        //TODO: check notification
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_alarm");
        solo_.clickOnView(alarmSwitch);
        solo_.sleep(1000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_alarm");

        Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() == 0);
        Assert.assertTrue("Alarm stopped", !alarmSwitch.isChecked());
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_alarm");
    }

    @SmallTest
    public void testSetNow() {
        //cannot get timepicker of Xperia acro...
        //TimePicker picker = solo_.getCurrentTimePickers().get(0);
        TimePicker picker = (TimePicker) solo_.getView(R.id.timePicker1);
        solo_.clickOnButton(solo_.getString(R.string.set_now_short));
        //umm... yield to target activity
        Calendar now = new GregorianCalendar();
        solo_.sleep(200);
        Assert.assertEquals((int) now.get(Calendar.HOUR_OF_DAY), (int) picker.getCurrentHour());
        Assert.assertEquals((int) now.get(Calendar.MINUTE), (int) picker.getCurrentMinute());
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "set_now");
    }

    public void testNextButton() {
        View nextButton = solo_.getView(R.id.next_button);
        solo_.clickOnView(nextButton);
        solo_.sleep(5000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "next_button");
        //TODO: check playing item
        
        View playButton = solo_.getView(R.id.play_button);
        Assert.assertEquals("play/pause button is pause",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }

    public void testPreviousButton() {
        View previousButton = solo_.getView(R.id.previous_button);
        solo_.clickOnView(previousButton);
        solo_.sleep(5000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "previous_button");
        //TODO: check playing item

        View playButton = solo_.getView(R.id.play_button);
        Assert.assertEquals("play/pause button is pause",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }
    
    public void testPlayButton() {
        solo_.sleep(2000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "play_button");
    
        View playButton = solo_.getView(R.id.play_button);
        solo_.clickOnView(playButton);
        solo_.sleep(5000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "play_button");
        //TODO: check playing item

        Assert.assertEquals("play/pause button is pause",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }
    
    //sleep timer starts
    public void testPlayLongSleepTimer() {
        View playButton = solo_.getView(R.id.play_button);
        solo_.clickLongOnView(playButton);
        solo_.sleep(5000);
        //TODO: check preference value, playing...
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "next_tune_long");
        //stop
        solo_.clickOnView(playButton);
        solo_.sleep(2000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "next_tune_long");
    }

    @SmallTest
    public void testStopVibrationMenu() {
        //TODO: cannot select menu by japanese, why?
        //solo_.clickOnMenuItem(solo_.getString(R.string.stop_vibration));
        clickOverflowMenu(R.string.stop_vibration);

        solo_.sleep(2000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "stop_vibration_menu");
    }

    /////////////////
    //config screen
    @SmallTest
    public void testSitePreference() {
        startPreferenceActivity();
        selectPreference(R.string.pref_webview_url);
        //TODO: add more specific assert
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "site_preference");
    }

    public void testCreatePlaylists() {
        startPreferenceActivity();
        selectPreference(R.string.pref_create_playlist_title);
        solo_.sleep(5000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "create_playlist");
    }

    public void testSleepVolume() {
        startPreferenceActivity();
        selectPreference(R.string.pref_sleep_volume_title);
        //TODO: check volume
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "sleep_volume");
    }

    public void testWakeupVolume() {
        startPreferenceActivity();
        selectPreference(R.string.pref_wakeup_volume_title);
        //TODO: check volume
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "wakeup_volume");
    }

    public void testVolumeDown() {
        startPreferenceActivity();
        selectPreference(R.string.pref_wakeup_volume_title);
        solo_.clickOnButton("-");
        solo_.clickOnButton("OK");
        //TODO: check volume
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "volume_down");
    }

    public void testVolumeUp() {
        startPreferenceActivity();
        selectPreference(R.string.pref_wakeup_volume_title);
        solo_.clickOnButton("\\+");
        solo_.clickOnButton("OK");
        //TODO: check volume
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "volume_up");
    }

    //TODO: add to test number / alphabet into edit box of volume preference

    //add double tap test of webview

    public void testDefaultTimePreference() {
        startPreferenceActivity();
        selectPreference(R.string.pref_default_time_title);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "default_time_preference");
    }

    public void testVibration() {
        startPreferenceActivity();
        selectPreference(R.string.pref_vibration);
        solo_.sleep(1000);
        //TODO: add assert
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "vibration");
    }

    @SmallTest
    public void testSleepPlaylistPlay() {
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");

        startPreferenceActivity();

        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        solo_.waitForActivity("PlaylistViewer");
        selectPreference(R.string.pref_sleep_playlist);

        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        View playButton = solo_.getView(R.id.play_button);
        
        solo_.clickOnView(playButton);
        solo_.sleep(500);
        //TODO: check icon
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
        
        
        solo_.clickOnView(playButton);
        //TODO: check icon
        solo_.sleep(500);
        
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        Assert.assertEquals("play state",
                            solo_.getString(R.string.play_button_desc),
                            playButton.getContentDescription().toString());
    }

    public void testWakeupPlaylistPlay() {
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");

        startPreferenceActivity();
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        solo_.waitForActivity("PlaylistViewer");
        selectPreference(R.string.pref_wakeup_playlist);

        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        View playButton = solo_.getView(R.id.play_button);
        
        solo_.clickOnView(playButton);
        solo_.sleep(500);
        //TODO: check icon
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
        
        solo_.clickOnView(playButton);
        //TODO: check icon
        solo_.sleep(500);
        
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_play");
    }

    public void testSleepPlaylistClickShort() {
        startPreferenceActivity();
        selectPreference(R.string.pref_sleep_playlist);
        solo_.waitForActivity("PlaylistViewer");
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_short");
        solo_.clickInList(0);

        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_short");
        View playButton = solo_.getView(R.id.play_button);

        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }

    public void testSleepPlaylistClickLong() {
        startPreferenceActivity();
        selectPreference(R.string.pref_sleep_playlist);
        solo_.waitForActivity("PlaylistViewer");
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_long");
        solo_.clickLongInList(0);
        //TODO: add assert
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_long");
        Assert.assertTrue("operation dialog", solo_.waitForDialogToOpen(1000));
    }
    
    public void testSleepPlaylistNext() {
        startPreferenceActivity();
        
        selectPreference(R.string.pref_sleep_playlist);
        solo_.waitForActivity("PlaylistViewer");

        View nextButton = solo_.getView(R.id.next_button);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_next");
        
        solo_.clickOnView(nextButton);
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_next");

        View playButton = solo_.getView(R.id.play_button);

        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
       
        solo_.clickOnView(nextButton);
        solo_.sleep(500);
        
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_next");
        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }

    public void testSleepPlaylistPrevious() {
        startPreferenceActivity();
        selectPreference(R.string.pref_sleep_playlist);
        solo_.waitForActivity("PlaylistViewer");
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_previous");

        View previousButton = solo_.getView(R.id.previous_button);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_previous");
        
        solo_.clickOnView(previousButton);
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_previous");
        View playButton = solo_.getView(R.id.play_button);

        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
        
        solo_.clickOnView(previousButton);
        solo_.sleep(500);
        
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "playlist_previous");
        Assert.assertEquals("pause state",
                            solo_.getString(R.string.pause_button_desc),
                            playButton.getContentDescription().toString());
    }
    
    public void testReloadPlaylist() {
        startPreferenceActivity();
        selectPreference(R.string.pref_reload_playlist);

        FalconSpoon.screenshot(solo_.getCurrentActivity(), "reload_play_list");
    }

    public void testClearCache() {
        startPreferenceActivity();
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "clear_cache");
        selectPreference(R.string.pref_clear_webview_cache_title);
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "clear_cache");
    }

    public void testHelp() {
        startPreferenceActivity();
        selectPreference(R.string.help_title);
        solo_.sleep(4000);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "help");
    }

    // public void testDummyListScroll() {
    //     startPreferenceActivity();
    //     solo_.scrollDown();
    //     solo_.sleep(500);
    //     FalconSpoon.screenshot(solo_.getCurrentActivity(), "list_scroll");

    //     View targetView = null;
    //     for (TextView view : solo_.getCurrentViews(TextView.class)) {
    //         Log.i(TAG, "title: " + view.getText());
    //         if (view.getText().equals("Sleep tunes volume")) {
    //             targetView = view;
    //         }
    //     }
    //     if (targetView != null) {
    //         solo_.clickOnView(targetView);
    //     }
    //     solo_.sleep(5000);
    //     FalconSpoon.screenshot(solo_.getCurrentActivity(), "list_scroll");
    // }

    public void testVersion() {
        startPreferenceActivity();
        selectPreference(R.string.malarm_version_title);
        ImageView view = solo_.getImage(1);
        solo_.clickOnView(view);
        //TODO: check that browser starts
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "version");
    }

    //TODO: fix!
    // public void testDoubleTouchLeft() {
    //     float x = (float) (SCREEN_WIDTH / 6);
    //     float y = (float) (SCREEN_HEIGHT - 100);
    //     solo_.clickLongOnView(solo_.getView(R.id.loading_icon));
    //     View webview = solo_.getView(R.id.webView1);
    //     int[] pos = new int[2];
    //     webview.getLocationOnScreen(pos);
    //     Log.i("malarm_test", "view pos: " + pos[0] + " " + pos[1]);

    //     x = pos[0] + 40;
    //     y = pos[1] + 40;
    //     solo_.clickOnScreen(x, y);
    //     solo_.sleep(100);
    //     solo_.clickOnScreen(x, y);
    //     solo_.sleep(5000);
    //     //goto prev index
    //     solo_.finishOpenedActivities();
    //     FalconSpoon.screenshot(solo_.getCurrentActivity(), "double_touch_left");
    // }

    //TODO: fix!
    // public void testDoubleTouchRight() {
    //     float x = (float) (SCREEN_WIDTH - (SCREEN_WIDTH / 6));
    //     float y = (float) (SCREEN_HEIGHT - 40);
    //     solo_.clickOnScreen(x, y);
    //     solo_.sleep(100);
    //     solo_.clickOnScreen(x, y);
    //     solo_.sleep(5000);
    //     FalconSpoon.screenshot(solo_.getCurrentActivity(), "double_touch_right");
    // }

    @SmallTest
    public void testPreferenceScroll(){
        startPreferenceActivity();
        solo_.sleep(500);
        FalconSpoon.screenshot(solo_.getCurrentActivity(), "preference_scroll");
        //XXX
        for(int i = 0; i < 6; i++){
            solo_.scrollDown();
            solo_.sleep(500);
            FalconSpoon.screenshot(solo_.getCurrentActivity(), "preference_scroll");
        }
    }

    //TODO: default config test
    //TODO: add test of widget
    //TODO: playlist edit
}
