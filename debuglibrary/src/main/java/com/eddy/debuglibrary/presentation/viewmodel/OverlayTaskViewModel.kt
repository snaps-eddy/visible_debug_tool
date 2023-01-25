package com.eddy.debuglibrary.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.eddy.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.eddy.debuglibrary.presentation.base.BaseViewModel
import com.eddy.debuglibrary.presentation.model.log.LogUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class OverlayTaskViewModel(
    private val getLogcatUseCase: GetLogcatUseCase,
) : BaseViewModel<OverlayContract.Event, OverlayContract.State, OverlayContract.SideEffect>() {

    private lateinit var job: Job

    fun requestLogcats(searchTag: String) {
        cancelJob()

        job = viewModelScope.launch {
            getLogcatUseCase.invoke(searchTag.ifEmpty { "" })
                .filter { it.isNotEmpty() }
                .collect {
                    setEffect { OverlayContract.SideEffect.FetchLogs(LogUiModel(it)) }
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
