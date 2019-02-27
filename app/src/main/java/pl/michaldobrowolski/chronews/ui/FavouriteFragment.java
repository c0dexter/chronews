package pl.michaldobrowolski.chronews.ui;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.FavouriteArticleRepository;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.api.model.pojo.Source;
import pl.michaldobrowolski.chronews.ui.adapters.FavouriteListAdapter;

public class FavouriteFragment extends Fragment implements FavouriteListAdapter.OnItemClickListener {
    private static final String TAG = FavouriteFragment.class.getClass().getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FavouriteArticleRepository favouriteArticleRepository;
    private FavouriteArticlesListResult favouriteArticlesListResult;
    private OnRemoveArticleListener onRemoveArticleListener;


    public interface OnRemoveArticleListener {
        //Callback for when "Remove all (fav)" button clicked.
        void onRemoveArticleButtonClickedCallback();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its onRemoveArticleListener
        onRemoveArticleListener = (OnRemoveArticleListener) activity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        setHasOptionsMenu(true);
        context = getActivity();
        if (context != null) {
            Objects.requireNonNull(((AppCompatActivity) context).getSupportActionBar()).show();
        }

        Toolbar toolbar = Objects.requireNonNull(getActivity(),
                "Activity context must not be null")
                .findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle(R.string.toolbar_title_favourites);
        toolbar.setSubtitle(R.string.favourite_scrn_subtitle);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        favouriteArticleRepository = new FavouriteArticleRepository(getContext());
        recyclerView = rootView.findViewById(R.id.recycler_view_favourites);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = Objects.requireNonNull(getActivity(),
                "Activity Context must not be null").getMenuInflater();
        inflater.inflate(R.menu.menu_fav_screen, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionShareAllFav:
                shareAllFavArticleUrls(favouriteArticlesListResult);
                return true;

            case R.id.actionRemoveAllFav:
                new AlertDialog.Builder(context)
                        //TODO: Refactor: move strings to string.xml file, make tests
                        .setTitle("Are you sure?")
                        .setMessage("Note that all favourite articles will be removed permanently.")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAllFavArticles();
                                onRemoveArticleListener.onRemoveArticleButtonClickedCallback();
                                Toast.makeText(context, "Favourite articles removed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "Removing articles canelled");
                            }
                        })
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(View view, int position) {
        ArticleEntity articleEntity = favouriteArticlesListResult.getFavouriteArticleList().get(position);
        Article article = new Article();
        Source source = new Source();
        source.setName(articleEntity.getSourceName());
        article.setUrlToImage(articleEntity.getImageUrl());
        article.setUrl(articleEntity.getUrl());
        article.setDescription(articleEntity.getDescription());
        article.setTitle(articleEntity.getTitle());
        article.setAuthor(articleEntity.getAuthor());
        article.setContent(articleEntity.getContent());
        article.setPublishedAt(articleEntity.getPublishedDate());
        article.setSource(source);
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
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Application application = getActivity().getApplication();

        FavouriteArticlesListViewModel viewModel = ViewModelProviders.of(
                this,
                new FavouriteArticlesListViewModel.Factory(application,
                        favouriteArticleRepository)).get(FavouriteArticlesListViewModel.class);
        viewModel.getFavouriteArticlesFromDb()
                .observe(this, (FavouriteArticlesListResult favouriteArticlesListResult) -> {
                    this.favouriteArticlesListResult = favouriteArticlesListResult;
                    adapter = new FavouriteListAdapter(
                            FavouriteFragment.this,
                            onRemoveArticleListener,
                            favouriteArticlesListResult.getFavouriteArticleList(),
                            favouriteArticleRepository, context);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (favouriteArticlesListResult.getFavouriteArticleList().size() == 0) {
                        Toast.makeText(context, R.string.empty_favourite_list_message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void shareAllFavArticleUrls(FavouriteArticlesListResult favouriteArticlesListResult) {
        List<ArticleEntity> favArticles;
        favArticles = favouriteArticlesListResult.getFavouriteArticleList();
        StringBuilder stackArticleUrls = new StringBuilder();
        if (!favArticles.isEmpty()) {
            for (int i = 0; i < favArticles.size(); i++) {
                stackArticleUrls.append(favArticles.get(i).getUrl()).append("\n\n");
            }
        }

        Intent i = new Intent(
                android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, stackArticleUrls.toString());
        startActivity(Intent.createChooser(i, "Title of your share dialog"));
    }


    private void removeAllFavArticles() {
        favouriteArticleRepository.deleteAllArticles();
        adapter = new FavouriteListAdapter(
                FavouriteFragment.this,
                onRemoveArticleListener,
                favouriteArticlesListResult.getFavouriteArticleList(),
                favouriteArticleRepository, context);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Toast.makeText(context, "Articles removed", Toast.LENGTH_SHORT).show();
    }
}

