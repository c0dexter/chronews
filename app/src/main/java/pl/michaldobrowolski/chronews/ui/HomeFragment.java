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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
    private Boolean isManualSearch = false;
    private LinearLayout settingsTopHeadlinesNotifier;

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

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                if (isAdded()) {
                    Intent intent = new Intent(context, GoogleSignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        recyclerView = rootView.findViewById(R.id.recycler_view_home);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        settingsTopHeadlinesNotifier = rootView.findViewById(R.id.home_top_headlines_feature_disable_notifier);

                // Show top headlines articles based on Shared Pref settings
        fetchArticles(null, isManualSearch);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void fetchArticles(@Nullable String searchedPhrase, @Nullable Boolean isManualSearch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String countryCode;
        String languageCode;
        String sortingType = preferences.getString("key_sorting_code", null);
        String topHeadlinesCategory = preferences.getString("key_top_headlines_category", null);
        String phraseForSearchingTopHeadlines;
        Boolean preferredLanguageSwitch = preferences.getBoolean("key_switch_specific_news_language", false);
        Boolean preferredCountrySwitch = preferences.getBoolean("key_switch_country_top_headlines", false);
        Boolean preferredTopHeadlinesSwitch = preferences.getBoolean("key_switch_top_headlines_home_screen", false);
        Boolean preferredTopHeadlinesCategorySwitch = preferences.getBoolean("key_switch_category_top_headlines", false);
        Boolean preferredTopHeadlinesSpecificPhraseSwitch = preferences.getBoolean("key_switch_search_phrase_top_headlines", false);

        toolbar.setTitle(UtilityHelper.makeUpperString(getString(R.string.top_headlines_title)));
        setSubTitle(preferredTopHeadlinesCategorySwitch, preferredTopHeadlinesSpecificPhraseSwitch, preferredTopHeadlinesSwitch, preferredCountrySwitch, preferences);


        if (preferredLanguageSwitch) {
            languageCode = preferences.getString("key_language_code", null);
        } else {
            languageCode = null;
        }

        // TOP HEADLINES - DEFAULT SEARCH LOGIC
        if (preferredTopHeadlinesSwitch) {
            // Check selected country code
            settingsTopHeadlinesNotifier.setVisibility(View.GONE);
            // Check switch for displaying top headlines for one specific category
            if (preferredCountrySwitch) {
                // Select country code to build a call
                countryCode = preferences.getString("key_country_code_top_headlines", null);
            } else {
                // Set country code as null
                countryCode = null;
            }

            // Check switch for displaying top headlines for one specific category
            if (preferredTopHeadlinesCategorySwitch) {
                // Select category code to build a call
                topHeadlinesCategory = preferences.getString("key_top_headlines_category", null);
            } else {
                // Set country code as null
                topHeadlinesCategory = null;
            }

            // Check switch for searching news by phrase
            if (preferredTopHeadlinesSpecificPhraseSwitch) {
                // get a search phrase form shared pref and assign value to searched phrase
                phraseForSearchingTopHeadlines = preferences.getString("key_default_phrase_top_headlines", null);
                if (!isManualSearch) {
                    searchedPhrase = phraseForSearchingTopHeadlines;
                }
            }
        } else {
            countryCode = null;
            topHeadlinesCategory = null;
            settingsTopHeadlinesNotifier.setVisibility(View.VISIBLE);
        }

            if (UtilityHelper.isOnline(context)) {
                Call<News> call = null;
                if (searchedPhrase != null && !Objects.equals(searchedPhrase, "")) {
                    // Use search phrase and display everything what is related to the phrase
                    if (isManualSearch) {
                        call = apiInterface.everything(searchedPhrase, languageCode, sortingType, API_KEY);
                        settingsTopHeadlinesNotifier.setVisibility(View.GONE);
                    } else {
                        call = apiInterface.topHeadlines(countryCode, topHeadlinesCategory, searchedPhrase, null, null, API_KEY);
                    }
                } else if (!isManualSearch) {
                    call = apiInterface.topHeadlines(countryCode, topHeadlinesCategory, searchedPhrase, null, null, API_KEY);
                }

                if(isManualSearch || preferredTopHeadlinesSwitch){
                    if (call != null) {
                        call.enqueue(new Callback<News>() {
                            @Override
                            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    news = response.body();
                                    adapter = new ArticleListAdapter(news.getArticles(), context, HomeFragment.this);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    if(news.getArticles().size()==0){
                                        Toast.makeText(context, R.string.top_headlines_no_results_extend_search_criteria, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, R.string.fetching_data_failed_change_settings, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                }

            } else {
                Toast.makeText(context, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                isManualSearch = true;
                Boolean searchInSpecificLanguage = preferences.getBoolean("key_switch_specific_news_language", false);
                String language = preferences.getString("key_language_code", null);
                fetchArticles(query, true); // TODO: make switch-case block for selected search type in the Settings
                toolbar.setTitle("Search results");
                if (searchInSpecificLanguage) {
                    if (language != null) {
                        toolbar.setSubtitle("for phrase: \"" + query + "\" (" + language.toUpperCase() + ")");
                    }
                } else {
                    toolbar.setSubtitle("for phrase: " + query);
                }

                isManualSearch = false;
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
        switch (id) {
            case R.id.actionSettings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.actionLogout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context, R.string.log_out_message, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }



    private void setSubTitle(Boolean preferredTopHeadlinesCategorySwitch, Boolean preferredTopHeadlinesSpecificPhraseSwitch, Boolean preferredTopHeadlinesSwitch, Boolean preferredCountrySwitch, SharedPreferences preferences) {
        if (preferredTopHeadlinesSwitch) {
            String country = preferences.getString("key_country_code_top_headlines", null);
            String category = preferences.getString("key_top_headlines_category", null);
            String phrase = preferences.getString("key_default_phrase_top_headlines", null);
            if (preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && preferredTopHeadlinesSpecificPhraseSwitch) {
                if (country != null) {
                    if (category != null) {
                        toolbar.setSubtitle(UtilityHelper.makeUpperString(category) + " from " + country.toUpperCase() + " (" + phrase + ")");
                    }
                }
            } else if (preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && !preferredTopHeadlinesSpecificPhraseSwitch) {
                if (category != null) {
                    if (country != null) {
                        toolbar.setSubtitle(UtilityHelper.makeUpperString(category) + " from " + country.toUpperCase());
                    }
                }
            } else if (!preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && !preferredTopHeadlinesSpecificPhraseSwitch) {
                if (country != null) {
                    toolbar.setSubtitle("From " + country.toUpperCase());
                }
            } else {
                toolbar.setSubtitle(R.string.subtitle_search_hint_message);
            }
        } else {
            toolbar.setSubtitle(R.string.subtitle_feature_disabled);
        }
    }
}
