package pl.michaldobrowolski.chronews.ui;

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

import java.util.Objects;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.CategoryBoardAdapter;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.CategoryFactory;

public class CategoryBoardFragment extends Fragment implements CategoryBoardAdapter.OnItemClickListener {
    private static final String TAG = CategoryBoardFragment.class.getClass().getSimpleName();
    public static RecyclerView.Adapter adapter;
    private CategoryFactory categoryFactory;
    private Context context;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        addCategoriesToScreen(gridLayoutManager, rootView);

        return rootView;
    }

    public void createCategories(ApiInterface apiInterface, Context context) {
        categoryFactory = new CategoryFactory(context, apiInterface);
        categoryFactory.createCategories();
    }

    public void addCategoriesToScreen(GridLayoutManager gridLayoutManager, View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.supportsPredictiveItemAnimations();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        createCategories(apiInterface, context);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryFactory.getCategoryObjectList().size() % 2 != 0) {
                    return (position == categoryFactory.getCategoryObjectList().size() - 1) ? 2 : 1;
                } else {
                    return 1;
                }
            }
        });

        adapter = new CategoryBoardAdapter(CategoryBoardFragment.this, context, categoryFactory.getCategoryObjectList());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Category category = categoryFactory.getCategoryObjectList().get(position);
        CategoryArticleListFragment categoryArticleListFragment = new CategoryArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("categoryArticleKey", category);
        categoryArticleListFragment.setArguments(bundle);

        Objects.requireNonNull(getFragmentManager(), "Fragment Manager must not be null")
                .beginTransaction()
                .replace(R.id.fragment_container, categoryArticleListFragment)
                .addToBackStack(null)
                .commit();
    }
}
