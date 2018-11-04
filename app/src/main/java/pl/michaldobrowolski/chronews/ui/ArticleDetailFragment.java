package pl.michaldobrowolski.chronews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class ArticleDetailFragment extends Fragment {
    private static final String TAG = ArticleDetailFragment.class.getClass().getSimpleName();
    @BindView(R.id.article_image)
    DynamicHeightImage ivArticleImage;
    @BindView(R.id.article_detail_text)
    TextView tvArticleDetailText;
    @BindView(R.id.article_detail_title)
    TextView tvArticleDetailTitle;
    @BindView(R.id.article_detail_author)
    TextView tvArticleDetailAuthor;
    private Article article;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, null);
        ButterKnife.bind(this, rootView);

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
        } else {
            tvArticleDetailText.setText(article.getDescription());
        }

        tvArticleDetailTitle.setText(article.getTitle());


        Log.d(TAG, article.getContent() + " " + article.getAuthor());
    }
}
