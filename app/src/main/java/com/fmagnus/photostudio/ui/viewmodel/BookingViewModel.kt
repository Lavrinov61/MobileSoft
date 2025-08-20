package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmagnus.photostudio.data.repository.PhotoStudioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {

    private val repository = PhotoStudioRepository()
    private val auth = FirebaseAuth.getInstance()

    // A simple result class for the booking operation
    data class BookingResult(val success: Boolean, val message: String? = null)

    private val _bookingResult = MutableLiveData<BookingResult>()
    val bookingResult: LiveData<BookingResult> = _bookingResult

    fun createBooking(serviceId: String, slotId: String, userName: String, userPhone: String) {
        viewModelScope.launch {
            _bookingResult.value = BookingResult(false, "Loading...")
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _bookingResult.value = BookingResult(false, "User not logged in.")
                return@launch
            }

            val success = repository.createBooking(userId, serviceId, slotId, userName, userPhone)
            if (success) {
                _bookingResult.value = BookingResult(true, "Booking successful!")
            } else {
                _bookingResult.value = BookingResult(false, "Failed to create booking.")
            }
        }
    }
}
