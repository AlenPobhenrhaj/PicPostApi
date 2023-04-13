package com.example.picpostapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picpostapi.data.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CaptureViewModel(private val apiService: APIService) : ViewModel() {

    fun uploadImage(file: File, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.uploadFile(file.toMultipartBody())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.url?.let { onSuccess(it) }
                } else {
                    onFailure()
                }
            }
        }
    }

    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, requestBody)
    }
}
