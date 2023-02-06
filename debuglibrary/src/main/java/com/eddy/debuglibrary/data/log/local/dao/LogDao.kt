package com.eddy.debuglibrary.data.log.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.eddy.debuglibrary.data.log.entity.LogEntity

@Dao
internal interface LogDao {
    @Query("SELECT * FROM logEntity")
    fun getAllLog(): List<LogEntity>

    @Insert
    fun insertAllLog(logEntities: List<LogEntity>)

    @Insert
    fun insertLog(logEntity: LogEntity)

    @Query("DELETE FROM logEntity")
    fun deleteAllLog()
}