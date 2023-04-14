package com.example.picpostapi.viewmodel


import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CaptureViewModel(private val apiService: APIService) : ViewModel() {

    var imageUri: Uri? = null

    fun createImageFile(context: Context): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            File.createTempFile(imageFileName, ".jpg", storageDir).apply {
                deleteOnExit()
            }.also { file ->
                imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
            }
            imageUri
        } catch (ex: IOException) {
            null
        }
    }


    fun getPathFromUri(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                return it.getString(columnIndex)
            }
        }
        return null
    }

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

