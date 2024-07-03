package com.the8way.rssnews

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.the8way.rssnews.ui.theme.screens.ArticleListScreen
import com.the8way.rssnews.ui.theme.screens.LoginScreen
import com.the8way.rssnews.ui.theme.screens.RegisterScreen
import com.the8way.rssnews.ui.theme.viewmodels.AuthViewModel
import com.the8way.rssnews.ui.theme.viewmodels.AuthViewModelFactory
import com.the8way.rssnews.workers.ArticleCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(applicationContext) }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setupWorker()
            } else {
                Toast.makeText(this, "Permission for notifications denied.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.authManager.configureGoogleSignIn()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = if (authViewModel.currentUser != null) "articles_screen" else "login_screen") {
                composable("login_screen") {
                    LoginScreen(activity = this@MainActivity, authViewModel = authViewModel, navController = navController)
                }
                composable("register_screen") {
                    RegisterScreen(navController = navController, authViewModel = authViewModel)
                }
                composable("articles_screen") {
                    ArticleListScreen(navController = navController, authViewModel = authViewModel)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    setupWorker()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            setupWorker()
        }
    }

    private fun setupWorker() {
        val workRequest = PeriodicWorkRequestBuilder<ArticleCheckWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ArticleCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
