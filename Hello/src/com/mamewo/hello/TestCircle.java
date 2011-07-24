package com.mamewo.hello;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

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
	
	public MyCircleView(Context context) {
		super(context);
		setFocusable(true);
		this.setOnTouchListener(new MyMotionListener());
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
		canvas.drawText(_x + ", " + _y, 10, 40, paint);
		canvas.drawRect(new Rect(149, 149, 151, 151), paint);
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