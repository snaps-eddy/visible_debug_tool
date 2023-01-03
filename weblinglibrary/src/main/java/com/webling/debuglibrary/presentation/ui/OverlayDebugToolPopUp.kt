package com.webling.debuglibrary.presentation.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.lifecycle.LifecycleService
import com.example.weblinglibrary.R


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

    private val inflate: LayoutInflater by lazy {
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val rootView: View by lazy {
        inflate.inflate(R.layout.webling_view_in_overlay_popup_service, null)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { createView(it) } ?: Service.START_STICKY

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createView(intent: Intent) {

        windowManager.addView(rootView, rootViewParams)

        rootView.findViewById<ImageButton>(R.id.btn_close).setOnClickListener{ v ->
            stopSelf()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("test","eddy test  stop이 되는거니?")
        windowManager.removeView(rootView)
    }
}