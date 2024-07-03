package com.the8way.rssnews.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.the8way.rssnews.ui.theme.screens.WebViewScreen

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url")

        setContent {
            RssNewsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    url?.let {
                        WebViewScreen(url)
                    }
                }
            }
        }
    }
}