package com.mamewo.malarm24;

/**
 * @author Takashi Masuyama <mamewotoko@gmail.com>
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MalarmWidgetProvider
	extends AppWidgetProvider
{
	final static
	private String PACKAGE_NAME = MalarmWidgetProvider.class.getPackage().getName();
	final static
	public String HELLO_ACTION = PACKAGE_NAME + ".HELLO_ACTION";
	final static
	private String TAG = "malarm";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "action: " + action);
		if (HELLO_ACTION.equals(action)) {
			Intent i = new Intent(context, MalarmActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);
		Intent intent = new Intent(context, MalarmWidgetProvider.class);
		intent.setAction(HELLO_ACTION);
		PendingIntent pintent =
			PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.appwidget, pintent);

		appWidgetManager.updateAppWidget(appWidgetIds[0], rv);
	}
}
