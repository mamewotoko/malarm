package com.mamewo.hello;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
	private MediaPlayer _player = null;
	private Button _music_button;
	private Button _bluetooth_button;
	private static final int REQUEST_ENABLE_BT = 10;
	private BluetoothAdapter _adapter;
	
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       _music_button = (Button)findViewById(R.id.button1);
       _music_button.setOnClickListener(this);
       _bluetooth_button = (Button)findViewById(R.id.button2);
       _bluetooth_button.setOnClickListener(this);
	   String path = "/mnt/sdcard/music/04 ŒŽŒõ.m4a";
       _player = new MediaPlayer();
	   if (! (new File(path)).exists()) {
		   //TODO: use resource music
		   return;
	   }
	   try {
		   _player.setDataSource(path);
	   } catch (IOException e) {
		   
	   }
   }
   
   public void startMusic () {
	   try {
		   if (! _player.isPlaying()) {
			   _player.prepare();
			   _player.start();
		   } else {
			   _player.stop();
		   }
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }

   public void startBlueTooth (View v) {
	   _adapter = BluetoothAdapter.getDefaultAdapter();
	   if (_adapter == null) {
		   showMessage(v.getContext(),  "no bluetooth");
		   return;
	   }
	   showMessage (v.getContext(), "start bluetooth");
	   if (! _adapter.isEnabled()) {
		   Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		   startActivityForResult(i, REQUEST_ENABLE_BT);
	   }
	   showMessage (v.getContext(), "request bluetooth");
	   displayBluetoothDevices ();
   }

   void displayBluetoothDevices() {
	   Set<BluetoothDevice> devices = _adapter.getBondedDevices();
	   String message = "";
	   for (BluetoothDevice dev : devices) {
		   message += dev.getName() + ": " + dev.getAddress() + "\n";
	   }
	   showMessage(this.getBaseContext(), message);
   }
   
   protected void onActivityResult (int requestCode, int resultCode, Intent data) {
	   Log.d("DEBUG", "onActivityResult: " + requestCode + ": " + resultCode);
	   if (requestCode == REQUEST_ENABLE_BT) {
		   if (resultCode != RESULT_OK) {
			   return;
		   }
		   displayBluetoothDevices();
	   }
   }
   
   public void onClick(View v) {
	   if (v == _music_button) {
		   startMusic();
	   } else if (v == _bluetooth_button) {
		   startBlueTooth(v);
	   } else {
		   showMessage(v.getContext(), "Unknown button!");
	   }
   }

   private static void showMessage(Context c, String message) {
	   Toast.makeText(c, message, Toast.LENGTH_LONG).show();
   }
}