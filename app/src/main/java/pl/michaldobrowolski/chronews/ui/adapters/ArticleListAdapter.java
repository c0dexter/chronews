package pl.michaldobrowolski.chronews.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    //private final ListAdapterOnClickHandler listAdapterOnClickHandler;

    private List<Article> articleList;
    private Article article;
    private Context context;
    private DynamicHeightImage dynamicHeightImage;

    public ArticleListAdapter(List<Article> articleList, Context context) { //ListAdapterOnClickHandler listAdapterOnClickHandler
        //this.listAdapterOnClickHandler = listAdapterOnClickHandler;
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);

        dynamicHeightImage = new DynamicHeightImage(context);
        dynamicHeightImage.setRatioThreeTwo();
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        if (article.getUrlToImage() != null) {
            Picasso.get().load(article.getUrlToImage()).into(holder.ivArticleThumbnail);
        } else {
            Picasso.get().load(R.drawable.default_news_photo).into(holder.ivArticleThumbnail);
        }

        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvArticleDate.setText(UtilityHelper.displayShortDate(article.getPublishedAt()));
        holder.tvArticleSource.setText(article.getSource().getName());
        holder.tvArticleDesc.setText(article.getDescription());
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "Item count: " + String.valueOf(articleList.size()));
        return articleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.article_thumbnail)
        DynamicHeightImage ivArticleThumbnail;
        @BindView(R.id.article_title)
        TextView tvArticleTitle;
        @BindView(R.id.article_source)
        TextView tvArticleSource;
        @BindView(R.id.article_date)
        TextView tvArticleDate;
        @BindView(R.id.article_description)
        TextView tvArticleDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View v) {

        }
    }

}
