package com.androiddevs.mvvmnewsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.util.isRedirectArticle
import com.bumptech.glide.Glide

class NewsAdapter :RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

   inner class ArticleViewHolder(val binding:ItemArticlePreviewBinding) :RecyclerView.ViewHolder(binding.root)

     val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
           return  oldItem==newItem
        }
    }
     val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
       val binding= ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArticleViewHolder(binding)

    }

    override fun getItemCount(): Int {
            return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article =differ.currentList[position]
        holder.itemView.apply {

            Glide.with(this).load(article.urlToImage).into(holder.binding.ivArticleImage)
            holder.binding.tvSource.text = article.source?.name
            holder.binding.tvTitle.text = article.title
            holder.binding.tvDescription.text = article.description
            holder.binding.tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.invoke(article)
            }
        }

    }

    private var onItemClickListener :((Article)-> Unit)? = null

    fun setOnItemClickListener(listener: (Article)->Unit){
        onItemClickListener = listener
    }

}