package pl.michaldobrowolski.chronews.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.News;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.api.service.ApiInterface;
import pl.michaldobrowolski.chronews.ui.adapters.CategoryListAdapter;
import pl.michaldobrowolski.chronews.utils.NewsApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment implements CategoryListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String API_KEY = BuildConfig.ApiKey;
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private NewsApiUtils.Category category;
    private ApiInterface apiInterface;
    private News news;
    private List<NewsApiUtils.CategoryObject> categoryObjectList;
    private String urlToThumbnail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, null);
        context = getContext();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView = rootView.findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        categoryObjectList = new ArrayList<>();
        createCategories();

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryObjectList.size() % 2 != 0) {
                    return (position == categoryObjectList.size() - 1) ? 2 : 1;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    private void createCategories() {
        if (NewsApiUtils.Category.values().length >= 0) {
            for (NewsApiUtils.Category category : NewsApiUtils.Category.values()) {
                getCategoryThumbnail(category);
                categoryObjectList.add(new NewsApiUtils.CategoryObject(category.getCategory(), urlToThumbnail));
            }

            adapter = new CategoryListAdapter(CategoriesFragment.this, context, categoryObjectList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, "No categories defined", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCategoryThumbnail(NewsApiUtils.Category category) {
        Call<News> call;
        call = apiInterface.topHeadlines(null, category.getCategory(), null, 1, null, API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response Code: " + response.code());
                    news = response.body();
                    urlToThumbnail = news.getArticles().get(0).getUrlToImage();
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
        Log.d(TAG, "URL to IMAGE: " + urlToThumbnail);
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "TOAST on position: #" + String.valueOf(position), Toast.LENGTH_SHORT).show();
    }
}
