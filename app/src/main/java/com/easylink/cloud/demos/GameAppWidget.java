package com.easylink.cloud.demos;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.easylink.cloud.R;

/**
 * Implementation of App Widget functionality.
 */
public class GameAppWidget extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.easyLink.cloud.CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(CLICK_ACTION)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.game_app_widget);
            Intent clickIntent = new Intent();
            clickIntent.setAction(CLICK_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.iv_actor,pendingIntent);
            manager.updateAppWidget(new ComponentName(context,GameAppWidget.class),remoteViews);

        }

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.game_app_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

