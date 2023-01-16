package com.webling.debuglibrary.presentation.presenter

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleService

class OverlayTaskManager(
    private val context: Context
) : View.OnTouchListener {

    private lateinit var rootView: RelativeLayout
    private val windowManager: WindowManager by lazy { context.getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager }
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

    private var touchX = 0
    private var touchY = 0
    private var viewX = 0
    private var viewY = 0

    private val screenRatio = 3
    private val screenFullRatio = 1.5

    fun addViewToWindow() {
        windowManager.addView(rootView, rootViewParams)
    }

    fun destroyView() {
        windowManager.removeView(rootView)
    }

    fun updateView() {
        windowManager.updateViewLayout(rootView, rootViewParams)
    }

    fun bindView() {

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX.toInt()
                touchY = event.rawY.toInt()
                viewX = rootViewParams.x
                viewY = rootViewParams.y
            }
            MotionEvent.ACTION_MOVE -> {
                val x = (event.rawX - touchX).toInt()
                val y = (event.rawY - touchY).toInt()
                rootViewParams.x = viewX + x
                rootViewParams.y = viewY + y

                updateView()
            }
        }
        return false
    }

}