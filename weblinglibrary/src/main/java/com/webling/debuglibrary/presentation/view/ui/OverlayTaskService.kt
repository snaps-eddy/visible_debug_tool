package com.webling.debuglibrary.presentation.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.weblinglibrary.R
import com.example.weblinglibrary.logItemView
import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.webling.debuglibrary.presentation.BindServiceCallback
import com.webling.debuglibrary.presentation.model.log.LogUiModel
import com.webling.debuglibrary.presentation.presenter.OverlayTaskContract
import com.webling.debuglibrary.presentation.presenter.OverlayTaskPresenter
import kotlinx.coroutines.*

@SuppressLint("ClickableViewAccessibility")
class OverlayTaskService : LifecycleService(), OverlayTaskContract.OverlayView {

    private lateinit var presenter: OverlayTaskContract.Presenter

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val binder = OverlayDebugToolPopUpBinder()

    init {
        lifecycle.addObserver(OverlayDebugObserver())
    }

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
    private val rootView: RelativeLayout by lazy { inflate.inflate(R.layout.webling_view_in_overlay_popup, null) as RelativeLayout }
    private val headerLayout: RelativeLayout by lazy { rootView.children.first() as RelativeLayout }
    private val rvLog: EpoxyRecyclerView by lazy { rootView.findViewById(R.id.rv_logs) }
    private val ivMove: ImageView by lazy { rootView.findViewById(R.id.iv_move) }
    private val btnMenu: ImageButton by lazy { rootView.findViewById(R.id.btn_menu) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }
    private val ivClose: ImageView by lazy { rootView.findViewById(R.id.iv_close) }
    private val cbZoom: CheckBox by lazy { rootView.findViewById(R.id.cb_zoom) }

    private var touchX = 0
    private var touchY = 0
    private var viewX = 0
    private var viewY = 0

    private val screenRatio = 3
    private val screenFullRatio = 1.5

    private lateinit var myJob: Job

    private val touchListener = View.OnTouchListener { v, event ->
        when (event.action) {
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
                windowManager.updateViewLayout(rootView, rootViewParams)
            }
        }
        return@OnTouchListener false
    }

    override fun onCreate() {
        super.onCreate()

        presenter = OverlayTaskPresenter(GetLogcatUseCase(), this)
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

        btnMenu.setOnClickListener {
            presenter.getLogData("eddy white")
        }

        rootView.setOnLongClickListener {
            unBindCallback.onUnbindService()
            true
        }

        tvLog.setOnClickListener {
            resizeView()
            rvLog.updateLayoutParams {
                width = Resources.getSystem().displayMetrics.widthPixels
                height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
            }

            presenter.getLogData("eddy gege")
        }

        ivMove.setOnTouchListener(touchListener)

        ivClose.setOnClickListener {
            restorationView()
        }

        cbZoom.setOnClickListener {
            if(cbZoom.isChecked) {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenFullRatio).toInt())
                }
            }else {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                }
            }
        }

    }

    private fun cancelJob() {
        if (this::myJob.isInitialized) {
            myJob.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rvLog.adapter = null
        windowManager.removeView(rootView)
    }

    var isScrollBottom = false
    override fun setLogData(uiModels: List<LogUiModel>) {
        cancelJob()
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                rvLog.withModels {
                    uiModels.forEachIndexed { index, logUiModel ->
                        logItemView {
                            id(index)
                            model(logUiModel)
                        }
                    }
                }
            }
            if (isScrollBottom) {
                rvLog.smoothScrollToPosition(uiModels.size - 1)
            }
        }
        rvLog.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            isScrollBottom = !rvLog.canScrollVertically(1)
        }
    }

    private fun addView() {
        ImageButton(this).apply {

        }
    }

    private fun restorationView() {
        ivClose.isVisible = false
        rvLog.isVisible = false
        cbZoom.isVisible = false
        cbZoom.isChecked = true

        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.LEFT_OF)
        moveLayoutParams.addRule(RelativeLayout.RIGHT_OF, tvLog.id)
        ivMove.layoutParams = moveLayoutParams


//        val headerLayout2: LinearLayout by lazy { rootView.children.find { it.id == R.id.rv_layout } as LinearLayout }
//        headerLayout2.removeAllViews()

        presenter.deInit()

        windowManager.updateViewLayout(rootView, rootViewParams)
    }

    private fun resizeView() {
        ivClose.isVisible = true
        rvLog.isVisible = true
        cbZoom.isVisible = true
        cbZoom.isChecked = false

        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.RIGHT_OF)
        moveLayoutParams.addRule(RelativeLayout.LEFT_OF, ivClose.id)
        ivMove.layoutParams = moveLayoutParams


//        val width = (Resources.getSystem().displayMetrics.density * 43).toInt()
//        val height = (Resources.getSystem().displayMetrics.density * 43).toInt()
//
//        ImageView(this).apply {
//            val params = RelativeLayout.LayoutParams(width, height)
//            params.addRule(RelativeLayout.LEFT_OF, ivMove.id)
//            setImageResource(R.drawable.move)
//            tag = "btn_move"
//            layoutParams = params
//        }.run { headerLayout.addView(this) }

    }

    internal fun setUnBindServiceCallback(callback: BindServiceCallback) {
        this.unBindCallback = callback
    }

}