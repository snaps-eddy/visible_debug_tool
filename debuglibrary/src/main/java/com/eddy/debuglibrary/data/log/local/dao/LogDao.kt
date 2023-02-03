package com.eddy.debuglibrary.data.log.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.eddy.debuglibrary.data.log.entity.Log

@Dao
internal interface LogDao {
    @Query("SELECT * FROM log")
    fun getAllLog(): List<Log>

    @Insert
    fun insertAllLog(logs: List<Log>)

    @Insert
    fun insertLog(log: Log)

    @Query("DELETE FROM log")
    fun deleteAllLog()
}