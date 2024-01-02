package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun upsert(article: Article): Long


    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    fun deleteArticle(article: Article)

  @Query("SELECT * FROM articles WHERE url = :articleUrl")
  fun getArticleByUrl(articleUrl: String): Article?

  @Query("DELETE FROM articles WHERE url = :articleUrl")
   fun deleteArticleByUrl(articleUrl: String)

}