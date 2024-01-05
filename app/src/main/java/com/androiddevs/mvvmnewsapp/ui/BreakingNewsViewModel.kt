package com.androiddevs.mvvmnewsapp.ui

import androidx.lifecycle.ViewModel
import com.androiddevs.mvvmnewsapp.models.Article

class BreakingNewsViewModel : ViewModel() {
    var selectedArticle: Article? = null
}
