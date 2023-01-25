package com.eddy.debuglibrary.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.RequiresPermission
import com.eddy.debuglibrary.presentation.view.ui.OverlayTaskService

public inline fun createDebugTool(
    context: Context,
    crossinline block: DebugTool.Builder.() -> Unit
): DebugTool = DebugTool.Builder(context).apply(block).build()

class DebugTool private constructor(
    private val context: Context,
    private val builder: Builder
) {

    private lateinit var myService: OverlayTaskService
    private var isService = false

    @RequiresPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun bindService() {
        val intent = Intent(context, OverlayTaskService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isService) {
            context.unbindService(connection)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as OverlayTaskService.OverlayDebugToolPopUpBinder
            isService = true

            myService = binder.getService()
            myService.setTagList(builder.tags)
            myService.setUnBindServiceCallback(::unbindService)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isService = false
        }
    }

    public class Builder(private val context: Context) {

        public var tags: List<String>? = null

        public fun setTag(tag: String): Builder = apply {
            this.tags = listOf(tag)
        }

        public fun setTagList(tags: List<String>): Builder = apply {
            this.tags = tags
        }

        public fun setJson(): Builder = apply {

        }

        public fun setJsonList(): Builder = apply {

        }

        public fun build(): DebugTool = DebugTool(
            context = context,
            builder = this@Builder
        )

    }

}