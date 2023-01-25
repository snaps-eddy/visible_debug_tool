package com.eddy.debuglibrary.presentation.viewmodel

import com.eddy.debuglibrary.presentation.base.UiEffect
import com.eddy.debuglibrary.presentation.base.UiEvent
import com.eddy.debuglibrary.presentation.base.UiState
import com.eddy.debuglibrary.presentation.model.log.LogUiModel

class OverlayContract {

    sealed interface Event : UiEvent {
        object OnCloseClick: Event
        data class OnClickTagItem(val tag: String) : Event
        data class OnSearchLog(val word: String): Event
    }

    data class State(
        val logsState: LogsState
    ) : UiState

    sealed interface LogsState {
        object Idle : LogsState
    }


    sealed interface SideEffect : UiEffect {
        data class FetchLogs(val log : LogUiModel) : SideEffect
        data class SearchLog(val word: String): SideEffect
    }

}

