* What is this?

This is a simple alarm application of android (version 2.3.3).

OVERVIEW
            sleep music (1h)                     wakeup music w/ vibration
 Alarm set ------------------> ..... Alarm time --------------->

HOW TO RUN:
1. Rename Playlist_tmpl.java into Playlist.java.
2. Fill Playlist.WAKEUP_PLAYLIST and Playlist.SLEEP_PLAYLIST by music
  filename in /sdcard/music/ folder
3. Build and run on your target device

MEMO:
Alarm application starts when user push appwidget.
After set/stop alarm button
   Play sleep music one hour
   Play alarm music at specified time

* TODO:
- editable playlist support
-- add favorite music
- implement music player as Service to play long time
- preset volume of sleep / wakeup mode.
- add license file of this source
- fix vibration bug (onNewIntent is called after unlocking device?)

* FUTURE WORK:
- add tweet button of now playing
- link to music store

----
Takashi Masuyama <mamewotoko@gmail.com>
http://www002.upp.so-net.ne.jp/mamewo/
