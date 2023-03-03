package com.eddy.debuglibrary.presentation.view.ui.setting.epoxy

import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.eddy.debuglibrary.presentation.base.KotlinEpoxyHolder
import com.example.debuglibrary.R

@EpoxyModelClass
internal abstract class LogKeywordItemView : EpoxyModelWithHolder<LogFilterKeywordItemViewHolder>() {
    override fun getDefaultLayout(): Int = R.layout.epoxy_log_filter_keyword_item_view

    @EpoxyAttribute
    lateinit var content: String

    override fun bind(holder: LogFilterKeywordItemViewHolder) {
        with(holder) {
            textView.apply {
                text = content
            }
        }
    }
}


internal class LogFilterKeywordItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<LinearLayout>(R.id.root)
    val textView by bind<TextView>(R.id.tv_content)
}