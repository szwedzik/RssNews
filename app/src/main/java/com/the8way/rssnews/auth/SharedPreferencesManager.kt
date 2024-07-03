package com.the8way.rssnews.auth

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("rss_prefs", Context.MODE_PRIVATE)

    fun saveReadArticle(articleLink: String) {
        prefs.edit().putBoolean(articleLink, true).apply()
    }

    fun isArticleRead(articleLink: String): Boolean {
        return prefs.getBoolean(articleLink, false)
    }

    fun saveFavoriteArticle(articleLink: String) {
        prefs.edit().putBoolean("favorite_$articleLink", true).apply()
    }

    fun removeFavoriteArticle(articleLink: String) {
        prefs.edit().remove("favorite_$articleLink").apply()
    }

    fun isArticleFavorite(articleLink: String): Boolean {
        return prefs.getBoolean("favorite_$articleLink", false)
    }
}