package com.eddy.debuglibrary.di

import android.content.Context
import android.content.SharedPreferences
import com.eddy.debuglibrary.data.datastore.DataStoreRepositoryImpl
import com.eddy.debuglibrary.data.datastore.local.DataStoreLocalDataSourceImpl
import com.eddy.debuglibrary.data.log.LogRepositoryImpl
import com.eddy.debuglibrary.data.log.remote.LogRemoteDataSourceImpl
import com.eddy.debuglibrary.data.log.remote.LogcatCollector
import com.eddy.debuglibrary.domain.datastore.usecase.GetNeverSeeAgainUseCase
import com.eddy.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.eddy.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL

internal class AppContainer(context: Context) {

    private val logcatCollector = LogcatCollector()
    private val logRemoteDataSource = LogRemoteDataSourceImpl(logcatCollector)

    private val logRepository = LogRepositoryImpl(logRemoteDataSource)

    val getLogcatUseCase = GetLogcatUseCase(logRepository)


    private val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences(EDDY_DEBUG_TOOL, Context.MODE_PRIVATE) }
    private val dataStoreLocalDataSource: DataStoreLocalDataSourceImpl by lazy { DataStoreLocalDataSourceImpl(sharedPreferences) }
    private val dataStoreRepository: DataStoreRepositoryImpl by lazy { DataStoreRepositoryImpl(dataStoreLocalDataSource) }
    val getNeverSeeAgainUseCase: GetNeverSeeAgainUseCase by lazy { GetNeverSeeAgainUseCase(dataStoreRepository) }
    val writeDataStoreUseCase: WriteDataStoreUseCase by lazy { WriteDataStoreUseCase(dataStoreRepository) }

}