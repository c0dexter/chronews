package pl.michaldobrowolski.chronews.api.service;

import java.util.List;

import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.model.pojo.Source;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // Here are populated endpoints only with proper methods (GET/POST/PUT)
    // Call to the proper endpoint by GET method (because of getting info only)

    @GET("top-headlines")
    Call<News> topHeadlines( // why not Call<List<Article>> ??
            @Query("country") String countryCode,
            @Query("category") String categoryType,
            @Query("q") String queryPhrase,
            @Query("pageSize") Integer amountResultsPerPage,
            @Query("page") Integer pagePaginationAmount,
            @Query("apiKey") String apiKeyValue
    );

    @GET("everything")
    Call<News> everything(
            @Query("q") String searchedPhrase,
            @Query("language") String languageCode,
            @Query("sortBy") String sortingType,
            @Query("apiKey") String apiKeyValue
    );

    @GET("sources")
    Call<Source> sources(
            @Query("category") String categoryType,
            @Query("language") String languageCode,
            @Query("country") String countryCode,
            @Query("apiKey") String apiKeyValue
    );
}
