package com.my.retrofit.coroutines.data.repository

import com.my.retrofit.coroutines.data.api.ApiHelper
import com.my.retrofit.coroutines.data.api.ApiService
import com.my.retrofit.coroutines.data.model.FileStorageEntity

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getUsers() = apiHelper.getUser()

    suspend fun postInternalStorageMainHelper(fileStorageEntity: FileStorageEntity) =
        apiHelper.postInternalStorageFileHelper(fileStorageEntity)
}