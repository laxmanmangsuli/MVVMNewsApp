package com.androiddevs.mvvmnewsapp.repository

import androidx.room.Query
import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article
import java.util.Locale.IsoCountryCode

class NewRepository(
    val db :ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String,pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    fun upsert(article: Article) = db.getArticleDao().upsert(article)

     fun getSavedNews()= db.getArticleDao().getAllArticles()

    fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}