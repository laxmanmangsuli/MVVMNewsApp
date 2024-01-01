package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponce
import com.androiddevs.mvvmnewsapp.repository.NewRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class NewsViewModel(
    app :Application,
    private val newRepository : NewRepository
) : AndroidViewModel(app) {
    val breakingNews : MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
     var breakingNewsPage = 1
    var breakingNewsResponse :NewsResponce? = null

    val searchNews : MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
     var searchNewsPage = 1
    var searchNewsResponse :NewsResponce? = null

    init {

        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handlerBreakingNewsResponse(response: Response<NewsResponce>): Resource<NewsResponce>? {

        if (response.isSuccessful){
            response.body()?.let { resultResponce ->
                breakingNewsPage++
                if (breakingNewsResponse== null){
                    breakingNewsResponse =resultResponce
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponce.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponce)
            }
        }
        return Resource.Error(response.message())

    }

    private fun handlerSearchNewsResponse(response: Response<NewsResponce>): Resource<NewsResponce>? {
        if (response.isSuccessful){
            response.body()?.let { resultResponce ->
                breakingNewsPage++
                if (searchNewsResponse== null){
                    searchNewsResponse =resultResponce
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponce.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponce)
            }
        }
        return Resource.Error(response.message())

    }

    fun saveArticle(article: Article)= newRepository.upsert(article)

    fun getSavedNews() =newRepository.getSavedNews()

    fun deleteArticle(article: Article) = newRepository.deleteArticle(article)

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handlerSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handlerBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection():Boolean{

        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           val activityNetwork = connectivityManager.activeNetwork ?: return false

           val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork) ?:return false

           return when{
               capabilities.hasTransport(TRANSPORT_WIFI) ->true
               capabilities.hasTransport(TRANSPORT_CELLULAR) ->true
               capabilities.hasTransport(TRANSPORT_ETHERNET) ->true
               else -> false
           }
       }else{
           connectivityManager.activeNetworkInfo?.run {
               return  when(type){
                   TYPE_WIFI ->true
                   TYPE_MOBILE -> true
                   TYPE_ETHERNET -> true
                   else ->false
               }
           }
       }
        return false
    }

}