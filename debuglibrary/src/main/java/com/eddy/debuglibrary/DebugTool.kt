package com.eddy.debuglibrary

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.eddy.debuglibrary.presentation.view.ui.overlay.OverlayTaskService
import com.eddy.debuglibrary.presentation.view.ui.permission.PermissionActivity
import com.eddy.debuglibrary.presentation.view.ui.permission.PermissionEvent
import com.eddy.debuglibrary.util.Constants
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_BOOLEAN_TOKEN
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
    private val sharedPreferences = context.getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)

    @RequiresPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun bindService() {
        if (getPermission()) {
            startService()
        }
    }

    fun unbindService() {
        if (isService) {
            context.unbindService(connection)
        }
    }

    private fun startService() {
        val intent = Intent(context, OverlayTaskService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun getPermission(): Boolean {

        val isNeverSeeAgain = sharedPreferences.getBoolean(EDDY_DEBUG_TOOL_BOOLEAN_TOKEN, false)

        if (builder.isAutoPermission && isNeverSeeAgain.not()) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(context, PermissionActivity::class.java)
                context.startActivity(intent)
                EventBus.getDefault().register(this)
                return false
            } else {
                EventBus.getDefault().unregister(this)
                return true
            }
        } else {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "권한 을 먼저 얻어 주세요.", Toast.LENGTH_SHORT).show()
                return false
            } else {
                return true
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as OverlayTaskService.OverlayDebugToolPopUpBinder
            isService = true

            myService = binder.getService()
            myService.setTagList(builder._searchKeyWords)
            myService.setUnBindServiceCallback(::unbindService)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isService = false
        }
    }


    @Subscribe
    internal fun permissionEventHandler(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Allow -> {
                Toast.makeText(context, "권한 허용", Toast.LENGTH_SHORT).show()
                startService()
            }
            PermissionEvent.Deny -> {
                Toast.makeText(context, "권한 거절.", Toast.LENGTH_SHORT).show()
            }
            PermissionEvent.NeverDeny -> {
                Toast.makeText(context, "권한 영구 거절.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public class Builder(private val context: Context) {

        private var searchKeyWords: List<String>? = null
        internal val _searchKeyWords: MutableList<String>
            get() {
                return searchKeyWords?.let {
                    mutableListOf("normal").run {
                        addAll(it)
                        this
                    }
                } ?: mutableListOf("normal")

            }
        internal var isAutoPermission: Boolean = false

        public fun setSearchKeyWord(keyWord: String): Builder = apply {
            this.searchKeyWords = listOf(keyWord)
        }

        public fun setSearchKeyWordList(keyWords: List<String>): Builder = apply {
            this.searchKeyWords = keyWords
        }

        public fun setJson(): Builder = apply {

        }

        public fun setJsonList(): Builder = apply {

        }

        public fun setAutoPermissionCheck(isAutoPermission: Boolean): Builder = apply {
            this.isAutoPermission = isAutoPermission
        }

        public fun build(): DebugTool = DebugTool(
            context = context,
            builder = this@Builder
        )

    }

}