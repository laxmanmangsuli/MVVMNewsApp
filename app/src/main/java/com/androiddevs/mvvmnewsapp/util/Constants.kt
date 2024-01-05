package com.androiddevs.mvvmnewsapp.util

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.androiddevs.mvvmnewsapp.NewsApplication.Companion.appContext

class Constants {
    companion object{
        const val API_KEY = "a0e59f1f753e483ab70d8f48f5987f14"
        const val BASE_URL = "https://newsapi.org/"
        const val SEARCH_NEWS_TIME_DELAY = 1000L
        const val QUERY_PAGE_SIZE = 20

    }

}

var isRedirectArticle = 0
fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}
private const val PREF_KEY_IS_ARTICLE_SAVED = "is_article_saved"

private fun saveIsArticleSavedState(context: Context, isArticleSaved: Boolean) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    editor.putBoolean(PREF_KEY_IS_ARTICLE_SAVED, isArticleSaved)
    editor.apply()
}
private fun getIsArticleSavedState(context: Context): Boolean {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getBoolean(PREF_KEY_IS_ARTICLE_SAVED, false)
}

var isArticleSaved: Boolean
    get() = getIsArticleSavedState(appContext)
    set(value) {
        saveIsArticleSavedState(appContext, value)
    }
