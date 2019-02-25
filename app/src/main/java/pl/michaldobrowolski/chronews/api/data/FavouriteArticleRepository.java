package pl.michaldobrowolski.chronews.api.data;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FavouriteArticleRepository {

    private static AppDatabase appDatabase;

    public FavouriteArticleRepository(Context context) {
        String DB_NAME = "db_article";
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    // *** START INSERT ***
    public void insertArticle(String articleTitle,
                              String articlePublishedDate,
                              String articleSource,
                              String articleUrl,
                              String articleAuthor,
                              String articleDesc,
                              String articleImageUrl,
                              String articleContent) throws ExecutionException, InterruptedException {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setTitle(articleTitle);
        articleEntity.setPublishedDate(articlePublishedDate);
        articleEntity.setSourceName(articleSource);
        articleEntity.setUrl(articleUrl);
        articleEntity.setAuthor(articleAuthor);
        articleEntity.setDescription(articleDesc);
        articleEntity.setImageUrl(articleImageUrl);
        articleEntity.setContent(articleContent);

        new InsertArticleAsyncTask().execute(articleEntity).get();
    }

    public List<ArticleEntity> getAllArticles() throws ExecutionException, InterruptedException {
        return new GetAllArticlesAsyncTask().execute().get();
    }

    public void deleteArticle(
            String articleImageUrl
    ) throws ExecutionException, InterruptedException {

        new DeleteArticleAsyncTask().execute(articleImageUrl).get();
    }

    public void deleteAllArticles() {
        new DeleteAllArticlesAsyncTask().execute();
    }

    /**
     * This method is counting amount of specific url stored in db
     *
     * @param articleUrl - URL to article
     * @return - "TRUE" if article exist in DB, FALSE if article doesn't exist or there more than one
     * occurrence of item with the same URL
     */
    public boolean getArticleCountByUrl(String articleUrl) throws ExecutionException, InterruptedException {
        int articlesCount = new GetCountArticlesByUrlAsyncTask().execute(articleUrl).get();
        if (articlesCount > 1) {
            throw new IllegalStateException("There is more articles with the same URLs");
        }
        return articlesCount == 1;
    }

    private static class GetCountArticlesByUrlAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            return appDatabase.myDao().countArticlesWithUrl(strings[0]);
        }
    }

    private static class InsertArticleAsyncTask extends AsyncTask<ArticleEntity, Void, ArticleEntity> {
        @Override
        protected ArticleEntity doInBackground(ArticleEntity... articleEntities) {
            appDatabase.myDao().addArticle(articleEntities[0]);
            return null;
        }
    }

    private static class GetAllArticlesAsyncTask extends AsyncTask<Void, Void, List<ArticleEntity>> {
        @Override
        protected List<ArticleEntity> doInBackground(Void... voids) {
            return appDatabase.myDao().getArticles();
        }
    }

    private static class DeleteArticleAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            appDatabase.myDao().deleteArticleByUrl(strings[0]);
            return null;
        }
    }

    private static class DeleteAllArticlesAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            appDatabase.myDao().deleteAllArticles();
            return null;
        }
    }

}
