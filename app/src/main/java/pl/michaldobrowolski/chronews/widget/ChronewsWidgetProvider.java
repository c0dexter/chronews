package pl.michaldobrowolski.chronews.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class ChronewsWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOAST = "com.dharmangsoni.widgets.ACTION_TOAST";
    public static final String EXTRA_STRING = "com.dharmangsoni.widgets.EXTRA_STRING";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.chronews_widget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widgetCollectionList,
                new Intent(context, ChronewsWidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widgetCollectionList,
                new Intent(context, ChronewsWidgetService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Handle all of widgets
        for (int appWidgetId : appWidgetIds) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse("www.google.pl"));
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.chronews_widget);
//            remoteViews.setOnClickPendingIntent(R.id.image_widget_fav_article_thumb, pendingIntent);
//            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setComponent(new ComponentName(context.getPackageName(),
                        "Activity.class"));
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.chronews_widget);
                views.setOnClickPendingIntent(R.id.widget_fav_article_item, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
//        Bundle dataFromBundle = intent.getExtras();
//        if (dataFromBundle != null) {
//            String articleUrl = dataFromBundle.getString("articleUrl");
//            Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
//            context.startActivity(launch);
//        }
        super.onReceive(context, intent);
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
