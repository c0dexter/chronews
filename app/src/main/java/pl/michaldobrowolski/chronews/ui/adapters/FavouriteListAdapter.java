package pl.michaldobrowolski.chronews.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.data.ArticleEntity;
import pl.michaldobrowolski.chronews.api.data.FavouriteArticleRepository;
import pl.michaldobrowolski.chronews.ui.FavouriteFragment;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private final FavouriteListAdapter.OnItemClickListener onItemClickListener;
    private List<ArticleEntity> dbArticlesList;
    private Context context;
    private FavouriteArticleRepository favouriteArticleRepository;
    private Toast toast;
    private FavouriteFragment.OnRemoveArticleListener onRemoveArticleListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public FavouriteListAdapter(
            OnItemClickListener onItemClickListener,
            FavouriteFragment.OnRemoveArticleListener onRemoveArticleListener,
            List<ArticleEntity> dbArticlesList,
            FavouriteArticleRepository favouriteArticleRepository,
            Context context) {
        this.onItemClickListener = onItemClickListener;
        this.onRemoveArticleListener = onRemoveArticleListener;
        this.dbArticlesList = dbArticlesList;
        this.favouriteArticleRepository = favouriteArticleRepository;
        this.context = context;
    }


    @NonNull
    @Override
    public FavouriteListAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_fav_article, parent, false);
        return new FavouriteListAdapter.ViewHolder(view, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull FavouriteListAdapter.ViewHolder holder, int position) {
        ArticleEntity articleEntity = dbArticlesList.get(position);

        if (articleEntity.getImageUrl() != null) {
            Picasso.get()
                    .load(articleEntity.getImageUrl())
                    .into(holder.ivFavArticleThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbFavArticleThumbnail.setAlpha(0f);
                            holder.pbFavArticleThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbFavArticleThumbnail.setVisibility(View.GONE);
                            holder.tvFavArticleSource.setVisibility(View.VISIBLE);
                            holder.tvFavArticleAuthor.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbFavArticleThumbnail.setVisibility(View.GONE);
                        }
                    });
        } else {
            Picasso.get()
                    .load(R.drawable.default_news_photo)
                    .into(holder.ivFavArticleThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbFavArticleThumbnail.setAlpha(0f);
                            holder.pbFavArticleThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbFavArticleThumbnail.setVisibility(View.GONE);
                            holder.tvFavArticleSource.setVisibility(View.VISIBLE);
                            holder.tvFavArticleAuthor.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbFavArticleThumbnail.setVisibility(View.GONE);
                        }
                    });
        }

        holder.tvFavArticleTitle.setText(articleEntity.getTitle());
        holder.tvFavArticleSource.setText(articleEntity.getSourceName());
        holder.tvPublishedTimeCounter.setText(UtilityHelper.publishTimeCounter(articleEntity.getPublishedDate()));
        if (articleEntity.getAuthor() != null) {
            holder.tvFavArticleAuthor.setText(articleEntity.getAuthor());
        }
    }

    @Override
    public int getItemCount() {
        return dbArticlesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text_fav_article_title)
        TextView tvFavArticleTitle;
        @BindView(R.id.text_fav_article_author)
        TextView tvFavArticleAuthor;
        @BindView(R.id.text_fav_article_source)
        TextView tvFavArticleSource;
        @BindView(R.id.text_fav_published_time_counter)
        TextView tvPublishedTimeCounter;
        @BindView(R.id.button_remove_fav_article)
        ImageButton btnRemoveFavArticle;
        @BindView(R.id.image_fav_article_thumb)
        ImageView ivFavArticleThumbnail;
        @BindView(R.id.progress_bar_fav_article_image)
        ProgressBar pbFavArticleThumbnail;
        FavouriteListAdapter.OnItemClickListener onItemClickListener;


        ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            btnRemoveFavArticle.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String articleUrl = dbArticlesList.get(position).getUrl();
                try {
                    favouriteArticleRepository.deleteArticle(articleUrl);
                    dbArticlesList = favouriteArticleRepository.getAllArticles(); // Remember this!!!
                    notifyItemRemoved(position); // OR notifyDataSetChanged();

                    // Update widgets
                    UtilityHelper.updateWidget(context);
                    if (toast != null) {
                        toast.cancel();
                    }
                    onRemoveArticleListener.onRemoveArticleButtonClickedCallback();
                    Toast.makeText(context, R.string.article_removed_message, Toast.LENGTH_SHORT).show();

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            this.onItemClickListener = onItemClickListener;
        }


        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());

        }


    }
}
