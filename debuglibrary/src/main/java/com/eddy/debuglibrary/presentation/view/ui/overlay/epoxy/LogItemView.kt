package com.eddy.debuglibrary.presentation.view.ui.overlay.epoxy

import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.eddy.debuglibrary.presentation.base.KotlinEpoxyHolder
import com.example.debuglibrary.R

@EpoxyModelClass
internal abstract class LogItemView : EpoxyModelWithHolder<LogItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.epoxy_log_item_view

    @EpoxyAttribute
    lateinit var content: String

    @EpoxyAttribute
    var contentColor: Int = 0

    override fun bind(holder: LogItemViewHolder) {
        with(holder) {
            textView.apply {
                text = content
                setTextColor(contentColor)
            }
        }
    }
}

internal class LogItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<LinearLayout>(R.id.root)
    val textView by bind<TextView>(R.id.tv_content)
}