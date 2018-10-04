package pl.michaldobrowolski.chronews.api.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // Here are populated endpoints only with proper methods (GET/POST/PUT)
    // Call to the proper endpoint by GET method (because of getting info only)

    @GET("top-headlines")
    Call<ResponseBody> topHeadlines(
            @Query("country") String countryCode,
            @Query("category") String categoryType,
            @Query("q") String queryPhrase,
            @Query("pageSize") Integer amountResultsPerPage,
            @Query("page") Integer pagePaginationAmount
    );

    @GET("everything")
    Call<ResponseBody> everything(
            @Query("q") String queryPhrase,
            @Query("language") String languageCode,
            @Query("sortBy") String sortingType
    );

    @GET("sources")
    Call<ResponseBody> sources(
            @Query("category") String categoryType,
            @Query("language") String languageCode,
            @Query("country") String countryCode
    );

}
