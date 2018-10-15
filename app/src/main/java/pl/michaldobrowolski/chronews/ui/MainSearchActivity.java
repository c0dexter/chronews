package pl.michaldobrowolski.chronews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSearchActivity extends AppCompatActivity {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String API_KEY = ""; // TODO: remove API Key before commit!!!

    private TextView mTextMessage;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private News news;
    private ApiInterface apiInterface;
    private String jasonRetrofitResult;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_search);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_categories);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_favourites);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        fetchTopHeadlinesArticles();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void fetchTopHeadlinesArticles() {
        Call<News> call = apiInterface.topHeadlines("pl", null, null, null, null, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.body() != null) {
                    Log.d("LOG: Response Code: ", response.code() + "");
                    //fetchingData(response);
                    news = response.body();
                    jasonRetrofitResult = new Gson().toJson(news);
                    adapter = new ArticleListAdapter(news.getArticles(), MainSearchActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Error. Fetching data failed :(", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void fetchEverythingArticles() {
        Call<News> call = apiInterface.everything("bitcoin", null, null, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.body() != null) {
                    Log.d("LOG: Response Code: ", response.code() + "");
                    //fetchingData(response);
                    news = response.body();
                    jasonRetrofitResult = new Gson().toJson(news);
                    adapter = new ArticleListAdapter(news.getArticles(), MainSearchActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Error. Fetching data failed :(", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

}
