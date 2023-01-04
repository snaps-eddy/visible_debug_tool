package com.webling.debuglibrary.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.RequiresPermission
import com.webling.debuglibrary.presentation.ui.OverlayDebugToolPopUpService

class WeblingDebugTool(
    private val context: Context,
) : BindServiceCallback {

    private lateinit var myService: OverlayDebugToolPopUpService
    private var isService = false
    private var isCollect = false
    private lateinit var searchTag: String

    @RequiresPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun bindService() {
        val intent = Intent(context, OverlayDebugToolPopUpService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isService) {
            context.unbindService(connection)
        }
    }



    fun onLogCollect(searchTag: String) {
        isCollect = true
        this.searchTag = searchTag
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as OverlayDebugToolPopUpService.OverlayDebugToolPopUpBinder
            myService = binder.getService()
            isService = true

            myService.logCollect(searchTag)
            myService.setUnBindServiceCallback(this@WeblingDebugTool)
        }

        // 정상적으로 연결 해제되었을 때는 호출되지 않고, 비정상적으로 서비스가 종료되었을 때만 호출된다.
        override fun onServiceDisconnected(name: ComponentName) {
            isService = false
        }
    }

    override fun onUnbindService() {
        unbindService()
    }
}