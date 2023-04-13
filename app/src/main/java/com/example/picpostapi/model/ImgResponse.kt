package com.example.picpostapi.model

import com.google.gson.annotations.SerializedName


data class ImgResponse(

    @field:SerializedName("FileExt")
    val fileExt: String? = null,

    @field:SerializedName("FileName")
    val fileName: String? = null,

    @field:SerializedName("FileId")
    val fileId: String? = null,

    @field:SerializedName("Url")
    val url: String? = null,

    @field:SerializedName("FileSize")
    val fileSize: Int? = null
)
