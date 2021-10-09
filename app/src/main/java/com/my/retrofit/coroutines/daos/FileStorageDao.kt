package com.my.myapplication.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.my.myapplication.entity.FileStorage

@Dao
interface FileStorageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(fileStorage: FileStorage)

    @Query("SELECT * FROM FileStorage")
    fun getFiles():List<FileStorage>


    @Query("SELECT * FROM FileStorage WHERE isSyncedWith")
    fun getAllsynctedFiles():List<FileStorage>

}