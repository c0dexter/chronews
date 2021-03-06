package pl.michaldobrowolski.chronews.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.FavouriteArticleRepository;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class ChronewsWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<ArticleEntity> dbArticlesList;
    private Context context;
    private FavouriteArticleRepository favouriteArticleRepository;
    private ArticleEntity articleEntity;


    ChronewsWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        favouriteArticleRepository = new FavouriteArticleRepository(context);
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
        } else {
            remoteViews.setImageViewResource(R.id.image_widget_fav_article_thumb, R.drawable.default_news_photo);
            remoteViews.setViewVisibility(R.id.progress_bar_widget_fav_article_image, View.GONE);
        }

        Intent intent = new Intent();
        intent.setData(Uri.parse(dbArticlesList.get(position).getUrl()));
        remoteViews.setOnClickFillInIntent(R.id.widget_fav_article_item, intent);

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
            dbArticlesList = favouriteArticleRepository.getAllArticles();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
