package com.example.picpostapi.data

import com.example.picpostapi.model.ImgResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {

    companion object{
        const val BASE_URL = "https://v2.convertapi.com/"
    }

    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part body: MultipartBody.Part): Response<ImgResponse>

}