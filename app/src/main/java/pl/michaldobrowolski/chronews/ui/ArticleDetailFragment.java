package pl.michaldobrowolski.chronews.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.ArticleRepository;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class ArticleDetailFragment extends Fragment {
    private static final String TAG = ArticleDetailFragment.class.getClass().getSimpleName();
    @BindView(R.id.button_favourite)
    FloatingActionButton btnFav;
    @BindView(R.id.image_article_detail)
    DynamicHeightImage ivArticleImage;
    @BindView(R.id.text_article_detail_text)
    TextView tvArticleDetailText;
    @BindView(R.id.text_article_detail_title)
    TextView tvArticleDetailTitle;
    @BindView(R.id.text_article_detail_read_more)
    TextView tvReadMoreText;
    @BindView(R.id.text_article_detail_author)
    TextView tvArticleDetailAuthor;
    @BindView(R.id.toolbar_article_detail)
    Toolbar tbArticleDetailToolbar;
    private Context context;
    private Article article;
    private ArticleRepository articleRepository;
    private boolean articleExist = false;
    private String articleTitle;
    private String articlePublishedDate;
    private String articleSource;
    private String articleUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, null);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        articleRepository = new ArticleRepository(context);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            article = bundle.getParcelable("articleKey");
            populateViews(Objects.requireNonNull(article));
        }

        articleTitle = article.getTitle();
        articlePublishedDate = article.getPublishedAt();
        articleSource = article.getSource().getName();
        articleUrl = article.getUrl();

        setFavButton(articleUrl);

        tvReadMoreText.setOnClickListener(v -> UtilityHelper.openArticleInBrowser(context, article));
    }

    private void setFavButton(String articleUrl) {
        // btnFav.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnFav.setOnClickListener(v -> {
            DebugDB.getAddressLog();
            List<ArticleEntity> articleEntities = null;
            ArticleEntity articleEntity = null;

            try {
                articleRepository.insertArticle(articleTitle, articlePublishedDate, articleSource, articleUrl);
                Toast.makeText(context, "Article has been saved", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                articleEntities = articleRepository.getAllArticles();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                articleEntity  = articleRepository.getArticleByUrl(articleUrl);
                Log.i(TAG, "MY_URL" + articleEntity.getUrl());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                articleRepository.deleteArticle(articleTitle, articlePublishedDate, articleSource, articleUrl);
                Toast.makeText(context, "Article deleted", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, articleEntities.get(0).getTitle());
            Log.i(TAG, articleEntity.getUrl());
        });
    }

    private void populateViews(Article article) {

        if (article.getUrl() != null) {
            Picasso.get()
                    .load(article.getUrlToImage())
                    .into(ivArticleImage);
        } else {
            Picasso.get()
                    .load(R.drawable.default_news_photo)
                    .into(ivArticleImage);
        }

        if (article.getAuthor() != null) {
            tvArticleDetailAuthor.setVisibility(View.VISIBLE);
            tvArticleDetailAuthor.setText(article.getAuthor());
        }

        if (article.getContent() != null) {
            tvArticleDetailText.setText(UtilityHelper.removeRedundantCharactersFromText(article.getContent()));
            Log.d(TAG, "ARTICLE CONTENT: " + article.getContent());
        } else {
            tvArticleDetailText.setText(article.getDescription());
        }
        tvArticleDetailTitle.setText(article.getTitle());
        Log.d(TAG, article.getContent() + " " + article.getAuthor());
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity(), "Context of activity must not be null")).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity(), "Context of activity must not be null")).getSupportActionBar()).show();
    }
}
