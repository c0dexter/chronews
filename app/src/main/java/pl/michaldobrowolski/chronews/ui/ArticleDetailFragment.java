package pl.michaldobrowolski.chronews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;

public class ArticleDetailFragment extends Fragment {
    private Article article;
    @BindView(R.id.article_image)
    DynamicHeightImage ivArticleImage;

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
        Picasso.get()
                .load(article.getUrlToImage())
                .into(ivArticleImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }
}
