package com.the8way.rssnews.ui.theme.components

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.the8way.rssnews.data.Article
import com.the8way.rssnews.ui.theme.WebViewActivity
import org.jsoup.Jsoup

fun cleanAndShortenDescription(description: String): String {
    val cleanedDescription = Jsoup.parse(description).text()
    return if (cleanedDescription.length > 100) {
        cleanedDescription.substring(0, 100) + "..."
    } else {
        cleanedDescription
    }
}

@Composable
fun ArticleItem(article: Article, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    val context = LocalContext.current

    val backgroundColor = if (article.isRead) Color.LightGray else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onClick()
                val intent = Intent(context, WebViewActivity::class.java).apply {
                    putExtra("url", article.link)
                }
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            val title = if (article.title.length > 30) {
                article.title.substring(0, 30) + "..."
            } else {
                article.title
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            article.imageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = article.pubDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cleanAndShortenDescription(article.description),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "By: ${article.creator}",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Categories: ${article.categories.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onFavoriteClick() }) {
                    Icon(
                        imageVector = if (article.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (article.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (article.isFavorite) Color.Yellow else LocalContentColor.current
                    )
                }
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${article.title}\n${article.link}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share article via"))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share article",
                        tint = LocalContentColor.current
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}