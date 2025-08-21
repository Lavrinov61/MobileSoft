package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmagnus.photostudio.data.model.PhotoService
import com.fmagnus.photostudio.data.repository.PhotoStudioRepository
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {

    private val repository = PhotoStudioRepository()

    private val _service = MutableLiveData<PhotoService?>()
    val service: LiveData<PhotoService?> = _service

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val functions = com.google.firebase.functions.ktx.functions
    private val auth = com.google.firebase.auth.ktx.auth

    sealed class PaymentState {
        object Idle : PaymentState()
        object Processing : PaymentState()
        data class Success(val message: String) : PaymentState()
        data class Error(val message: String) : PaymentState()
    }

    private val _paymentState = MutableLiveData<PaymentState>(PaymentState.Idle)
    val paymentState: LiveData<PaymentState> = _paymentState

    fun fetchServiceDetails(serviceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val serviceDetails = repository.getServiceDetails(serviceId)
            _service.value = serviceDetails
            _isLoading.value = false
        }
    }

    fun startPaymentProcess(serviceId: String, slotId: String, userName: String, userPhone: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            val data = hashMapOf("serviceId" to serviceId, "userId" to auth.currentUser?.uid)

            functions.getHttpsCallable("createPaymentInvoice").call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        _paymentState.value = PaymentState.Error(task.exception?.message ?: "Failed to get payment details.")
                        return@continueWith null
                    }
                    val transactionId = (task.result?.data as? Map<*, *>)?.get("transactionId") as? String
                    if (transactionId != null) {
                        // TODO: Launch CloudPayments SDK with this transactionId.
                        // Assuming payment is successful, we now create the booking.
                        val bookingSuccess = repository.createBooking(
                            userId = auth.currentUser!!.uid,
                            serviceId = serviceId,
                            slotId = slotId,
                            userName = userName,
                            userPhone = userPhone
                        )
                        if (bookingSuccess) {
                             _paymentState.value = PaymentState.Success("Payment successful! Booking confirmed.")
                        } else {
                             _paymentState.value = PaymentState.Error("Payment was successful, but booking failed.")
                        }
                    } else {
                         _paymentState.value = PaymentState.Error("Could not retrieve transaction ID.")
                    }
                }
        }
    }
}
