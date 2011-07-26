package com.mamewo.hello;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.mamewo.hello.R.id;

public class HelloActivity extends Activity implements OnClickListener {
   /** Called when the activity is first created. */
	private MediaPlayer _player = null;
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
//       TextView tv = new TextView(this);
       //       tv.setText("Hello, World");
       //setContentView(tv);
       Button b = (Button)findViewById(id.button1);
       b.setOnClickListener(this);
	   _player = new MediaPlayer();
   }
   
   public void onClick(View v) {
	   String path = "/mnt/sdcard/music/1-17 新しいラプソディー.m4a";
	   if (! (new File(path)).exists()) {
		   return;
	   }
	   try {
		   //TODO: stop current music
		   _player.setDataSource(path);
		   _player.prepare();
		   _player.stop();
		   _player.start();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }
}