package pl.michaldobrowolski.chronews.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ChronewsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ChronewsWidgetDataProvider dataProvider = new ChronewsWidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;
    }
}
