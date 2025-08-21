package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.UUID

class PhoneVerificationViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    sealed class VerificationState {
        object Idle : VerificationState()
        data class AwaitingBot(val deepLinkUrl: String) : VerificationState()
        data class Success(val phoneNumber: String) : VerificationState()
        data class Error(val message: String) : VerificationState()
    }

    private val _verificationState = MutableLiveData<VerificationState>(VerificationState.Idle)
    val verificationState: LiveData<VerificationState> = _verificationState

    fun startVerification(telegramUsername: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _verificationState.value = VerificationState.Error("User not logged in.")
                return@launch
            }
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)

            val token = UUID.randomUUID().toString()
            val botUsername = "YOUR_BOT_USERNAME" // This should be configured properly
            val deepLinkUrl = "https://t.me/$botUsername?start=$token"

            // Update user doc with token to trigger the bot
            userDocRef.update("telegramVerificationToken", token)
                .addOnSuccessListener {
                    // Now listen for the result
                    listenForVerificationResult(userId)
                    _verificationState.value = VerificationState.AwaitingBot(deepLinkUrl)
                }
                .addOnFailureListener { e ->
                    _verificationState.value = VerificationState.Error("Failed to start verification: ${e.message}")
                }
        }
    }

    private fun listenForVerificationResult(userId: String) {
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _verificationState.value = VerificationState.Error("Listener failed: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val phoneNumber = snapshot.getString("verifiedPhoneNumberTelegram")
                    if (!phoneNumber.isNullOrEmpty()) {
                        _verificationState.value = VerificationState.Success(phoneNumber)
                    }
                }
            }
    }
}
