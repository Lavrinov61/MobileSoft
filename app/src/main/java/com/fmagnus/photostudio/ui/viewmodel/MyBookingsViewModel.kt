package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmagnus.photostudio.data.model.Booking
import com.fmagnus.photostudio.data.repository.PhotoStudioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MyBookingsViewModel : ViewModel() {

    private val repository = PhotoStudioRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchUserBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val bookingList = repository.getUserBookings(userId)
                _bookings.value = bookingList
            }
            _isLoading.value = false
        }
    }
}
