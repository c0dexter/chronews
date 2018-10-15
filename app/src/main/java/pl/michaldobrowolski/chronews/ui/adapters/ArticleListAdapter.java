package pl.michaldobrowolski.chronews.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    //private final ListAdapterOnClickHandler listAdapterOnClickHandler;


    private List<Article> articleList;
    private Article article;
    private Context context;

    public ArticleListAdapter(List<Article> articleList, Context context) { //ListAdapterOnClickHandler listAdapterOnClickHandler
        //this.listAdapterOnClickHandler = listAdapterOnClickHandler;
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);
        Picasso.get().load(article.getUrlToImage()).into(holder.ivArticleThumbnail);
        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvArticleShortDesc.setText(article.getDescription());
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "Item count: " + String.valueOf(articleList.size()));
        return articleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.article_thumbnail)
        ImageView ivArticleThumbnail;
        @BindView(R.id.article_title)
        TextView tvArticleTitle;
        @BindView(R.id.article_short_desc)
        TextView tvArticleShortDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View v) {

        }
    }

//    // Transform selected Article to the JSON String for future operation on DB
//    private String transformJsonToString(String jsonResult, int position) {
//        JsonElement element = new JsonParser().parse(jsonResult);
//        JsonArray elementAsJsonArray = element.getAsJsonArray();
//        JsonElement articleElement = elementAsJsonArray.get(position);
//        return articleElement.toString();
//    }

//    public interface ListAdapterOnClickHandler {
//        void onClickArticle(int articleCardPosition);
//    }
}
