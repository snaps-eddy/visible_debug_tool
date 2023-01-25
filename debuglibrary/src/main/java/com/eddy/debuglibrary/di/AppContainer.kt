package com.eddy.debuglibrary.di

import com.eddy.debuglibrary.data.log.LogLocalDataSourceImpl
import com.eddy.debuglibrary.data.log.LogRemoteDataSourceImpl
import com.eddy.debuglibrary.data.log.LogRepositoryImpl
import com.eddy.debuglibrary.data.log.LogcatCollector
import com.eddy.debuglibrary.domain.log.usecase.GetLogcatUseCase

class AppContainer {

    private val logcatCollector = LogcatCollector()
    private val logRemoteDataSource = LogRemoteDataSourceImpl(logcatCollector)
    private val logLocalDataSource = LogLocalDataSourceImpl()

    private val logRepository = LogRepositoryImpl(logRemoteDataSource, logLocalDataSource)

    val getLogcatUseCase = GetLogcatUseCase(logRepository)

}