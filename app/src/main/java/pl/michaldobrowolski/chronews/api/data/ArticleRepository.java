package pl.michaldobrowolski.chronews.api.data;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArticleRepository {

    private static AppDatabase appDatabase;
    private String DB_NAME = "db_article";

    public ArticleRepository(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    // *** START INSERT ***
    public void insertArticle(String articleTitle, String articlePublishedDate, String articleSource, String articleUrl) throws ExecutionException, InterruptedException {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(articleTitle);
        articleEntity.setPublishedDate(articlePublishedDate);
        articleEntity.setSourceName(articleSource);
        articleEntity.setUrl(articleUrl);

        new InsertArticleAsyncTask().execute(articleEntity).get();
    }

    private static class InsertArticleAsyncTask extends AsyncTask<ArticleEntity, Void,ArticleEntity> {
        @Override
        protected ArticleEntity doInBackground(ArticleEntity... articleEntities) {
            appDatabase.myDao().addArticle(articleEntities[0]);
            return null;
        }
    }
    // ***  END INSERT ***

    // ***  Start SELECT ***
    // Get all Articles
    public List<ArticleEntity> getAllArticles() throws ExecutionException, InterruptedException {
        return new GetAllArticlesAsyncTask().execute().get();
    }

    private static class GetAllArticlesAsyncTask extends AsyncTask<Void, Void,List<ArticleEntity>> {
        @Override
        protected List<ArticleEntity> doInBackground(Void... voids) {
            return appDatabase.myDao().getArticles();
        }
    }

    // Get single article by specific url
    public ArticleEntity getArticleByUrl(String articleUrl) throws ExecutionException, InterruptedException {
        return new GetArticleByUrlAsyncTask().execute(articleUrl).get();
    }

    private static class GetArticleByUrlAsyncTask extends AsyncTask<String, Void,ArticleEntity> {
        @Override
        protected ArticleEntity doInBackground(String... strings) {
            return appDatabase.myDao().getArticle(strings[0]);
        }
    }
    // ***  END SELECT  ***

    // *** START DELETE ***
    public void deleteArticle(String articleTitle, String articlePublishedDate, String articleSource, String articleUrl) throws ExecutionException, InterruptedException {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(articleTitle);
        articleEntity.setPublishedDate(articlePublishedDate);
        articleEntity.setSourceName(articleSource);
        articleEntity.setUrl(articleUrl);

        new DeleteArticleAsyncTask().execute(articleEntity).get();
    }

    private static class DeleteArticleAsyncTask extends AsyncTask<ArticleEntity, Void,Void> {
        @Override
        protected Void doInBackground(ArticleEntity... articleEntities) {
            appDatabase.myDao().deleteArticleByUrl(articleEntities[0].getUrl());
            return null;
        }
    }
    // ***  END DELETE ***

}
