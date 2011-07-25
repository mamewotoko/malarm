package com.mamewo.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.mamewo.hello.R.id;

public class HelloActivity extends Activity {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
//       TextView tv = new TextView(this);
       //       tv.setText("Hello, World");
       //setContentView(tv);
       Button b = (Button)findViewById(id.button1);
   }
}