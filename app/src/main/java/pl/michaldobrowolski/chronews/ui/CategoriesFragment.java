package pl.michaldobrowolski.chronews.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.CategoryListAdapter;
import pl.michaldobrowolski.chronews.utils.CategoryFactory;

public class CategoriesFragment extends Fragment implements CategoryListAdapter.OnItemClickListener {
    private static final String TAG = CategoriesFragment.class.getClass().getSimpleName();
    public static RecyclerView.Adapter adapter;
    private CategoryFactory categoryFactory;
    private Context context;

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, null);
        context = getActivity();
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

        adapter = new CategoryListAdapter(CategoriesFragment.this, context, categoryFactory.getCategoryObjectList());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "TOAST on position: #" + String.valueOf(position), Toast.LENGTH_SHORT).show();
    }
}
