package com.eddy.debuglibrary.data.log

import com.eddy.debuglibrary.data.log.remote.LogRemoteDataSource
import com.eddy.debuglibrary.domain.log.LogRepository
import com.eddy.debuglibrary.domain.log.model.LogLevel
import com.eddy.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LogRepositoryImpl(
    private val remote: LogRemoteDataSource
): LogRepository {

    override fun getLogcatData(): Flow<LogModel> {
        return remote.logDataCollect()
            .map {
                when {
                    it.contains(LogLevel.V.containWord) -> { LogModel(content = it, logLevel = LogLevel.V) }
                    it.contains(LogLevel.D.containWord) -> { LogModel(content = it, logLevel = LogLevel.D) }
                    it.contains(LogLevel.I.containWord) -> { LogModel(content = it, logLevel = LogLevel.I) }
                    it.contains(LogLevel.W.containWord) -> { LogModel(content = it, logLevel = LogLevel.W) }
                    it.contains(LogLevel.E.containWord) -> { LogModel(content = it, logLevel = LogLevel.E) }
                    it.contains(LogLevel.F.containWord) -> { LogModel(content = it, logLevel = LogLevel.F) }
                    it.contains(LogLevel.S.containWord) -> { LogModel(content = it, logLevel = LogLevel.S) }
                    else ->  { LogModel(content = it, logLevel = LogLevel.S) }
                }
            }
    }
}