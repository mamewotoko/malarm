package com.mamewo.hello;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class HelloWidgetProvider extends AppWidgetProvider {
    public static final String HELLO_ACTION = "com.mamewo.hello.HELLO_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        //AppWidgetManager mgr = AppWidgetManager.getInstance(context);
    	Log.i("Hello", "action: " + intent.getAction());
    	if (intent.getAction().equals(HELLO_ACTION)) {
            //Toast.makeText(context, context.getString(R.string.touched), Toast.LENGTH_LONG).show();
        	Intent i = new Intent(context, HelloActivity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(i);
        }
    	super.onReceive(context, intent);
    }
    
    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.hellowidget_provider);
    	Intent intent = new Intent(context, HelloWidgetProvider.class);
    	intent.setAction(HELLO_ACTION);
    	PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.hellowidget_text, pintent);

    	appWidgetManager.updateAppWidget(appWidgetIds[0], rv);
    }
}
