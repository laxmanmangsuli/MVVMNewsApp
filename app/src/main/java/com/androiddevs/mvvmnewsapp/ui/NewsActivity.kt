package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = NewRepository(ArticleDatabase(this))

        val viewmodelProviderfactory = NewsViewModelProviderfactory(application,repository)

        viewModel = ViewModelProvider(this,viewmodelProviderfactory).get(NewsViewModel::class.java)

        val navController = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navController.navController)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController.navController)
        }
    }
}


