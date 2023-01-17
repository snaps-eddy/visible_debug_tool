package com.webling.debuglibrary.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.webling.debuglibrary.presentation.view.ui.OverlayTaskService

class WeblingDebugTool(
    private val context: Context,
) : BindServiceCallback {

    private lateinit var myService: OverlayTaskService
    private var isService = false
    private var isCollect = false
    private var searchTag: String? = null

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

    fun onLogCollect(searchTag: String? = null) {
        isCollect = true
        this.searchTag = searchTag
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as OverlayTaskService.OverlayDebugToolPopUpBinder
            myService = binder.getService()
            isService = true

            myService.setUnBindServiceCallback(this@WeblingDebugTool)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isService = false
        }
    }

   override fun onUnbindService() {
        unbindService()
    }

}