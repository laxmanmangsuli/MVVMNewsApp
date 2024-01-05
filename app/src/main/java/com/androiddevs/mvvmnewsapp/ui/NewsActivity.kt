package com.androiddevs.mvvmnewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.ActivityNewsBinding
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewRepository

class NewsActivity : AppCompatActivity() {
lateinit var viewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding
    private lateinit var viewModelBreak: BreakingNewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelBreak = ViewModelProvider(this).get(BreakingNewsViewModel::class.java)

        val repository = NewRepository(ArticleDatabase(this))

        val viewmodelProviderfactory = NewsViewModelProviderfactory(application,repository)

        viewModel = ViewModelProvider(this,viewmodelProviderfactory).get(NewsViewModel::class.java)

        val navController = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navController.navController)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
          when(item.itemId){
            R.id.breakingNewsFragment ->{
                viewModelBreak.selectedArticle = null
            }
          }
            NavigationUI.onNavDestinationSelected(item, navController.navController)
        }
    }
}


