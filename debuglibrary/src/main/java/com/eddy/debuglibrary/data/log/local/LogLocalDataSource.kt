package com.eddy.debuglibrary.data.log.local

import kotlinx.coroutines.flow.Flow

internal interface LogLocalDataSource {
    fun logDataCollect(): Flow<String>
}