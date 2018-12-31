package pl.michaldobrowolski.chronews.ui;

import java.util.List;

import pl.michaldobrowolski.chronews.api.data.ArticleEntity;

public class FavouriteArticlesListResult {
    private final List<ArticleEntity> favouriteArticleList;

    public FavouriteArticlesListResult(List<ArticleEntity> favouriteArticleList) {
        this.favouriteArticleList = favouriteArticleList;
    }

    public List<ArticleEntity> getFavouriteArticleList() {
        return favouriteArticleList;
    }
}
