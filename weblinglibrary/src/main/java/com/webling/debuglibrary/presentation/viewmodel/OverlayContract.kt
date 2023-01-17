package com.webling.debuglibrary.presentation.viewmodel

import com.webling.debuglibrary.presentation.base.UiEffect
import com.webling.debuglibrary.presentation.base.UiEvent
import com.webling.debuglibrary.presentation.base.UiState
import com.webling.debuglibrary.presentation.model.log.LogUiModel

class OverlayContract {

    sealed interface Event : UiEvent {
        object OnCloseClick: Event
        data class OnClickTagItem(val tag: String = "") : Event
    }

    data class State(
        val logsState: LogsState
    ) : UiState

    sealed interface LogsState {
        object Idle : LogsState
        object Loading: LogsState
        object Success: LogsState
    }


    sealed interface SideEffect : UiEffect {
        data class FetchLogs(val log : LogUiModel) : SideEffect
    }

}

