package pl.michaldobrowolski.chronews.ui;

import java.util.List;

import pl.michaldobrowolski.chronews.api.model.pojo.Article;

class CategoryArticlesListResult {
    private final List<Article> articleList;
    private final int errorCode;

    CategoryArticlesListResult(List<Article> articleList, int errorCode) {
        this.articleList = articleList;
        this.errorCode = errorCode;
    }

    List<Article> getArticleList() {
        return articleList;
    }

    int getErrorCode() {
        return errorCode;
    }
}
