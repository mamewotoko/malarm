malarm - Good-night & Good-morning with your favorite tunes [![Build Status](https://travis-ci.org/mamewotoko/malarm.svg?branch=master)](https://travis-ci.org/mamewotoko/malarm)
===========================================================

What is this?
-------------
This is a simple alarm application of android (version 2.2.x - 2.3.3, 4.x).

Overview
--------
                sleep tunes (e.g.1h)               wake-up tunes w vibration
                                                   application starts
     Alarm set ------------------> ..... Alarm time --------------->
     press "set alarm" button                      display google calendar/Gmail etc..
     

 If you want to stop vibration only, please use "stop vibration" menu

Resources
----------
### Demo video
http://www.youtube.com/watch?v=vA2F_dO0mQA

### Screenshot
![Japanese screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_ja.png)
![English screen shot](https://github.com/mamewotoko/malarm/raw/master/doc/alarm_en.png)
![Preference](https://github.com/mamewotoko/malarm/raw/master/doc/malarm_pref.png)

### Help page
* [Japanese](http://mamewotoko.github.com/malarm/doc/index.html) 
* [English](http://mamewotoko.github.com/malarm/doc/index_en.html)

### Google Play
 [![my play page](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](http://play.google.com/store/search?q=pub:mamewo)
* [For 2.3.x](https://play.google.com/store/apps/details?id=com.mamewo.malarm24)
* [For 2.1 - 2.2](https://play.google.com/store/apps/details?id=com.mamewo.malarm78)

### Github
https://github.com/mamewotoko/malarm

How to start
----------------------
1. Put your music file into /sdcard/music directory of android device
2. Prepare m3u format play list for sleep (named sleep.m3u) and wakeup (named wakeup.m3u).
m3u file contains one music filename in one line.
3. Put playlists and stop.m4a file in /sdcard/music/ directory of android device.
4. Download apk file from 
https://github.com/mamewotoko/malarm/downloads
or Build malarm and install it on android device
5. Run malarm on android device
6. Enjoy!

You can change path to playlist and music files by "Playlist directory" preference screen

Tested Device
-------------
* Xperia acro
    * Android 2.3.4 (API level 10)
    * 480x854 (WVGA+) screen
* AQUOS PHONE Serie SHL21
    * Android 4.0.4
    * Android 4.1.2
* Android 2.1 emulator (API level 7)
* Android 2.2 emulator (API level 8)
* Android 4.0 emulator

Playlist format
---------------
Basically m3u format.
Podcast is supported by adding podcast XML url like following
```
podcast:http://www.nhk.or.jp/rj/podcast/rss/english.xml
```
The first episode is played.

Limitation
----------
* Music stops when DRM decoding failed

TODO
----
* use Actionbar
* support android-6, 7, 8 and remove malarm78
* stop playing on phone call
  * requires READ_PHONE_STATE permission, then should be liked to the privacy policy of this app
* easy play list
  * record voice like "wakeup!" and use it
* preset voice
  * using miku?
  * read text on web using text to speech 
    http://developer.android.com/resources/articles/tts.html
* use [Compatibility Test Suite](http://source.android.com/compatibility/cts-intro.html) to test
* use gradle to build
  * download robotium and scirocco jar from repository
  * but ant is enough and fast....
* merge PlayserService from podplayer project
* add UI to widen WebView area
* use playlist created by Google music player
  * add UI to select playlist for wakeup and sleep.
* Write more detail to help page
  * how to create playlist
  * how to create customized url list file
* add preference to avoid network access from web viewer
  * add network preference, disable/enable
  * e.g. display photo
* fix bugs
  * long press set alarm button
    -> rotate
    -> then notification disappears but alarm is set
  * alarm set
    -> pause music from menu
    -> notification title is incorrect
* make COOL widget to set alarm (make clock widget?)
* show music title as a notification, not filename
* add play button on volume preference to check volume
* show mark on playing music in playlist preference
* improve UI to create and edit playlist
* improve accessibility
http://android-developers.blogspot.jp/2012/04/accessibility-are-you-serving-all-your.html?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed:+blogspot/hsDu+(Android+Developers+Blog)
* add introduction guide or tutorial to create playlist
* improve wording especially one of notification
* parse preference.xml to test preference easily. (remove lookup and table)
* add preference to set silent mode or restore volume
* change notification icon while playing music
* change webview into photo view, movie player...
  * separate webview part from main activity
* add UI to play ringtone on VolumePreference to check volume
* hide time picker after malarm is set? (or count down?)
  * scale of web contents
* move current playing position when playlist is edited
* add mode to display alert dialog if device is not charged when alarm is set
* fix UI update bug when sleep timer expires (broadcast -> activity)
* conditional playlist
  * Sunday, holiday playlist etc...
* add more test cases
  * setAlarm -> Activity quits
  * double touch of webview
  * label text is updated when music is stopped or alarm is canceled
  * check default config value
* fix bug when native player is used....
* support HVGA, WVGA, WVGA+ resolution
  * http://developer.android.com/resources/dashboard/screens.html

Future work
-----------
* localize: Germany, French, Chinese
* support multiple episode of podcast?
* support shuffle of music
* Design for tablets
* record wakeup time
* gift app with audio files
  * e.g. buy some music files from google play
  * e.g. mp4 file which recorded my playing the piano (free audio)
  * config music files, playlist and web site list, pack and send to friend

Appendix
--------
### How To Build From Command Line
1. Create local.properties file and set sdk.dir property to location where you installed android SDK
    `sdk.dir=<path to android SDK>`
2. In project top directory execute following command

    ```bash
    ant debug
    ```
3. malarm-debug.apk will be created in bin directory, if successed

### Start Android Emulator And Install APK From Command Line
1. start android emulator

    ```bash
    emulator -avd <avdname>
    ```
2. install application on android emulator

    ```bash
    adb install bin/malarm-cmd.apk
    ```
(build.xml and *.property files are created by android create command)
or install app using ant

    ```bash
    ant installd
    ```
   
### Automated UI Test
There is a GUI automated test using Robotium, named
[malarm_test](https://github.com/mamewotoko/malarm_test)

Memo
----
* URL list specification
    1. if malarm/urllist.txt exists in external storage(e.g. SD card), it is used as url list file
       url list file is a text file which contains title followed by tab and its url par line
       you can specify slide show contents in asset
       e.g. file:///android_asset/local/step.html
    2. otherwise, url list specified by UI is used
        pref_url_list_value in values/arrays.xml is used
* MultiListPreference is based on the following web page
http://blog.350nice.com/wp/archives/240
* Alarm application starts when user push appwidget of malarm.
* [Dropsync](https://play.google.com/store/apps/details?id=com.ttxapps.dropsync) is a useful Android app!
* To use @Overrides annotation, use following eclipse setting 
`org.eclipse.jdt.core.compiler.compliance=1.6`

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://mamewo.ddo.jp/
