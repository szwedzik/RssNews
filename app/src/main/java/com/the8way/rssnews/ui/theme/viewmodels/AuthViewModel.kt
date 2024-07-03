package com.the8way.rssnews.ui.theme.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.the8way.rssnews.auth.FirebaseAuthManager
import kotlinx.coroutines.launch

class AuthViewModel(val context: Context) : ViewModel() {
    val authManager = FirebaseAuthManager(context)

    val currentUser: FirebaseUser?
        get() = authManager.getCurrentUser()

    fun loginUser(email: String, password: String, onComplete: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = authManager.loginUser(email, password)
            onComplete(user)
        }
    }

    fun registerUser(email: String, password: String, onComplete: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = authManager.registerUser(email, password)
            onComplete(user)
        }
    }

    fun logoutUser() {
        authManager.logoutUser()
    }

    fun handleGoogleSignInResult(data: Intent, onComplete: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            try {
                val signInCredential: SignInCredential = Identity.getSignInClient(context).getSignInCredentialFromIntent(data)
                val idToken = signInCredential.googleIdToken
                if (idToken != null) {
                    val user = authManager.firebaseAuthWithGoogle(idToken)
                    onComplete(user)
                } else {
                    onComplete(null)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed", e)
                onComplete(null)
            }
        }
    }
}
