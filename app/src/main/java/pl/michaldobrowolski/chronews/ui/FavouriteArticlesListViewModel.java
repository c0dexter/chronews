package pl.michaldobrowolski.chronews.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.FavouriteArticleRepository;

class FavouriteArticlesListViewModel extends AndroidViewModel {
    private FavouriteArticleRepository favouriteArticleRepository;
    private List<ArticleEntity> dbArticlesList;


    // LiveData
    private MutableLiveData<FavouriteArticlesListResult> favouritesLiveData;

    private FavouriteArticlesListViewModel(@NonNull Application application, FavouriteArticleRepository favouriteArticleRepository) {
        super(application);
        this.favouriteArticleRepository = favouriteArticleRepository;
    }


    LiveData<FavouriteArticlesListResult> getFavouriteArticlesFromDb() {
        List<ArticleEntity> articleEntityArrayList = new ArrayList<>();
        if (favouritesLiveData == null) {
            favouritesLiveData = new MutableLiveData<>();
            favouritesLiveData.setValue(new FavouriteArticlesListResult(articleEntityArrayList));

            // load stored articles from database
            getStoredData();
        }
        return favouritesLiveData;
    }


    private void getStoredData() {
        try {
            dbArticlesList = favouriteArticleRepository.getAllArticles();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        FavouriteArticlesListResult favouriteArticlesListResult = new FavouriteArticlesListResult(dbArticlesList);
        favouritesLiveData.setValue(favouriteArticlesListResult);
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final FavouriteArticleRepository favouriteArticleRepository;
        @NonNull
        private final Application application;

        Factory(@NonNull Application application, FavouriteArticleRepository favouriteArticleRepository) {
            this.favouriteArticleRepository = favouriteArticleRepository;
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FavouriteArticlesListViewModel(application, favouriteArticleRepository );
        }
    }
}
