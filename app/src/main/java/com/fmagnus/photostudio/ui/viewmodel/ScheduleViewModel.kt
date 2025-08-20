package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmagnus.photostudio.data.model.ScheduleSlot
import com.fmagnus.photostudio.data.repository.PhotoStudioRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class ScheduleViewModel : ViewModel() {

    private val repository = PhotoStudioRepository()

    private val _slots = MutableLiveData<List<ScheduleSlot>>()
    val slots: LiveData<List<ScheduleSlot>> = _slots

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchSlots(serviceId: String, date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val dateAsDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val slotList = repository.getScheduleSlots(serviceId, dateAsDate)
                _slots.value = slotList
            } catch (e: Exception) {
                _error.value = "Failed to load available slots. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
