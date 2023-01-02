package com.example.weblinglibrary

import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import androidx.lifecycle.LifecycleService

class OverlayDebugToolPopUp: LifecycleService()  {

    private val rootViewParams: WindowManager.LayoutParams by lazy {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
    }

    private val windowManager: WindowManager by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onCreate() {
        super.onCreate()

        windowManager.addView(rootView, rootViewParams)
    }

}