package com.webling.debuglibrary.presentation.ui

import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class OverlayDebugToolViewModel(
    private val getLogcatUseCase: GetLogcatUseCase
) {

    fun getLogCollect(searchTag: String): Flow<String?> = flow {
        emitAll(getLogcatUseCase.invoke(searchTag))
    }

}