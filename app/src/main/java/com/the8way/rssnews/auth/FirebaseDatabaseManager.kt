package com.the8way.rssnews.auth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.the8way.rssnews.data.Article
import kotlinx.coroutines.tasks.await

class FirebaseDatabaseManager {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private fun saveArticle(article: Article) {
        val articleId = database.child("articles").push().key
        if (articleId != null) {
            database.child("articles").child(articleId).setValue(article)
        }
    }

    fun saveArticles(articles: List<Article>) {
        articles.forEach { saveArticle(it) }
    }


    fun saveReadArticle(article: Article) {
        val articleId = article.link.hashCode().toString()
        database.child("read_articles").child(articleId).setValue(true)
    }


    fun saveFavoriteArticle(article: Article) {
        val articleId = article.link.hashCode().toString()
        database.child("favorite_articles").child(articleId).setValue(true)
    }

    fun removeFavoriteArticle(article: Article) {
        val articleId = article.link.hashCode().toString()
        database.child("favorite_articles").child(articleId).removeValue()
    }

}
