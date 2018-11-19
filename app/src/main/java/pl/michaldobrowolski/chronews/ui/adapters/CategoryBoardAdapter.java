package pl.michaldobrowolski.chronews.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.utils.Category;
import pl.michaldobrowolski.chronews.utils.DynamicHeightImage;

public class CategoryBoardAdapter extends RecyclerView.Adapter<CategoryBoardAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private final OnItemClickListener onItemClickListener;
    private Context context;
    private DynamicHeightImage dynamicHeightImage;
    private List<Category> categoryList;

    public CategoryBoardAdapter(OnItemClickListener onItemClickListener, Context context, List<Category> categoryList) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        dynamicHeightImage = new DynamicHeightImage(context);
        dynamicHeightImage.setUserAspecthRatio(1.2f);
        view.setFocusable(true);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (categoryList.get(position).getCategoryImageUrl() != null) {
            Picasso.get()
                    .load(categoryList.get(position).getCategoryImageUrl())
                    .into(holder.ivCategoryThumb, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbCategoryThumbnail.setAlpha(0f);
                            holder.pbCategoryThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbCategoryThumbnail.setVisibility(View.GONE);
                            holder.ivShadowShapeCategoryThumbnail.setVisibility(View.VISIBLE);
                            holder.tvCategoryName.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbCategoryThumbnail.setVisibility(View.GONE);
                        }
                    });
        } else {
            Picasso.get()
                    .load(R.drawable.default_news_photo)
                    .into(holder.ivCategoryThumb, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pbCategoryThumbnail.setAlpha(0f);
                            holder.pbCategoryThumbnail.animate().setDuration(300).alpha(1f).start();
                            holder.pbCategoryThumbnail.setVisibility(View.GONE);
                            holder.ivShadowShapeCategoryThumbnail.setVisibility(View.VISIBLE);
                            holder.tvCategoryName.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.pbCategoryThumbnail.setVisibility(View.GONE);
                        }
                    });
        }
        holder.tvCategoryName.setText(categoryList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "Item count: " + String.valueOf(categoryList.size()));
        return categoryList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_category_thumb)
        DynamicHeightImage ivCategoryThumb;
        @BindView(R.id.image_shadow_bottom_category_thumb)
        ImageView ivShadowShapeCategoryThumbnail;
        @BindView(R.id.text_category_name)
        TextView tvCategoryName;
        @BindView(R.id.progress_bar_category_thumbnail)
        ProgressBar pbCategoryThumbnail;
        CategoryBoardAdapter.OnItemClickListener onItemClickListener;

        ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
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
