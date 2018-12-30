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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.NewsApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesBoardListViewModel extends AndroidViewModel {
    private static final String TAG = CategoriesBoardListViewModel.class.getSimpleName();
    private static String API_KEY = BuildConfig.ApiKey;
    private final ApiInterface apiInterface;

    private String categoriesCountryCode;
    private String categoryName;
    private News news;
    private String urlToThumbnail;
    private SharedPreferences preferences;
    private boolean atLeastOneCategorySelected = false;
    private List<Category> categoryObjectList = new ArrayList<>();

    // LiveData
    private MutableLiveData<CategoriesListResult> categoriesLiveData;

    public CategoriesBoardListViewModel(@NonNull Application application, ApiInterface apiInterface) {
        super(application);
        this.apiInterface = apiInterface;
    }


    public LiveData<CategoriesListResult> getCategories() {
        List<Category> categoryList = new ArrayList<>();
        if (categoriesLiveData == null) {
            categoriesLiveData = new MutableLiveData<>();
            categoriesLiveData.setValue(new CategoriesListResult(categoryList));

            // search categories selected by user on the shared pref screen
            searchCategories();
        }
        return categoriesLiveData;
    }


    private void checkCountryCodeInSharedPref() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());

        // Switches
        Boolean parredCountryCategoryBoard = preferences.getBoolean("key_switch_country_of_category_board", false);
        // Country code value
        categoriesCountryCode = preferences.getString("key_country_code_categories_boar", null);

        // LOGIC FOR SWITCHES
        // == country for searching categories with news for a specific country
        if (!parredCountryCategoryBoard) {
            categoriesCountryCode = "gb"; // this is a default value if users haven't set country by thyself
        }
    }


    private void searchCategories() {
        checkCountryCodeInSharedPref();
        // Check selected categories one by one and add result with image URL to categoriesList
        if (NewsApiUtils.Category.values().length > 0) {
            for (final NewsApiUtils.Category category : NewsApiUtils.Category.values()) {
                Boolean categorySelected = preferences.getBoolean("key_category_" + category.getCategory(), true);
                if (categorySelected) {
                    atLeastOneCategorySelected = true;
                }
                Call<News> call;
                if (categorySelected) {
                    call = apiInterface.topHeadlines(categoriesCountryCode, category.getCategory(), null, 20, null, API_KEY);
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
                                //CategoryBoardFragment.adapter.notifyDataSetChanged();

                            } else {
                                Log.i(TAG, "Fetching data failed for some reason :(");
                                //Toast.makeText(context, R.string.fetching_data_failed_message, Toast.LENGTH_SHORT).show();
                            }
                            CategoriesListResult categoriesListResult = new CategoriesListResult(categoryObjectList);
                            categoriesLiveData.setValue(categoriesListResult);
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
            Log.i(TAG, "No categories selected! Select at least one category");
            //Toast.makeText(context, R.string.no_categories_set_message, Toast.LENGTH_SHORT).show();
        }
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
            return (T) new CategoriesBoardListViewModel(application, apiInterface);
        }
    }
}
