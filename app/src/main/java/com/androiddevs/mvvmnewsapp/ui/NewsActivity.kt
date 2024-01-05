package com.androiddevs.mvvmnewsapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.ActivityNewsBinding
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewRepository
import com.androiddevs.mvvmnewsapp.ui.fragment.ArticleFragment

class NewsActivity : AppCompatActivity() {
lateinit var viewModel: NewsViewModel
    lateinit var binding: ActivityNewsBinding
    private var bottomNavManager :BottomNavManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigationManager()

        val repository = NewRepository(ArticleDatabase(this))

        val viewmodelProviderfactory = NewsViewModelProviderfactory(application,repository)

        viewModel = ViewModelProvider(this,viewmodelProviderfactory).get(NewsViewModel::class.java)

//        val navController = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
//        binding.bottomNavigationView.setupWithNavController(navController.navController)

//        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//          when(item.itemId){
//            R.id.breakingNewsFragment ->{
//                viewModelBreak.selectedArticle = null
//            }
//          }
//            NavigationUI.onNavDestinationSelected(item, navController.navController)
//        }
    }

    fun showImageView() {
        binding.ivBack.visibility = View.VISIBLE
    }

    fun hideImageView() {
        binding.ivBack.visibility = View.GONE
    }

    private fun setUpNavigationManager() {

        bottomNavManager?.setupNavController() ?: kotlin.run {
            bottomNavManager = BottomNavManager(
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_fragment_container,
                bottomNavigationView = findViewById(R.id.bottomNavigationView)
            )
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bottomNavManager?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNavManager?.onRestoreInstanceState(savedInstanceState)
        setUpNavigationManager()
    }

    override fun onBackPressed() {
        if (bottomNavManager?.onBackPressed() == false) super.onBackPressed()
    }
}


