package com.mamewo.hello;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;

public class TestCircle extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyCircleView view = new MyCircleView(getApplication());
		setContentView(view);
	}
}

class MyCircleView extends View {
	private float _x = 0;
	private float _y = 0;
	private Timer _timer;
	private String _timestr = "";
	private long _time = 0;
	private Bitmap _bitmap = null;
	
	public MyCircleView(Context context) {
		super(context);
		setFocusable(true);
		this.setOnTouchListener(new MyMotionListener());
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
		paint.setColor(Color.BLUE);
		canvas.drawCircle(150, 150, 100, paint);
		paint.setColor(Color.RED);
		paint.setTextSize(24.0f);
		String coord = String.format("(%3.0f, %3.0f)", _x, _y);
		canvas.drawText(coord, 10, 40, paint);
		canvas.drawText(_timestr, 10, 400, paint);
		canvas.drawRect(new Rect(149, 149, 151, 151), paint);
		paint.setAlpha(0x88);
		canvas.drawBitmap(_bitmap, _x-20, _y-20, paint);
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
	
	private class MyMotionListener implements View.OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			_x = event.getX();
			_y = event.getY();
			v.invalidate ();
			return true;
		}
	}
}