package com.eddy.debuglibrary.data.log

import kotlinx.coroutines.flow.Flow

interface LogLocalDataSource {
    fun logDataCollect(): Flow<String>
}