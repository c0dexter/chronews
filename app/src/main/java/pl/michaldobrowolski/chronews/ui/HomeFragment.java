package pl.michaldobrowolski.chronews.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.SharedPreferences.SettingsActivity;
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ArticleListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String API_KEY = BuildConfig.ApiKey;
    private Context context;
    private RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private RecyclerView.Adapter adapter;
    private News news;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        if (context != null) {
            Objects.requireNonNull(((AppCompatActivity) context).getSupportActionBar()).show();
        }
        toolbar = Objects.requireNonNull(getActivity(), "Activity context must not be null").findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle(R.string.app_name);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);


        recyclerView = rootView.findViewById(R.id.recycler_view_home);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        fetchArticles(null); // TODO: (FEATURE) get info from shared pref

        return rootView;
    }


    public void fetchArticles(@Nullable String searchedPhrase) {
        toolbar.setTitle(UtilityHelper.makeUpperString("top headlines"));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String countryCode;
        String languageCode;
        String sortingType = preferences.getString("key_sorting_code", null);
        String topHeadlinesCategory = preferences.getString("key_top_headlines_category", null);
        String phraseForSearchingTopHeadlines;
        Boolean preferredLanguageSwitch = preferences.getBoolean("key_switch_specific_news_language", false);
        Boolean preferredTopHeadlinesSwitch = preferences.getBoolean("key_switch_top_headlines_home_screen", false);
        Boolean preferredTopHeadlinesCategorySwitch = preferences.getBoolean("key_switch_category_top_headlines", false);
        Boolean preferredTopHeadlinesSpecificPhraseSwitch = preferences.getBoolean("key_switch_search_phrase_top_headlines", false);

        if (preferredLanguageSwitch == true) {
            languageCode = preferences.getString("key_language_code", null);
        } else {
            languageCode = null;
        }

        // TOP HEADLINES - DEFAULT SEARCH LOGIC
        if (preferredTopHeadlinesSwitch == true) {
            // Check selected country code
            countryCode = preferences.getString("key_country_code_top_headlines", null);

            // Check switch for displaying top headlines for one specific category
            if (preferredTopHeadlinesCategorySwitch == true) {
                // Select country code to build a call
                topHeadlinesCategory = preferences.getString("key_top_headlines_category", null);
            } else {
                // Set country code as null
                topHeadlinesCategory = null;
            }

            // Check switch for searching news by phrase
            if (preferredTopHeadlinesSpecificPhraseSwitch == true) {
                // get a search phrase form shared pref and assign value to searched phrase
                phraseForSearchingTopHeadlines = preferences.getString("key_default_phrase_top_headlines", null);
                searchedPhrase = phraseForSearchingTopHeadlines;
                // set country as null, because API can return a global top headlines for some phrase only
                countryCode = null;
            } else {
                //searchedPhrase = null;
            }
        } else {
            countryCode = null;
        }


        Call<News> call;
        if (searchedPhrase != null && !Objects.equals(searchedPhrase, "")) {
            // Use search phrase and display everything what is related to the phrase
            call = apiInterface.everything(searchedPhrase, languageCode, sortingType, API_KEY);
        } else {
            // Show top headlines
            call = apiInterface.topHeadlines(countryCode, topHeadlinesCategory, searchedPhrase, null, null, API_KEY);
        }
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response Code: " + response.code());
                    news = response.body();
                    //jasonRetrofitResult = new Gson().toJson(news);
                    adapter = new ArticleListAdapter(news.getArticles(), context, HomeFragment.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error. Fetching data failed :(", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Fetching data failed! Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = Objects.requireNonNull(getActivity(), "Activity Context must not be null").getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_bar_search).getActionView();
        MenuItem menuItem = menu.findItem(R.id.action_bar_search);

        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getActivity().getComponentName()) : null);
        searchView.setQueryHint(getString(R.string.search_news_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchArticles(query); // TODO: make switch-case block for selected search type in the Settings
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                return false;
            }
        });

        menuItem.getIcon().setVisible(false, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionSettings) {
            // launch settings activity
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "TOAST on position: #" + String.valueOf(position), Toast.LENGTH_SHORT).show();

        Article article = news.getArticles().get(position);
        ArticleDetailFragment articleDetailFragment = new ArticleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("articleKey", article);
        articleDetailFragment.setArguments(bundle);

        Objects.requireNonNull(getFragmentManager(), "Fragment Manager must not be null")
                .beginTransaction()
                .replace(R.id.fragment_container, articleDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
