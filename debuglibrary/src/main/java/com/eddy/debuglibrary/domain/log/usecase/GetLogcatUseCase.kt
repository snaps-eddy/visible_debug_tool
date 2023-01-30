package com.eddy.debuglibrary.domain.log.usecase

import com.eddy.debuglibrary.domain.base.UseCase
import com.eddy.debuglibrary.domain.log.LogRepository
import com.eddy.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

internal class GetLogcatUseCase(
    private val log: LogRepository
) : UseCase<LogModel, GetLogcatUseCase.Params>() {

    public override fun invoke(params: Params): Flow<LogModel> {
        return log.getLogcatData()
            .filter { it.content.contains(params.searchKeyWord) }
    }

    data class Params(
        val searchKeyWord: String
    )
}