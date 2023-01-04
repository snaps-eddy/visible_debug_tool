package com.webling.debuglibrary.presentation.ui

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.weblinglibrary.R
import com.example.weblinglibrary.databinding.WeblingViewInOverlayPopupServiceBinding
import com.example.weblinglibrary.logItemView
import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.webling.debuglibrary.presentation.BindServiceCallback
import com.webling.debuglibrary.presentation.log.LogUiModel
import kotlinx.coroutines.launch


class OverlayDebugToolPopUpService : LifecycleService() {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayDebugToolPopUpService {
            return this@OverlayDebugToolPopUpService
        }
    }

    val binder = OverlayDebugToolPopUpBinder()

    init {
        lifecycle.addObserver(OverlayDebugObserver())
    }

    private val vm = OverlayDebugToolViewModel(GetLogcatUseCase())

    private val logUiModels = mutableListOf<LogUiModel>()

    private lateinit var unBindCallback: BindServiceCallback

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

    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val inflate: LayoutInflater by lazy { getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }
    private val rootView: View by lazy { inflate.inflate(R.layout.webling_view_in_overlay_popup_service, null) }
    private val rvLog: EpoxyRecyclerView by lazy { rootView.findViewById(R.id.rv_logs) }
    private val btnClose: ImageButton by lazy { rootView.findViewById(R.id.btn_close) }
    private val btnMenu: ImageButton by lazy { rootView.findViewById(R.id.btn_menu) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }

    override fun onCreate() {
        super.onCreate()
        windowManager.addView(rootView, rootViewParams)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        bindView(intent)
        return binder
    }

    private fun bindView(intent: Intent) {
        rvLog.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        btnMenu.setOnClickListener { v ->
            Log.d("eddy test", " 메뉴 클릭....")
        }

        tvLog.setOnClickListener {
            Log.d("eddy test", "eddy test  이거뭐냐!!")
        }

        btnClose.setOnClickListener {
            Log.d("test","Eddy test ㅇ 언바인드 눌러써여")
            unBindCallback.onUnbindService()
        }
    }

    fun logCollect(searchTag: String) {
        Log.d("test","eddy test d =durl dksksk")
        lifecycleScope.launch {
            var isScrollBottom = false
            vm.getLogCollect(searchTag).collect {
                if (!it.isNullOrEmpty()) {
                    logUiModels.add(LogUiModel(it))

                    rvLog.withModels {
                        logUiModels.forEachIndexed { index, logUiModel ->
                            logItemView {
                                id(index)
                                model(logUiModel)
                            }
                        }
                    }
                    if (isScrollBottom) rvLog.smoothScrollToPosition(logUiModels.size - 1)
                }
                rvLog.setOnScrollChangeListener { view, i, i2, i3, i4 ->
                    isScrollBottom = !rvLog.canScrollVertically(1)
                }
            }
        }
    }

    fun setUnBindServiceCallback(callback: BindServiceCallback) {
        Log.d("Test","eddy test aa 콜백 됨? $callback")
        this.unBindCallback = callback
    }

    override fun onDestroy() {
        super.onDestroy()
        rvLog.adapter = null
        windowManager.removeView(rootView)
    }


}