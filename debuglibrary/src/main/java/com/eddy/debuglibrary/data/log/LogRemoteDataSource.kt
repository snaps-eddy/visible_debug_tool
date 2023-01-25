package com.eddy.debuglibrary.data.log

import kotlinx.coroutines.flow.Flow

interface LogRemoteDataSource {
    fun logDataCollect(): Flow<String>
}