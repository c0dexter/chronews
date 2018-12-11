package pl.michaldobrowolski.chronews.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.NewsApiUtils;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryArticleListFragment extends Fragment implements ArticleListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String API_KEY = BuildConfig.ApiKey;
    private Context context;
    private RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private RecyclerView.Adapter adapter;
    private News news;
    private Category category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_article_list, container, false);
        context = getActivity();
        if (context != null) {
            Objects.requireNonNull(((AppCompatActivity) context).getSupportActionBar()).show();
        }

        Toolbar toolbar = Objects.requireNonNull(getActivity(), "Context must not be null").findViewById(R.id.main_activity_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        context = getContext();
        recyclerView = rootView.findViewById(R.id.recycler_view_category_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category = bundle.getParcelable("categoryArticleKey");
        }

        if (category != null) {
            toolbar.setTitle(UtilityHelper.makeUpperString(category.getCategoryName()));
            fetchArticles(category.getCategoryName()); // TODO: get info from the bundle
        }
        return rootView;
    }

    private void fetchArticles(final String category) {

        Call<News> call;
        String country = NewsApiUtils.CountryCodes.UNITED_KINGDOM.getCountryCode();
        String categoryType = NewsApiUtils.Category.ENTERTAINMENT.getCategory();
        String sortingType = NewsApiUtils.SortOption.POPULARITY.getSortingOption(); // TODO: options for search has to be moved to SharedPref

        call = apiInterface.topHeadlines(country, category, null, null, null, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response Code: " + response.code());
                    news = response.body();
                    //jasonRetrofitResult = new Gson().toJson(news);
                    adapter = new ArticleListAdapter(news.getArticles(), context, CategoryArticleListFragment.this);
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
        inflater = Objects.requireNonNull(getActivity(), "Context must not be null").getMenuInflater();
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
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "TOAST on position: #" + String.valueOf(position), Toast.LENGTH_SHORT).show();

        Article article = news.getArticles().get(position);
        ArticleDetailFragment articleDetailFragment = new ArticleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("articleKey", article);
        articleDetailFragment.setArguments(bundle);

        Objects.requireNonNull(getFragmentManager(), "FragmentManager must not be null")
                .beginTransaction()
                .replace(R.id.fragment_container, articleDetailFragment)
                .addToBackStack(null)
                .commit();
    }

}