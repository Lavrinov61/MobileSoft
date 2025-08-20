package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // A simple resource class to represent states
    data class AuthResult(val success: Boolean, val message: String? = null)

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(AuthResult(false, "Loading...")) // Represent loading state
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthResult(true))
        } catch (e: Exception) {
            emit(AuthResult(false, e.message))
        }
    }

    fun register(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(AuthResult(false, "Loading...")) // Represent loading state
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            emit(AuthResult(true))
        } catch (e: Exception) {
            emit(AuthResult(false, e.message))
        }
    }
}

// We need to add the kotlinx-coroutines-play-services dependency for the .await() function
// I will add this to the build.gradle.kts file.
