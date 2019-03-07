package pl.michaldobrowolski.chronews.ui;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class CategoryArticleListFragment extends Fragment implements ArticleListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String TOOLBAR_SUBTITLE_KEY = "subtitle_key";
    private static final String TOOLBAR_TITLE_KEY = "title_key";
    private Context context;
    private RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private RecyclerView.Adapter adapter;
    private Category category;
    private CategoryArticlesListViewModel viewModel;
    private CategoryArticlesListResult categoryArticlesListResult;
    private SharedPreferences preferences;
    private String toolbarTitleText;
    private String toolbarSubtitleText = null;
    private Toolbar toolbar;


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

        if (savedInstanceState != null) {
            toolbarSubtitleText = savedInstanceState.getString(TOOLBAR_SUBTITLE_KEY);
            toolbarTitleText = savedInstanceState.getString(TOOLBAR_TITLE_KEY);
        }

        toolbar = Objects.requireNonNull(getActivity(), "Context must not be null").findViewById(R.id.main_activity_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
            toolbar.setNavigationIcon(null);
        });

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
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Boolean showArticlesForSpecificCountry = preferences.getBoolean("key_switch_country_of_category_board", false);
            String country = preferences.getString("key_country_code_categories_board", "gb");
            toolbar.setTitle(UtilityHelper.makeUpperString(category.getCategoryName()));

            if (showArticlesForSpecificCountry) {
                toolbar.setSubtitle(getString(R.string.top_headlines_specific_country_selected_toolbar) + " " + country.toUpperCase());
            } else {
                toolbar.setSubtitle(R.string.default_country_set_category_article_list_message);
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        Article article = categoryArticlesListResult.getArticleList().get(position);
        ArticleDetailFragment articleDetailFragment = new ArticleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("articleKey", article);
        bundle.putString("titleNameKey", category.getCategoryName());
        articleDetailFragment.setArguments(bundle);

        Objects.requireNonNull(getFragmentManager(), "FragmentManager must not be null")
                .beginTransaction()
                .replace(R.id.fragment_container, articleDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Application application = getActivity().getApplication();

        viewModel = ViewModelProviders.of(this, new CategoryArticlesListViewModel.Factory(application,
                apiInterface)).get(CategoryArticlesListViewModel.class);

        if ((UtilityHelper.isOnline(context))) {
            viewModel.getArticles(category.getCategoryName()).observe(this, (CategoryArticlesListResult categoryArticlesListResult) -> {
                this.categoryArticlesListResult = categoryArticlesListResult;
                int errorCode = 0;
                if (categoryArticlesListResult != null) {
                    errorCode = categoryArticlesListResult.getErrorCode();
                }

                switch (errorCode) {
                    case -1: {
                        // do nothing, this is a default value for initialization
                        break;
                    }
                    case 200: {
                        adapter = new ArticleListAdapter(categoryArticlesListResult.getArticleList(), context, CategoryArticleListFragment.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        // Check if API returns any of articles for provided query criteria. If not, inform a user.
                        if (categoryArticlesListResult.getArticleList().isEmpty()) {
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

        } else {
            Toast.makeText(context, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        toolbarTitleText = toolbar.getTitle().toString();
        toolbarSubtitleText = toolbar.getSubtitle().toString();

        outState.putString(TOOLBAR_TITLE_KEY, toolbarTitleText);
        outState.putString(TOOLBAR_SUBTITLE_KEY, toolbarSubtitleText);
    }
}
