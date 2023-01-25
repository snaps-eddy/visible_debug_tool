package com.eddy.debuglibrary.data.log

import kotlinx.coroutines.flow.Flow

class LogRemoteDataSourceImpl(
    private val log: LogcatCollector
) : LogRemoteDataSource {
    override fun logDataCollect(): Flow<String> {
        return log.collect()
    }
}