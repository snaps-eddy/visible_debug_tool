package com.webling.debuglibrary.presentation

import com.airbnb.epoxy.EpoxyModel

interface EpoxyItem {

    fun buildEpoxyModel(): EpoxyModel<*>

}