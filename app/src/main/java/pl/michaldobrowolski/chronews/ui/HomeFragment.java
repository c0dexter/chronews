package pl.michaldobrowolski.chronews.ui;

import android.app.Application;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
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

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.SharedPreferences.SettingsActivity;
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class HomeFragment extends Fragment implements ArticleListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String SEARCH_PHRASE_KEY = "search_key";
    private static final String TOOLBAR_SUBTITLE_KEY = "subtitle_key";
    private static final String TOOLBAR_TITLE_KEY = "title_key";
    private Context context;
    private RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private RecyclerView.Adapter adapter;
    private Toolbar toolbar;
    private Boolean isManualSearch = false;
    private LinearLayout settingsTopHeadlinesNotifier;
    private String searchText;
    private SearchView searchView;
    private NewsListViewModel viewModel;
    private String toolbarTitleText;
    private String toolbarSubtitleText = null;

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

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_PHRASE_KEY);
            toolbarSubtitleText = savedInstanceState.getString(TOOLBAR_SUBTITLE_KEY);
            toolbarTitleText = savedInstanceState.getString(TOOLBAR_TITLE_KEY);
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

        fetchArticles(null, isManualSearch);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void fetchArticles(@Nullable String searchedPhrase, @Nullable Boolean isManualSearch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String phraseForSearchingTopHeadlines;
        Boolean preferredLanguageSwitch = preferences.getBoolean("key_switch_specific_news_language", false);
        Boolean preferredCountrySwitch = preferences.getBoolean("key_switch_country_top_headlines", false);
        Boolean preferredTopHeadlinesSwitch = preferences.getBoolean("key_switch_top_headlines_home_screen", false);
        Boolean preferredTopHeadlinesCategorySwitch = preferences.getBoolean("key_switch_category_top_headlines", false);
        Boolean preferredTopHeadlinesSpecificPhraseSwitch = preferences.getBoolean("key_switch_search_phrase_top_headlines", false);

        // Toolbar title and subtitle
        if (toolbarTitleText == null || toolbarTitleText.equals("")) {
            toolbar.setTitle(UtilityHelper.makeUpperString(getString(R.string.top_headlines_title)));
        } else {
            toolbar.setTitle(toolbarTitleText);
        }
        if (toolbarSubtitleText == null || toolbarSubtitleText.equals("")) {
            setSubTitle(preferredTopHeadlinesCategorySwitch, preferredTopHeadlinesSpecificPhraseSwitch, preferredTopHeadlinesSwitch, preferredCountrySwitch, preferences);
        } else {
            toolbar.setSubtitle(toolbarSubtitleText);
        }

        // TOP HEADLINES - DEFAULT SEARCH LOGIC
        if (preferredTopHeadlinesSwitch) {
            // Check selected country code
            settingsTopHeadlinesNotifier.setVisibility(View.GONE);
            // Check switch for searching news by phrase
            // get a search phrase form shared pref and assign value to searched phrase
            if (preferredTopHeadlinesSpecificPhraseSwitch)
                phraseForSearchingTopHeadlines = preferences.getString("key_default_phrase_top_headlines", null);
        } else {
            settingsTopHeadlinesNotifier.setVisibility(View.VISIBLE);
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

        MenuItem menuItem = menu.findItem(R.id.action_bar_search);
        searchView = (SearchView) menu.findItem(R.id.action_bar_search).getActionView();

        //focus the SearchView
        if (searchText != null && !searchText.isEmpty()) {
            menuItem.expandActionView();
            searchView.onActionViewExpanded();
            searchView.setQuery(searchText, true);
            searchView.clearFocus();
        } else {
            searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getActivity().getComponentName()) : null);
            searchView.setQueryHint(getString(R.string.search_news_hint));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);
                searchView.clearFocus();

                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                isManualSearch = true;
                Boolean searchInSpecificLanguage = preferences.getBoolean(
                        "key_switch_specific_news_language", false);
                String language = preferences.getString("key_language_code", null);

                viewModel.searchArticlesBySearchPhrase(query);// TODO; check this, here is populated search phrase
                settingsTopHeadlinesNotifier.setVisibility(View.GONE);

                toolbar.setTitle(R.string.sear_result_name);
                if (searchInSpecificLanguage) {
                    if (language != null) {
                        toolbar.setSubtitle(getString(R.string.search_for_phrase_part_name) + " " +
                                query + "\" (" + language.toUpperCase() + ")");
                    }
                } else {
                    toolbar.setSubtitle(getString(R.string.search_for_phrase_part_name) + query);
                }

                isManualSearch = false;
                // collapse the action view
                (menu.findItem(R.id.action_bar_search)).collapseActionView();
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                // Leave as is, this feature could be use in a new version of app
                // This method can execute search after passing letter one by one
                // For free plan of API this is dangerous because of generating lot of calls
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
        // TODO: Here is executing a DEFAULT search for TOP-HEADLINES
        Article article = viewModel.getArticles().getValue().getArticleList().get(position);
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
        Application application = getActivity().getApplication();

        viewModel = ViewModelProviders.of(this, new NewsListViewModel.Factory(application,
                apiInterface)).get(NewsListViewModel.class);
        // TODO: Here is executing the TOP-HEADLINES call
        viewModel.getArticles().observe(this, (NewsListResult newsListResult) -> {
            int errorCode = 0;
            if (newsListResult != null) {
                errorCode = newsListResult.getErrorCode();
            }

            switch (errorCode) {
                case -1: {
                    // do nothing, this is a default value for initialization
                    break;
                }
                case 200: {
                    adapter = new ArticleListAdapter(newsListResult.getArticleList(), context, HomeFragment.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // Check if API returns any of articles for provided query criteria. If not, inform a user.
                    if (newsListResult.getArticleList().isEmpty()) {
                        Toast.makeText(context, getString(R.string.api_code_200_but_no_results), Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 400: {
                    Toast.makeText(context, getString(R.string.api_error_400_msg), Toast.LENGTH_LONG).show();
                    break;
                }
                case 401: {
                    Toast.makeText(context, getString(R.string.api_error_401_msg), Toast.LENGTH_LONG).show();
                    break;
                }
                case 429: {
                    Toast.makeText(context, getString(R.string.api_error_429_msg), Toast.LENGTH_LONG).show();
                    break;
                }
                case 500: {
                    Toast.makeText(context, getString(R.string.api_error_500_msg),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {
                    Toast.makeText(context, getString(R.string.api_unknkown_error_msg) + " " + errorCode,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSubTitle(Boolean preferredTopHeadlinesCategorySwitch, Boolean preferredTopHeadlinesSpecificPhraseSwitch, Boolean preferredTopHeadlinesSwitch, Boolean preferredCountrySwitch, SharedPreferences preferences) {

        if (preferredTopHeadlinesSwitch) {
            String country = preferences.getString("key_country_code_top_headlines", null);
            String category = preferences.getString("key_top_headlines_category", null);
            String phrase = preferences.getString("key_default_phrase_top_headlines", null);
            if (preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && preferredTopHeadlinesSpecificPhraseSwitch) {
                if (country != null) {
                    if (category != null) {
                        toolbarSubtitleText = UtilityHelper.makeUpperString(category) + " " + getString(R.string.subtitle_part_name_from) + " " + country.toUpperCase() + " (" + phrase + ")";
                        toolbar.setSubtitle(toolbarSubtitleText);
                    }
                }
            } else if (preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && !preferredTopHeadlinesSpecificPhraseSwitch) {
                if (category != null) {
                    if (country != null) {
                        toolbarSubtitleText = UtilityHelper.makeUpperString(category) + " " + getString(R.string.subtitle_part_name_from) + " " + country.toUpperCase();
                        toolbar.setSubtitle(toolbarSubtitleText);
                    }
                }
            } else if (!preferredTopHeadlinesCategorySwitch && preferredCountrySwitch && !preferredTopHeadlinesSpecificPhraseSwitch) {
                if (country != null) {
                    toolbarSubtitleText = getString(R.string.subtile_name_start_name_from) + " " + country.toUpperCase();
                    toolbar.setSubtitle(toolbarSubtitleText);
                }
            } else {
                toolbarSubtitleText = getString(R.string.subtitle_search_hint_message);
                toolbar.setSubtitle(toolbarSubtitleText);
            }
        } else {
            toolbarSubtitleText = getString(R.string.subtitle_feature_disabled);
            toolbar.setSubtitle(toolbarSubtitleText);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        searchText = searchView.getQuery().toString();
        toolbarTitleText = toolbar.getTitle().toString();
        toolbarSubtitleText = toolbar.getSubtitle().toString();

        outState.putString(SEARCH_PHRASE_KEY, searchText);
        outState.putString(TOOLBAR_TITLE_KEY, toolbarTitleText);
        outState.putString(TOOLBAR_SUBTITLE_KEY, toolbarSubtitleText);
    }
}
