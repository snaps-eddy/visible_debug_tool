package com.eddy.debuglibrary.presentation.view

interface OverlayTaskCallback {
    val onClickClose: () -> Unit
    val onClickTagItem: (tag: String) -> Unit
    val onLongClickCloseService: () -> Unit
}