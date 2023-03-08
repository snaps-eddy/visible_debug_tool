package com.eddy.debuglibrary.presentation.view.ui.setting.viewmodel

import com.eddy.debuglibrary.presentation.base.BaseViewModel
import com.eddy.debuglibrary.presentation.view.ui.setting.SettingActivity
import com.eddy.debuglibrary.presentation.view.ui.setting.SettingContract

internal class SettingViewModel : BaseViewModel<SettingContract.Event, SettingContract.State, SettingContract.SideEffect>(), SettingActivity.Callback {

    override fun createInitialState(): SettingContract.State {
        return SettingContract.State(SettingContract.SettingState.Init)
    }

    override fun handleEvent(event: SettingContract.Event) {

    }

    private fun onClickDeleteKeyword(keyword: String) {
        setEffect { SettingContract.SideEffect.DeleteKeyword(keyword) }
    }

    override val onClickDeleteKeyword: (keyword: String) -> Unit get() = ::onClickDeleteKeyword

}