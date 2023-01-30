package com.eddy.debuglibrary.domain.log

import com.eddy.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.flow.Flow

internal interface LogRepository {

    fun getLogcatData(): Flow<LogModel>

}