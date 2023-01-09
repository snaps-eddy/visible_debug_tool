package com.webling.debuglibrary.presentation.presenter

import android.view.View
import com.webling.debuglibrary.presentation.model.log.LogUiModel


interface OverlayTaskContract {

    interface OverlayView {
        fun setLogData(uiModels: List<LogUiModel>)
    }

    interface Presenter {
        fun deInit()
        fun getLogData(searchTag: String?)
    }

}
