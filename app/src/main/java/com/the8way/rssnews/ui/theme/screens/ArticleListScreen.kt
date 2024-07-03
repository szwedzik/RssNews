@file:Suppress("DEPRECATION")

package com.the8way.rssnews.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.the8way.rssnews.ui.theme.components.ArticleItem
import com.the8way.rssnews.ui.theme.viewmodels.AuthViewModel
import com.the8way.rssnews.ui.theme.viewmodels.RssViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    navController: NavController,
    viewModel: RssViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {

    val articles by viewModel.articles.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var currentCategory by remember { mutableStateOf("Local News") }
    var currentUrl by remember { mutableStateOf("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.xml") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentCategory) },
                navigationIcon = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Local News") },
                            onClick = {
                                currentCategory = "Local News"
                                currentUrl = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.xml"
                                viewModel.fetchArticles("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.xml")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("World News") },
                            onClick = {
                                currentCategory = "World News"
                                currentUrl = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_swiat.xml"
                                viewModel.fetchArticles("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_swiat.xml")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Favorites") },
                            onClick = {
                                currentCategory = "Favorites"
                                viewModel.showFavoritesOnly()
                                expanded = false
                            }
                        )
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                authViewModel.logoutUser()
                                navController.navigate("login_screen") {
                                    popUpTo("articles_screen") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.fetchArticles(currentUrl) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(articles) { article ->
                    ArticleItem(
                        article = article,
                        onClick = {
                            viewModel.markAsRead(article)
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(article) }
                    )
                }
            }
        }
    }
}