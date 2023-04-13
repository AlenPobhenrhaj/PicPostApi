package com.example.picpostapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.picpostapi.data.APIService

class CaptureViewModelFactory(private val apiService: APIService) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaptureViewModel::class.java)) {
            return CaptureViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}