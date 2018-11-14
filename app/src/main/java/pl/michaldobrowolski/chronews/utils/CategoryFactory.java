package pl.michaldobrowolski.chronews.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFactory {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
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
            for (NewsApiUtils.Category category : NewsApiUtils.Category.values()) {
                getCategoryThumbnail(category);

            }

        } else {
            Toast.makeText(context, "No categories defined", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoryThumbnail(final NewsApiUtils.Category category) {
        Call<News> call;
        call = apiInterface.topHeadlines(null, category.getCategory(), null, 1, null, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    news = response.body();
                    Log.d(TAG, "NEWS RESPONSE BODY: " + news);
                    urlToThumbnail = news.getArticles().get(0).getUrlToImage();
                    categoryObjectList.add(new Category(category.getCategory(), urlToThumbnail));
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
}
