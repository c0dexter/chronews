package pl.michaldobrowolski.chronews.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CategoryArticlesListViewModel extends AndroidViewModel {
    private static String API_KEY = BuildConfig.ApiKey;

    private final ApiInterface apiInterface;

    private String countryCode;

    private MutableLiveData<CategoryArticlesListResult> articlesCategoryLiveData;

    private CategoryArticlesListViewModel(@NonNull Application application, ApiInterface apiInterface) {
        super(application);
        this.apiInterface = apiInterface;
    }

    LiveData<CategoryArticlesListResult> getArticles(String category) {
        List<Article> articleList = new ArrayList<>();
        if (articlesCategoryLiveData == null) {
            articlesCategoryLiveData = new MutableLiveData<>();
            articlesCategoryLiveData.setValue(new CategoryArticlesListResult(articleList, -1));

            searchTopHeadlinesArticlesForSpecificCategory(category);
        }
        return articlesCategoryLiveData;
    }

    private void checkSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        Boolean preferredCountryCategorySwitch = preferences.getBoolean("key_switch_country_of_category_board", false);
        String preferredCountryCode = preferences.getString("key_country_code_categories_board", null);
        if (preferredCountryCategorySwitch) {
            countryCode = preferredCountryCode;
        } else {
            countryCode = getApplication().getString(R.string.default_country_code);
        }
    }

    private void searchTopHeadlinesArticlesForSpecificCategory(String category) {
        checkSharedPreferences();
        Call<News> call = apiInterface.topHeadlines(countryCode, category, null, null, null, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    CategoryArticlesListResult categoryArticlesListResult = new CategoryArticlesListResult(articles, response.code());
                    articlesCategoryLiveData.setValue(categoryArticlesListResult);
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final ApiInterface apiInterface;
        @NonNull
        private final Application application;


        Factory(@NonNull Application application, ApiInterface apiInterface) {
            this.apiInterface = apiInterface;
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CategoryArticlesListViewModel(application, apiInterface);
        }
    }
}
