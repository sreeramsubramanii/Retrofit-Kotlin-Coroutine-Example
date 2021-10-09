package com.my.myapplication.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileStorage(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val Filename: String,
    val fileType:String,
    val filePath:String,
    val isSyncedWith:Boolean
)
