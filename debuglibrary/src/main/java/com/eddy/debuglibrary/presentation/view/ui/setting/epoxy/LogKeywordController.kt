package com.eddy.debuglibrary.presentation.view.ui.setting.epoxy

import com.airbnb.epoxy.TypedEpoxyController

internal class LogKeywordController : TypedEpoxyController<List<String>>() {
    override fun buildModels(data: List<String>?) {
        data?.forEachIndexed { index, content ->
            logKeywordItemView {
                id(modelCountBuiltSoFar)
                content(content)
            }
        }
    }
}