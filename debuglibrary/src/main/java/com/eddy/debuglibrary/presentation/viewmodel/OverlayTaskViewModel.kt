package com.eddy.debuglibrary.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.eddy.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.eddy.debuglibrary.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class OverlayTaskViewModel(
    private val getLogcatUseCase: GetLogcatUseCase,
) : BaseViewModel<OverlayContract.Event, OverlayContract.State, OverlayContract.SideEffect>() {

    private lateinit var job: Job

    fun requestLogcats(searchTag: String) {
        cancelJob()

        job = viewModelScope.launch {

            val params = GetLogcatUseCase.Params(searchTag.ifEmpty { "normal" })
            getLogcatUseCase.invoke(params)
                .collect {
                    setEffect { OverlayContract.SideEffect.FetchLogs(it) }
                }
        }
    }

    private fun cancelJob() {
        if (this::job.isInitialized) {
            job.cancel()
        }
    }

    override fun createInitialState(): OverlayContract.State {
        return OverlayContract.State(OverlayContract.LogsState.Idle)
    }

    override fun handleEvent(event: OverlayContract.Event) {
        when (event) {
            is OverlayContract.Event.OnCloseClick -> {
                setState { copy(logsState = OverlayContract.LogsState.Idle) }
            }
            is OverlayContract.Event.OnClickTagItem -> {
                requestLogcats(event.tag)
            }
            is OverlayContract.Event.OnSearchLog -> {
                setEffect { OverlayContract.SideEffect.SearchLog(event.word) }
            }
        }
    }

}
