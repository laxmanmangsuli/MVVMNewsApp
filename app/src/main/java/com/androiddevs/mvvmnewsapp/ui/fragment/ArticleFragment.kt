package com.androiddevs.mvvmnewsapp.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentArticleBinding
import com.androiddevs.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    private var isArticleSaved = false

    private lateinit var binding: FragmentArticleBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.article
        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressbar.visibility = View.GONE
                    binding.fab.visibility = View.VISIBLE
                }
            }
            loadUrl(article.url!!)
        }
        CoroutineScope(Dispatchers.IO).launch {
            var isSaved = article.url?.let { it1 -> viewModel.isArticleSaved(it1) }
            if (isSaved!!) {
                binding.fab.setImageResource(R.drawable.ic_favorite)
                isArticleSaved = true
            }

            binding.fab.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (!isSaved!! && !isArticleSaved ) {
                        viewModel.saveArticle(article)
                        withContext(Dispatchers.Main) {
                            binding.fab.setImageResource(R.drawable.ic_favorite)
                            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
                            isArticleSaved = true
                        }
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteArticleByUrl(article.url!!)
                            CoroutineScope(Dispatchers.Main).launch {
                                binding.fab.setImageResource(R.drawable.ic_unfavorite)
                                Snackbar.make(
                                    view,
                                    "Article removed successfully",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                isArticleSaved = false
                                isSaved =false
                            }
                        }
                    }
                }

            }
        }
    }
}