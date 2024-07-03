package com.the8way.rssnews.ui.theme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prof18.rssparser.RssParser
import com.the8way.rssnews.auth.FirebaseDatabaseManager
import com.the8way.rssnews.auth.SharedPreferencesManager
import com.the8way.rssnews.data.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RssViewModel(application: Application) : AndroidViewModel(application) {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _showFavoritesOnly = MutableStateFlow(false)
    private val rssParser: RssParser = RssParser()
    private val databaseManager = FirebaseDatabaseManager()
    private val sharedPreferencesManager = SharedPreferencesManager(application)

    init {
        fetchArticles("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.xml")
    }

    fun fetchArticles(url: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val channel = rssParser.getRssChannel(url)
                val processedArticles = channel.items.map { article ->
                    Article(
                        title = article.title ?: "",
                        link = article.link ?: "",
                        description = article.description ?: "",
                        imageUrl = article.image ?: null,
                        categories = article.categories ?: emptyList(),
                        pubDate = article.pubDate ?: "",
                        creator = article.author ?: "",
                        isRead = sharedPreferencesManager.isArticleRead(article.link ?: ""),
                        isFavorite = sharedPreferencesManager.isArticleFavorite(article.link ?: "")
                    )
                }
                databaseManager.saveArticles(processedArticles)
                _articles.value = processedArticles
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun markAsRead(article: Article) {
        _articles.value = _articles.value.map {
            if (it == article) it.copy(isRead = true) else it
        }
        sharedPreferencesManager.saveReadArticle(article.link)
        viewModelScope.launch {
            databaseManager.saveReadArticle(article)
        }
    }

    fun toggleFavorite(article: Article) {
        _articles.value = _articles.value.map {
            if (it == article) {
                val isFavorite = !it.isFavorite
                if (isFavorite) {
                    sharedPreferencesManager.saveFavoriteArticle(article.link)
                    viewModelScope.launch {
                        databaseManager.saveFavoriteArticle(it)
                    }
                } else {
                    sharedPreferencesManager.removeFavoriteArticle(article.link)
                    viewModelScope.launch {
                        databaseManager.removeFavoriteArticle(it)
                    }
                }
                it.copy(isFavorite = isFavorite)
            } else it
        }
    }

    fun showFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        if (_showFavoritesOnly.value) {
            _articles.value = _articles.value.filter { it.isFavorite }
        }
    }
}