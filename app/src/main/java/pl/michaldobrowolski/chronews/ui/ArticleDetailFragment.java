package pl.michaldobrowolski.chronews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.FavouriteArticleRepository;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.Analytics;
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
    @BindView(R.id.share_icon)
    ImageView ivShareIcon;
    private Article article;
    private FavouriteArticleRepository favouriteArticleRepository;
    private boolean articleStored;
    private String articleTitle;
    private String articlePublishedDate;
    private String articleSource;
    private String articleAuthor;
    private String articleDesc;
    private String articleImageUrl;
    private String articleContent;
    private String titleText;
    private String subtitleText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = View.inflate(getActivity(), R.layout.fragment_article_detail, null);
        ButterKnife.bind(this, rootView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            article = bundle.getParcelable("articleKey");
            populateViews(Objects.requireNonNull(article));
            titleText = bundle.containsKey("titleNameKey") ? bundle.getString("titleNameKey") : "";
        }

        if (titleText != null && !titleText.equals("")) {
            tbArticleDetailToolbar.setTitle(UtilityHelper.makeUpperString(titleText));
        }

        tbArticleDetailToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        tbArticleDetailToolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        favouriteArticleRepository = new FavouriteArticleRepository(getContext());
        ChronewsWidgetProvider chronewsWidgetProvider = new ChronewsWidgetProvider();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleTitle = article.getTitle();
        articlePublishedDate = article.getPublishedAt();
        if (article.getSource() != null) {
            articleSource = article.getSource().getName();
        }
        String articleUrl = article.getUrl();
        articleAuthor = article.getAuthor();
        articleDesc = article.getDescription();
        articleImageUrl = article.getUrlToImage();
        articleContent = article.getContent();

        ivShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, articleUrl);
                startActivity(Intent.createChooser(i, "Title of your share dialog"));
            }
        });

        setFavButtonLogic(articleUrl);
        setFabButtonLook();
        tvReadMoreText.setOnClickListener(v -> UtilityHelper.openArticleInBrowser(requireContext(), article));
    }

    private void setFavButtonLogic(String articleUrl) {
        try {
            articleStored = favouriteArticleRepository.getArticleCountByUrl(articleUrl);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        btnFav.setOnClickListener(v -> {
            if (!articleStored) {
                try {
                    favouriteArticleRepository.insertArticle(articleTitle, articlePublishedDate, articleSource, articleUrl, articleAuthor, articleDesc, articleImageUrl, articleContent);
                    articleStored = true;
                    setFabButtonLook();
                    UtilityHelper.updateWidget(getContext()); // Update widgets
                    Toast.makeText(getContext(), R.string.article_saved_message, Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("article_title", articleTitle);
                    Analytics.get(getContext()).logEvent("article_added_to_favourite", bundle);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    favouriteArticleRepository.deleteArticle(articleUrl);
                    articleStored = false;
                    setFabButtonLook();
                    UtilityHelper.updateWidget(getContext()); // Update widgets
                    Toast.makeText(getContext(), R.string.article_removed_message, Toast.LENGTH_SHORT).show();
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
        if (article.getUrlToImage() != null && !String.valueOf(article.getUrlToImage()).equals("")) {
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
        } else {
            tvArticleDetailText.setText(article.getDescription());
        }
        tvArticleDetailTitle.setText(article.getTitle());
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
