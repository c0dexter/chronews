package pl.michaldobrowolski.chronews.ui;

import android.content.Context;
import android.os.Bundle;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import pl.michaldobrowolski.chronews.BuildConfig;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.ArticleRepository;
import pl.michaldobrowolski.chronews.api.service.ApiClient;
import pl.michaldobrowolski.chronews.ui.adapters.FavouriteListAdapter;

public class FavouriteFragment extends Fragment implements FavouriteListAdapter.OnItemClickListener {
    private static final String TAG = ApiClient.class.getClass().getSimpleName();
    private static final String API_KEY = BuildConfig.ApiKey;
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArticleEntity articleEntity;
    private List<ArticleEntity> dbArticlesList;
    private Toolbar toolbar;
    private ArticleRepository articleRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        context = getActivity();
        if (context != null) {
            Objects.requireNonNull(((AppCompatActivity) context).getSupportActionBar()).show();
        }
        toolbar = Objects.requireNonNull(getActivity(), "Activity context must not be null").findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle(R.string.toolbar_title_favourites);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        articleRepository = new ArticleRepository(getContext());
        recyclerView = rootView.findViewById(R.id.recycler_view_favourites);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        dbArticlesList = getDbArticlesList();
        adapter = new FavouriteListAdapter(FavouriteFragment.this, dbArticlesList, articleRepository, context);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


//    private void setAdapter(RecyclerView.Adapter myAdapter, List<ArticleEntity> articleEntitiesList) {
//        myAdapter = new FavouriteListAdapter(FavouriteFragment.this, articleEntitiesList, context);
//        recyclerView.setAdapter(adapter);
//        myAdapter.notifyDataSetChanged();
//    }


    /**
     * Get all articles from Database
     *
     * @return ArticlesEntity list which contains saved articles
     */
    private List<ArticleEntity> getDbArticlesList() {
        try {
            dbArticlesList = articleRepository.getAllArticles();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return dbArticlesList;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "Clicked aricle on: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
