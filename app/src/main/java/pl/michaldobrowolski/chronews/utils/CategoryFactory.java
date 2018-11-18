package pl.michaldobrowolski.chronews.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.CategoriesFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFactory {
    private static final String TAG = CategoryFactory.class.getClass().getSimpleName();
    private static final String API_KEY = BuildConfig.ApiKey;
    private List<Category> categoryObjectList = new ArrayList<>();
    private Context context;
    private ApiInterface apiInterface;
    private News news;
    private String urlToThumbnail;

    public CategoryFactory(Context context, ApiInterface apiInterface) {
        this.context = context;
        this.apiInterface = apiInterface;
    }

    public List<Category> getCategoryObjectList() {
        return categoryObjectList;
    }

    public void createCategories() {
        if (NewsApiUtils.Category.values().length >= 0) {
            for (final NewsApiUtils.Category category : NewsApiUtils.Category.values()) {
                Call<News> call;
                call = apiInterface.topHeadlines(null, category.getCategory(), null, 20, null, API_KEY);
                call.enqueue(new Callback<News>() {
                    @Override
                    public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int newsIndex = 0;
                            news = response.body();
                            Log.d(TAG, "NEWS RESPONSE BODY: " + news);
                            urlToThumbnail = news.getArticles().get(newsIndex).getUrlToImage();

                            while ((urlToThumbnail == null || news.getArticles().get(newsIndex).getUrlToImage().equals("")) && newsIndex < 20) {
                                newsIndex++;
                                urlToThumbnail = news.getArticles().get(newsIndex).getUrlToImage();
                            }

                            categoryObjectList.add(new Category(category.getCategory(), urlToThumbnail));
                            CategoriesFragment.adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(context, "Error. Fetching data failed :(", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Fetching data failed! Response code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                        call.cancel();
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
                Log.d(TAG, "URL to IMAGE: " + urlToThumbnail);
            }

        } else {
            Toast.makeText(context, "No categories defined", Toast.LENGTH_SHORT).show();
        }
    }
}
