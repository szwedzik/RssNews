package com.the8way.rssnews.data

data class Article(
    val title: String,
    val link: String,
    val description: String,
    val imageUrl: String?,
    val categories: List<String>,
    val pubDate: String,
    val creator: String,
    val isRead: Boolean = false,
    val isFavorite: Boolean = false
)