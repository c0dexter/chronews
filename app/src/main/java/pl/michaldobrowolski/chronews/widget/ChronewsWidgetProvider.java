package pl.michaldobrowolski.chronews.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class ChronewsWidgetProvider extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.chronews_widget);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent template = PendingIntent.getActivity(context, 0, intent, 0);
        views.setPendingIntentTemplate(R.id.widgetCollectionList, template);

        setRemoteAdapter(context, views);
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


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Handle all of widgets
        for (int appWidgetId : appWidgetIds) {

            try {
                Intent intent = new Intent(context, MainActivity.class);

            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        R.string.widget_error_login_app_message,
                        Toast.LENGTH_SHORT).show();
            }
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }


}
