package com.fmagnus.photostudio.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmagnus.photostudio.data.model.PhotoService
import com.fmagnus.photostudio.data.repository.PhotoStudioRepository
import kotlinx.coroutines.launch

class ServicesViewModel : ViewModel() {

    private val repository = PhotoStudioRepository()

    private val _services = MutableLiveData<List<PhotoService>>()
    val services: LiveData<List<PhotoService>> = _services

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchServices() {
        viewModelScope.launch {
            _isLoading.value = true
            val serviceList = repository.getPhotoServices()
            _services.value = serviceList
            _isLoading.value = false
        }
    }
}
