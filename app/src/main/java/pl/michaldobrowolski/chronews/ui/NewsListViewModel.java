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
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListViewModel extends AndroidViewModel {
    private static String API_KEY = BuildConfig.ApiKey;

    private final ApiInterface apiInterface;

    private String countryCode;
    private String languageCode;
    private String sortingType;
    private String topHeadlinesCategory;
    private String phraseForSearchingTopHeadlines;
    private Boolean preferredTopHeadlinesSwitch;
    private Boolean preferredTopHeadlinesCategorySwitch;

    private MutableLiveData<NewsListResult> articlesLiveData;

    public NewsListViewModel(@NonNull Application application, ApiInterface apiInterface) {
        super(application);
        this.apiInterface = apiInterface;


    }

    public LiveData<NewsListResult> getArticles() {
        List<Article> articleList = new ArrayList<>();
        if (articlesLiveData == null) {
            articlesLiveData = new MutableLiveData<>();
            articlesLiveData.setValue(new NewsListResult(articleList, "", -1));

            searchTopHeadlinesArticles();
        }
        return articlesLiveData;
    }

    private void checkSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        countryCode = preferences.getString("key_country_code_top_headlines", null);
        languageCode = preferences.getString("key_language_code", null);
        sortingType = preferences.getString("key_sorting_code", null);
        topHeadlinesCategory = preferences.getString("key_top_headlines_category", null);
        phraseForSearchingTopHeadlines = preferences.getString("key_default_phrase_top_headlines", null);

        // Switches
        Boolean preferredLanguageSwitch = preferences.getBoolean("key_switch_specific_news_language", false);
        Boolean preferredCountrySwitch = preferences.getBoolean("key_switch_country_top_headlines", false);
        Boolean preferredTopHeadlinesSpecificPhraseSwitch = preferences.getBoolean("key_switch_search_phrase_top_headlines", false);

        // TODO: Logic for UI should me move from here, because this is not a parameter for making call

        preferredTopHeadlinesSwitch = preferences.getBoolean("key_switch_top_headlines_home_screen", false);
        preferredTopHeadlinesCategorySwitch = preferences.getBoolean("key_switch_category_top_headlines", false);


        // LOGIC FOR SWITCHES
        // == language for searched news
        if (!preferredLanguageSwitch) {
            languageCode = null;
        }
        // == top headlines feature is displaying on the home screen
        if (!preferredTopHeadlinesSwitch) {
            countryCode = null;
            topHeadlinesCategory = null;
            phraseForSearchingTopHeadlines = null;

        } else {
            // -- country
            if (!preferredCountrySwitch) {
                countryCode = null;
            }
            // -- category
            if (!preferredTopHeadlinesCategorySwitch) {
                topHeadlinesCategory = null;
            }
            // -- stable search phrase
            if (!preferredTopHeadlinesSpecificPhraseSwitch) {
                phraseForSearchingTopHeadlines = null;
            }
        }
    }

    private void searchTopHeadlinesArticles() {
        checkSharedPreferences();
        Call<News> call = apiInterface.topHeadlines(countryCode, topHeadlinesCategory, phraseForSearchingTopHeadlines, null, null, BuildConfig.ApiKey);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    NewsListResult newsListResult = new NewsListResult(articles, null, response.code());
                    articlesLiveData.setValue(newsListResult);
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    void searchArticlesBySearchPhrase(String searchPhrase) {
        Call<News> call = apiInterface.everything(searchPhrase, languageCode, sortingType, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    NewsListResult newsListResult = new NewsListResult(articles, searchPhrase, response.code());
                    articlesLiveData.setValue(newsListResult);
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


        public Factory(@NonNull Application application, ApiInterface apiInterface) {
            this.apiInterface = apiInterface;
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NewsListViewModel(application, apiInterface);
        }
    }
}
