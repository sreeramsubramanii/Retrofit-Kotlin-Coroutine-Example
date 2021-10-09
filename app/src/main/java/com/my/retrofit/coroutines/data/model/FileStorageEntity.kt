package com.my.retrofit.coroutines.data.model

data class FileStorageEntity(

    val Filename: String,
    val fileType: String,
    val filePath: String,
    val isSyncedWith: Boolean

)
