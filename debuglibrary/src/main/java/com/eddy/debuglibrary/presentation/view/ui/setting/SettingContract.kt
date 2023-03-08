package com.eddy.debuglibrary.presentation.view.ui.setting

import com.eddy.debuglibrary.presentation.base.UiEffect
import com.eddy.debuglibrary.presentation.base.UiEvent
import com.eddy.debuglibrary.presentation.base.UiState

internal class SettingContract {

    sealed interface Event : UiEvent {

    }

    data class State(
        val state: SettingState
    ) : UiState

    sealed interface SettingState {
        object Init : SettingState
    }


    sealed interface SideEffect : UiEffect {
        data class DeleteKeyword(val keyword: String): SideEffect
    }
}