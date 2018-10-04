package pl.michaldobrowolski.chronews.api.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Article implements Parcelable {

    public final static Parcelable.Creator<Article> CREATOR = new Creator<Article>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return (new Article[size]);
        }

    };

    @SerializedName("source")
    private Source source;
    @SerializedName("author")
    private String author;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("url")
    private String url;
    @SerializedName("urlToImage")
    private String urlToImage;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("content")

    private String content;

    protected Article(Parcel in) {
        this.source = ((Source) in.readValue((Source.class.getClassLoader())));
        this.author = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.urlToImage = ((String) in.readValue((String.class.getClassLoader())));
        this.publishedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Article() {
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(source);
        dest.writeValue(author);
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeValue(url);
        dest.writeValue(urlToImage);
        dest.writeValue(publishedAt);
        dest.writeValue(content);
    }

    public int describeContents() {
        return 0;
    }

}