package com.the8way.rssnews.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prof18.rssparser.RssParser
import com.the8way.rssnews.auth.SharedPreferencesManager
import com.the8way.rssnews.ui.theme.WebViewActivity

class ArticleCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val rssParser = RssParser()
    private val sharedPreferencesManager = SharedPreferencesManager(context)
    private val channelId = "new_articles_channel"

    override suspend fun doWork(): Result {
        return try {
            val channel = rssParser.getRssChannel("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.xml")
            val newArticles = channel.items.filter { item ->
                !sharedPreferencesManager.isArticleRead(item.link ?: "")
            }

            if (newArticles.isNotEmpty()) {
                sendNotification(newArticles.size, newArticles.first().link ?: "")
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun sendNotification(articleCount: Int, firstArticleUrl: String) {
        createNotificationChannel()

        val intent = Intent(applicationContext, WebViewActivity::class.java).apply {
            putExtra("url", firstArticleUrl)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Articles Available")
            .setContentText("There are $articleCount new articles available.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(1, notification)
            }
        } else {
            Log.e("ArticleCheckWorker", "Permission for notifications not granted")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "New Articles Channel"
            val descriptionText = "Notifications for new articles"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}