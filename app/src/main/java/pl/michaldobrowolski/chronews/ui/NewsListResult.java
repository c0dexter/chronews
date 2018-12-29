package pl.michaldobrowolski.chronews.ui;

import java.util.List;

import pl.michaldobrowolski.chronews.api.model.pojo.Article;

public class NewsListResult {
    private final List<Article> articleList;
    private final String searchPhrase;
    private final int errorCode;

    public NewsListResult(List<Article> articleList, String searchPhrase, int errorCode) {
        this.articleList = articleList;
        this.searchPhrase = searchPhrase;
        this.errorCode = errorCode;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
