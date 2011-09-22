package com.mamewo.hello;

public class Playlist {
	//There are two ways to play music (a/b)
	//a) Use native music player by sending intent
	protected static final String WAKEUP_PLAYLIST_PATH = "/sdcard/music/wakeup.m3u";
	protected static final String SLEEP_PLAYLIST_PATH = "/sdcard/music/sleep.m3u";
	//TODO: add to resource?
	protected static final String STOP_MUSIC = "/sdcard/music/stop.m4a";
	
	//b) Use internal MediaPlayer instance
	protected static final String MUSIC_PATH = "/sdcard/music/";

	//TODO: list filenames of music file in "music" folder of SD card
	protected static final String[] WAKEUP_PLAYLIST = {
		"04 星間飛行(マクロスF) 1.m4a" };

	protected static final String[] SLEEP_PLAYLIST = {
		"11 約束.m4a",
		"01 帰れない二人.m4a"};
}
