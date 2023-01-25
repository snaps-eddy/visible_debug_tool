package com.eddy.debuglibrary.data.log

import com.eddy.debuglibrary.domain.log.LogRepository
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(
    private val remote: LogRemoteDataSource,
    private val local: LogLocalDataSource
): LogRepository {

    override fun getLogcatData(): Flow<String> {
        return remote.logDataCollect()
    }
}