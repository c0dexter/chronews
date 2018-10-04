package pl.michaldobrowolski.chronews.api.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News implements Parcelable {

    public final static Parcelable.Creator<News> CREATOR = new Creator<News>() {


        @SuppressWarnings({
                "unchecked"
        })
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return (new News[size]);
        }

    };

    @SerializedName("status")
    private String status;
    @SerializedName("totalResults")
    private int totalResults;
    @SerializedName("articles")
    private List<Article> articles = null;

    protected News(Parcel in) {
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.totalResults = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.articles, (pl.michaldobrowolski.chronews.api.model.pojo.Article.class.getClassLoader()));
    }

    public News() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeValue(totalResults);
        dest.writeList(articles);
    }

    public int describeContents() {
        return 0;
    }

}