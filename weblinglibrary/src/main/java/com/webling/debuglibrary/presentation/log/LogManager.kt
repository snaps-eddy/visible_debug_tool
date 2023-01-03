package com.webling.debuglibrary.presentation.log

import android.content.Context
import android.content.Intent
import androidx.annotation.LayoutRes
import com.webling.debuglibrary.presentation.Manager
import com.webling.debuglibrary.presentation.ui.OverlayDebugToolPopUp

class LogManager(
    private val context: Context,
) : Manager {

    override fun startService() {
        val intent = Intent(context, OverlayDebugToolPopUp::class.java)
        context.startService(intent)
    }

    companion object {
        const val RESOURCE_ID = "RESOURCE_ID"
    }
}