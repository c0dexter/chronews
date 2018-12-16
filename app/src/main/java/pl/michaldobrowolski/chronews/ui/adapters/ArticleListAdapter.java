package pl.michaldobrowolski.chronews.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
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
    private final OnItemClickListener onItemClickListener;
    private List<Article> articleList;
    private Context context;
    private DynamicHeightImage dynamicHeightImage;

    public ArticleListAdapter(List<Article> articleList, Context context, OnItemClickListener onItemClickListener) {
        this.articleList = articleList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);

        dynamicHeightImage = new DynamicHeightImage(context);
        dynamicHeightImage.setUserAspectRatio((float) 1.5);
        view.setFocusable(true);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Article article = articleList.get(position);

        if (article.getUrlToImage() != null) {
            Picasso.get()
                    .load(article.getUrlToImage())
                    .into(holder.ivArticleThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbMainThumbnail.setAlpha(0f);
                            holder.pbMainThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbMainThumbnail.setVisibility(View.GONE);
                            holder.ivShadowShapeThumbnail.setVisibility(View.VISIBLE);
                            holder.flDateItem.setVisibility(View.VISIBLE);
                            holder.tvArticleSource.setVisibility(View.VISIBLE);
                            holder.tvAuthor.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbMainThumbnail.setVisibility(View.GONE);
                        }
                    });
        } else {
            Picasso.get()
                    .load(R.drawable.default_news_photo)
                    .into(holder.ivArticleThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbMainThumbnail.setAlpha(0f);
                            holder.pbMainThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbMainThumbnail.setVisibility(View.GONE);
                            holder.ivShadowShapeThumbnail.setVisibility(View.VISIBLE);
                            holder.flDateItem.setVisibility(View.VISIBLE);
                            holder.tvArticleSource.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbMainThumbnail.setVisibility(View.GONE);
                        }
                    });
        }

        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvArticleDate.setText(UtilityHelper.displayShortDate(article.getPublishedAt()));
        holder.tvArticleSource.setText(article.getSource().getName());
        if (article.getDescription() != null) {
            holder.tvArticleDesc.setVisibility(View.VISIBLE);
            holder.tvArticleDesc.setText(article.getDescription());
        }
        holder.tvPublishedTimeCounter.setText(UtilityHelper.publishTimeCounter(article.getPublishedAt()));
        if (article.getAuthor() != null) {
            holder.tvAuthor.setText(article.getAuthor());
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "Item count: " + String.valueOf(articleList.size()));
        return articleList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_category_thumb)
        DynamicHeightImage ivArticleThumbnail;
        @BindView(R.id.text_author)
        TextView tvAuthor;
        @BindView(R.id.text_article_date)
        TextView tvArticleDate;
        @BindView(R.id.text_article_title)
        TextView tvArticleTitle;
        @BindView(R.id.text_article_source)
        TextView tvArticleSource;
        @BindView(R.id.text_published_time_counter)
        TextView tvPublishedTimeCounter;
        @BindView(R.id.text_article_description)
        TextView tvArticleDesc;
        @BindView(R.id.image_shadow_bottom_thumb)
        ImageView ivShadowShapeThumbnail;
        @BindView(R.id.frame_article_date)
        FrameLayout flDateItem;
        @BindView(R.id.progress_bar_home_thumbnail)
        ProgressBar pbMainThumbnail;
        OnItemClickListener onItemClickListener;


        ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
