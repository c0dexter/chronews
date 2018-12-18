package pl.michaldobrowolski.chronews.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.ArticleRepository;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

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
        articleRepository = new ArticleRepository(context);
        getDataFromDataBase();
    }

    @Override
    public void onDataSetChanged() {
        getDataFromDataBase();
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
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.item_widget_fav_article);

        remoteViews.setTextViewText(R.id.text_widget_fav_article_title, dbArticlesList.get(position).getTitle());
        remoteViews.setTextViewText(R.id.text_widget_fav_article_source, dbArticlesList.get(position).getSourceName());
        remoteViews.setTextViewText(R.id.text_widget_fav_published_time_counter, UtilityHelper.publishTimeCounter(dbArticlesList.get(position).getPublishedDate()));
        if (dbArticlesList.get(position).getImageUrl() != null) {
            try {
                Bitmap b = Picasso.get().load(dbArticlesList.get(position).getImageUrl()).get();
                remoteViews.setImageViewBitmap(R.id.image_widget_fav_article_thumb, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
            remoteViews.setViewVisibility(R.id.progress_bar_widget_fav_article_image, View.GONE);
        }

        String articleUrl = dbArticlesList.get(position).getUrl();
        final Intent intent = new Intent();
        final Bundle extra = new Bundle();
        extra.putString("articleUrl", articleUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(extra);
        remoteViews.setOnClickFillInIntent(R.id.image_widget_fav_article_thumb, intent);

        return remoteViews;
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

    private void getDataFromDataBase() {
        try {
            dbArticlesList = articleRepository.getAllArticles();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
