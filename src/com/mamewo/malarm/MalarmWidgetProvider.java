package com.mamewo.malarm;

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

public class MalarmWidgetProvider extends AppWidgetProvider {
	private static final String PACKAGE_NAME = MalarmWidgetProvider.class.getPackage().getName();
	public static final String HELLO_ACTION = PACKAGE_NAME + ".HELLO_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.i("malarm", "action: " + action);
		if (action.equals(HELLO_ACTION)) {
			final Intent i = new Intent(context, MalarmActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);
		final Intent intent = new Intent(context, MalarmWidgetProvider.class);
		intent.setAction(HELLO_ACTION);
		final PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.appwidget, pintent);

		appWidgetManager.updateAppWidget(appWidgetIds[0], rv);
	}
}
