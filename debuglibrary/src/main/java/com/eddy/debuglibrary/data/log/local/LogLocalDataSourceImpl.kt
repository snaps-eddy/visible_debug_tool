package com.eddy.debuglibrary.data.log.local

import com.eddy.debuglibrary.data.log.entity.Log
import com.eddy.debuglibrary.data.log.local.dao.LogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class LogLocalDataSourceImpl(
    private val logDao: LogDao
) : LogLocalDataSource {

    override fun insertLog(log: Log) {
        logDao.insertLog(log)
    }

    override fun getAllLog(): Flow<List<Log>> = flow {
        emit(logDao.getAllLog())
    }

    override fun insertAllLog(logs: List<Log>) {
        logDao.insertAllLog(logs)
    }

    override fun deleteAllLog() {
        logDao.deleteAllLog()
    }

}