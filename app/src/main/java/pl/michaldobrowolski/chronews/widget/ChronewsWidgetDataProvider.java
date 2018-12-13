package pl.michaldobrowolski.chronews.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.ArticleRepository;

public class ChronewsWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<ArticleEntity> dbArticlesList;
    private Context context = null;
    private ArticleRepository articleRepository;
    private ArticleEntity articleEntity;


    public ChronewsWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        // TODO: get data from DB
        articleRepository = new ArticleRepository(context);
        getDbArticlesList();
    }

    private List<ArticleEntity> getDbArticlesList() {
        try {
            dbArticlesList = articleRepository.getAllArticles();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return dbArticlesList;
    }

    @Override
    public void onDataSetChanged() {
        getDbArticlesList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return dbArticlesList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(context.getPackageName(),
                android.R.layout.simple_list_item_1);
        mView.setTextViewText(android.R.id.text1, dbArticlesList.get(position).getTitle());
        mView.setTextColor(android.R.id.text1, Color.BLACK);
        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
