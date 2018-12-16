package pl.michaldobrowolski.chronews.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleRepository;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;
import pl.michaldobrowolski.chronews.widget.ChronewsWidgetProvider;

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
    private boolean articleStored;
    private String articleTitle, articlePublishedDate, articleSource, articleUrl, articleAuthor, articleDesc, articleImageUrl, articleContent;
    private ChronewsWidgetProvider chronewsWidgetProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, null);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        articleRepository = new ArticleRepository(context);
        chronewsWidgetProvider = new ChronewsWidgetProvider();

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
        if (article.getSource() != null) {
            articleSource = article.getSource().getName();
        }
        articleUrl = article.getUrl();
        articleAuthor = article.getAuthor();
        articleDesc = article.getDescription();
        articleImageUrl = article.getUrlToImage();
        articleContent = article.getContent();

        setFavButtonLogic(articleUrl);
        setFabButtonLook();
        tvReadMoreText.setOnClickListener(v -> UtilityHelper.openArticleInBrowser(context, article));
    }

    private void setFavButtonLogic(String articleUrl) {
        try {
            articleStored = articleRepository.getArticleCountByUrl(articleUrl);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        btnFav.setOnClickListener(v -> {


            if (!articleStored) {
                DebugDB.getAddressLog();
                try {
                    articleRepository.insertArticle(articleTitle, articlePublishedDate, articleSource, articleUrl, articleAuthor, articleDesc, articleImageUrl, articleContent);
                    articleStored = true;
                    setFabButtonLook();
                    // Update widgets
                    UtilityHelper.updateWidget(context);
                    Toast.makeText(context, "Article has been saved", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    articleRepository.deleteArticle(articleUrl);
                    articleStored = false;
                    setFabButtonLook();
                    // Update widgets
                    UtilityHelper.updateWidget(context);
                    Toast.makeText(context, "Article removed", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setFabButtonLook() {
        if (articleStored) {
            btnFav.setImageResource(R.drawable.ic_favorite_article_on_24dp);
        } else {
            btnFav.setImageResource(R.drawable.ic_favorite_article_off_24dp);
        }
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
