package com.eddy.debuglibrary.domain.log

import kotlinx.coroutines.flow.Flow

interface LogRepository {

    fun getLogcatData(): Flow<String>

}