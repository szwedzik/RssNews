package com.the8way.rssnews.ui.theme.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.the8way.rssnews.auth.FirebaseAuthManager

class AuthViewModelFactory(private val activity: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}