package com.webling.debuglibrary.domain.log.usecase

import com.webling.debuglibrary.domain.log.LogcatCollector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLogcatUseCase : UseCase<String, String> {

    private val logcatCollector = LogcatCollector()

    override fun invoke(params: String): Flow<String> {
        return logcatCollector.collect(params)
            .map {
                var line: String
                buildString {
                    when {
                        it.contains(params) -> {
                            append(it).appendLine()
                        }
                        else -> null
                    }
                }
            }
    }

}