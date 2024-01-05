package com.androiddevs.mvvmnewsapp.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.androiddevs.mvvmnewsapp.databinding.FragmentSavedNewsBinding
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.isArticleSaved
import com.androiddevs.mvvmnewsapp.util.navigateSafe
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedNewsFragment : Fragment() {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter :NewsAdapter

    private lateinit var binding: FragmentSavedNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        if (activity is NewsActivity) {
            (activity as NewsActivity).hideImageView()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel = (activity as NewsActivity).viewModel
            setupRecyclerView()
            newsAdapter.setOnItemClickListener {
                val bundle = Bundle().apply {
                    putSerializable("article", it)
                }
                findNavController().navigateSafe(
                    R.id.action_savedNewsFragment2_to_articleFragment3,
                    bundle
                )
            }

            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    CoroutineScope(Dispatchers.IO).launch {
                        val article = newsAdapter.differ.currentList[position]
                        viewModel.deleteArticle(article)
                        Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_SHORT)
                            .apply {
                                setAction("Undo") {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.saveArticle(article)
                                    }
                                }
                                show()
                            }

                    }
                }
            }
            ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(binding.rvSavedNews)
            }

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
                newsAdapter.differ.submitList(articles)
            })
        }
    }
    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()

        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}