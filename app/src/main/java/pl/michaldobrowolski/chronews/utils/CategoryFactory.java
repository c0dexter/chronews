package pl.michaldobrowolski.chronews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.CategoryBoardFragment;
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
    private boolean atLeastOneCategorySelected = false;

    public CategoryFactory(Context context, ApiInterface apiInterface) {
        this.context = context;
        this.apiInterface = apiInterface;
    }

    public List<Category> getCategoryObjectList() {
        return categoryObjectList;
    }


    public void createCategories() {
        if (UtilityHelper.isOnline(context)) {
            String countryCode;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if((preferences.getBoolean("key_switch_country_of_category_board", false))){
                countryCode = preferences.getString("key_country_code_top_headlines", null);
            } else{
                countryCode = "gb";
            }

            if (NewsApiUtils.Category.values().length > 0) {
                for (final NewsApiUtils.Category category : NewsApiUtils.Category.values()) {
                    Boolean categorySelected = preferences.getBoolean("key_category_" + category.getCategory(), true);
                    if (categorySelected) {
                        atLeastOneCategorySelected = true;
                    }
                    Call<News> call;
                    if (categorySelected) {
                        call = apiInterface.topHeadlines(countryCode, category.getCategory(), null, 20, null, API_KEY);
                        call.enqueue(new Callback<News>() {
                            @Override
                            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    int newsIndex = 0;
                                    news = response.body();
                                    urlToThumbnail = news.getArticles().get(newsIndex).getUrlToImage();

                                    while ((urlToThumbnail == null || news.getArticles().get(newsIndex).getUrlToImage().equals("")) && newsIndex < 20) {
                                        newsIndex++;
                                        urlToThumbnail = news.getArticles().get(newsIndex).getUrlToImage();
                                    }
                                    categoryObjectList.add(new Category(category.getCategory(), urlToThumbnail));
                                    CategoryBoardFragment.adapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(context, R.string.fetching_data_failed_message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                }
            }
            if (!atLeastOneCategorySelected) {
                Toast.makeText(context, R.string.no_categories_set_message, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
        }

    }
}
