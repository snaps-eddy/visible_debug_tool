package com.eddy.debuglibrary.domain.log.usecase

import com.eddy.debuglibrary.domain.base.UseCase
import com.eddy.debuglibrary.domain.log.LogRepository

internal class ClearLogUseCase(
    private val log: LogRepository
) : UseCase<Unit, Unit>() {

    public override suspend fun run(params: Unit) {
        log.clearLog()
    }
}