package com.eddy.debuglibrary.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.eddy.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.eddy.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.eddy.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.eddy.debuglibrary.presentation.base.BaseViewModel
import com.eddy.debuglibrary.presentation.view.model.LogForm
import com.eddy.debuglibrary.presentation.view.model.LogUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class OverlayTaskViewModel(
    private val getLogcatUseCase: GetLogcatUseCase,
    private val clearLogUseCase: ClearLogUseCase,
    private val deleteLogUseCase: DeleteLogUseCase
) : BaseViewModel<OverlayContract.Event, OverlayContract.State, OverlayContract.SideEffect>() {

    private lateinit var job: Job
    private lateinit var logForms: List<LogForm>

    fun requestLogcats(searchTag: String) {
        cancelJob()
        clearLog()

        job = viewModelScope.launch {

            val params = GetLogcatUseCase.Params(searchTag.ifEmpty { "normal" })
            getLogcatUseCase.invoke(params)
                .map { it.map { LogUiModel(it.content, it.logLevel) } }
                .collect {
                    setEffect { OverlayContract.SideEffect.FetchLogs(it) } }
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

    private fun clearLog() {
        viewModelScope.launch {
            clearLogUseCase.run(Unit)
        }
    }

    private fun deleteLog() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteLogUseCase.run(Unit)
        }
    }

    override fun handleEvent(event: OverlayContract.Event) {
        when (event) {
            is OverlayContract.Event.OnCloseClick -> {
                setState { copy(logsState = OverlayContract.LogsState.Idle) }
            }
            is OverlayContract.Event.OnClickKeyWordItem -> {
                requestLogcats(event.keyWord)
            }
            is OverlayContract.Event.OnSearchLog -> {
                setEffect { OverlayContract.SideEffect.SearchLog(event.keyWord) }
            }
            is OverlayContract.Event.OnClearClick -> {
                clearLog()
            }
            is OverlayContract.Event.DeleteLog -> {
                deleteLog()
            }
            is OverlayContract.Event.ApplyLogForm -> {
                logForms = event.logForm
            }
        }
    }

}
