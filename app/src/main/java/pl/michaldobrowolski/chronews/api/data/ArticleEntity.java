package pl.michaldobrowolski.chronews.api.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"article_url"},
        unique = true)}, tableName = "articles")
public class ArticleEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "article_id")
    private int id;

    @ColumnInfo(name = "article_title")
    private String title;

    @ColumnInfo(name = "article_description")
    private String description;

    @ColumnInfo(name = "article_content")
    private String content;

    @ColumnInfo(name = "article_url")
    private String url;

    @ColumnInfo(name = "article_image_url")
    private String imageUrl;

    @ColumnInfo(name = "article_author")
    private String author;

    @ColumnInfo(name = "article_published_date")
    private String publishedDate;

    @ColumnInfo(name = "article_source_name")
    private String sourceName;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
