package com.eddy.debuglibrary.presentation.view

internal interface OverlayTaskCallback {
    val onClickClose: () -> Unit
    val onClickTagItem: (tag: String) -> Unit
    val onLongClickCloseService: () -> Unit
    val onClickClear: () -> Unit
}