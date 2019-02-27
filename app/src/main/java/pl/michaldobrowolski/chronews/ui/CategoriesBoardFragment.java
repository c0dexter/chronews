package pl.michaldobrowolski.chronews.ui;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.CategoryBoardAdapter;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class CategoriesBoardFragment
        extends Fragment implements CategoryBoardAdapter.OnItemClickListener {
    private static final String TAG = CategoriesBoardFragment.class.getClass().getSimpleName();
    public static RecyclerView.Adapter adapter;
    private CategoriesListResult categoriesListResult;
    private Context context;
    private CategoriesBoardListViewModel viewModel;
    private ApiInterface apiInterface;
    private GridLayoutManager gridLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = View.inflate(getActivity(), R.layout.fragment_categories_board, null);
        context = getActivity();
        if (context != null) {
            Objects.requireNonNull(((AppCompatActivity) context).getSupportActionBar()).hide();
        }
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        return rootView;
    }


    @Override
    public void onItemClick(View view, int position) {
        Category category = categoriesListResult.getCategoryList().get(position);
        CategoryArticleListFragment categoryArticleListFragment = new CategoryArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("categoryArticleKey", category);
        categoryArticleListFragment.setArguments(bundle);

        Objects.requireNonNull(getFragmentManager(),
                "Fragment Manager must not be null")
                .beginTransaction()
                .replace(R.id.fragment_container, categoryArticleListFragment)
                .addToBackStack(null)
                .commit();
    }


    public void addCategoriesToScreen(
            GridLayoutManager gridLayoutManager,
            View rootView,
            CategoriesListResult categoriesListResult) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.supportsPredictiveItemAnimations();
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoriesListResult.getCategoryList().size() % 2 != 0) {
                    return (position == categoriesListResult.getCategoryList().size() - 1) ? 2 : 1;
                } else {
                    return 1;
                }
            }
        });

        adapter = new CategoryBoardAdapter(
                CategoriesBoardFragment.this, context,
                categoriesListResult.getCategoryList());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Application application = getActivity().getApplication();

        viewModel = ViewModelProviders.of(this,
                new CategoriesBoardListViewModel.Factory(application,
                        apiInterface)).get(CategoriesBoardListViewModel.class);
        if ((UtilityHelper.isOnline(context))) {
            viewModel.getCategories()
                    .observe(this, (CategoriesListResult categoriesListResult) -> {
                        addCategoriesToScreen(gridLayoutManager, view, categoriesListResult);
                        this.categoriesListResult = categoriesListResult;
                    });
        } else {
            Toast.makeText(context, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
        }

    }
}
