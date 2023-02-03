package com.eddy.debuglibrary.presentation.view.ui.overlay

internal interface OverlayTaskCallback {
    val onClickClose: () -> Unit
    val onClickTagItem: (tag: String) -> Unit
    val onLongClickCloseService: () -> Unit
    val onClickClear: () -> Unit
}