package com.eddy.debuglibrary.domain.log.usecase

import com.eddy.debuglibrary.domain.base.UseCase
import com.eddy.debuglibrary.domain.log.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

class GetLogcatUseCase(
    private val log: LogRepository
) : UseCase<String, String> {

    override fun invoke(params: String): Flow<String> {
        return log.getLogcatData().filter { it.contains(params) }
//            .map {
//                buildString {
//                    when {
//                        it.contains(params) -> {
//                            append(it).appendLine()
//                        }
//                        else -> null
//                    }
//                }
//            }
    }

}