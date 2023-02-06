package com.eddy.debuglibrary.data.log.local

import com.eddy.debuglibrary.data.log.entity.LogEntity
import kotlinx.coroutines.flow.Flow

internal interface LogLocalDataSource {
    fun insertLog(logEntity: LogEntity)
    fun getAllLog(): Flow<List<LogEntity>>
    fun insertAllLog(logEntities: List<LogEntity>)
    fun deleteAllLog()

}