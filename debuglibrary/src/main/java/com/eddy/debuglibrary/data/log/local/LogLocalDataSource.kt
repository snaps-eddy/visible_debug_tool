package com.eddy.debuglibrary.data.log.local

import com.eddy.debuglibrary.data.log.entity.Log
import kotlinx.coroutines.flow.Flow

internal interface LogLocalDataSource {
    fun insertLog(log: Log)
    fun getAllLog(): Flow<List<Log>>
    fun insertAllLog(logs: List<Log>)
    fun deleteAllLog()

}