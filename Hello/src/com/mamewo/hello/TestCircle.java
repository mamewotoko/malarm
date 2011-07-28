package com.mamewo.hello;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;
import android.content.Intent;

public class TestCircle extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyCircleView view = new MyCircleView(getApplication());
		setContentView(view);
	}
}

class MyCircleView extends View implements View.OnTouchListener {
	private float _x = 0;
	private float _y = 0;
	private Timer _timer;
	private String _timestr = "";
	private long _time = 0;
	private Bitmap _bitmap = null;
	
	public MyCircleView(Context context) {
		super(context);
		setFocusable(true);
		this.setOnTouchListener(this);
		_timer = new Timer("hello", true);
		_timer.schedule(new MyTimerTask(this), 1000, 1000);
		Resources res = getResources();
		_bitmap = ((BitmapDrawable)res.getDrawable(R.drawable.img)).getBitmap();		
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.CYAN);
		canvas.drawCircle(150, 150, 100, paint);
		paint.setColor(Color.RED);
		paint.setTextSize(24.0f);
		String coord = String.format("(%3.0f, %3.0f)", _x, _y);
		canvas.drawText(coord, 10, 40, paint);
		//System.out.println("TestCircle: print debug: " + coord);
		canvas.drawText(_timestr, 10, 400, paint);
		canvas.drawRect(new Rect(149, 149, 151, 151), paint);
		paint.setColor(Color.GREEN);
		canvas.drawLine(0, 500, 400, 500, paint);
		canvas.drawLine(200, 500, 200, 800, paint);
		paint.setAlpha(0x88);
		canvas.drawBitmap(_bitmap, _x-20, _y-20, paint);
	}

	public boolean onTouch(View v, MotionEvent event) {
		_x = event.getX();
		_y = event.getY();
		System.out.printf("onTouch: (%.0f, %.0f)", _x, _y);

		if (event.getAction() == MotionEvent.ACTION_UP && _y > 500) {
			if (_x < 200) {
				Uri u = Uri.parse("http://www002.upp.so-net.ne.jp/mamewo/");
				Intent i = new Intent(Intent.ACTION_VIEW, u);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(i);
			} else {
				//start Hello activity
				//TODO: fix multiple view !!
				Intent i = new Intent();
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setClassName("com.mamewo.hello", "com.mamewo.hello.HelloActivity");
				getContext().startActivity(i);
			}
		}
		v.invalidate ();
		return true;
	}	
	
	private class MyTimerTask extends TimerTask {
		View _v = null;
		public MyTimerTask (View v) {
			super();
			_v = v; 
		}
		public void run() {
			Date now = new Date();
			_timestr = "time: " + now.toString () + " | " + (_time++);
			//View.invalidate cannot be called from non-UI thread
			_v.postInvalidate();
		}
	}
	}