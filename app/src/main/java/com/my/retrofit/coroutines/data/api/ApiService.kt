package com.my.retrofit.coroutines.data.api

import com.my.retrofit.coroutines.data.model.FileStorageEntity
import com.my.retrofit.coroutines.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("user")
    suspend fun getUser(): List<User>

    @POST("api/users")
    suspend fun postInternalStorageFile(@Body fileStorageEntity: FileStorageEntity)


}