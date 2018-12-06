package pl.michaldobrowolski.chronews.api.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void addArticle(ArticleEntity articleEntity);

    @Query("SELECT * FROM articles")
    List<ArticleEntity> getArticles();

    @Query("SELECT * FROM articles WHERE article_url LIKE :url")
    ArticleEntity getArticle(String url);

    @Update
    void updateArticle(ArticleEntity articleEntity);

    @Delete
    void deleteArticle(ArticleEntity articleEntity);

    // Delete by using query
    @Query("DELETE FROM articles WHERE article_url = :url")
    void deleteArticleByUrl(String url);




}
