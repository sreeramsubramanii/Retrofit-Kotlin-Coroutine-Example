package com.my.retrofit.coroutines.data.api

import com.my.retrofit.coroutines.data.model.FileStorageEntity

class ApiHelper(private val apiService: ApiService) {
    suspend fun getUser() = apiService.getUser()

    suspend fun postInternalStorageFileHelper(fileStorageEntity: FileStorageEntity)=apiService.postInternalStorageFile(fileStorageEntity)
}