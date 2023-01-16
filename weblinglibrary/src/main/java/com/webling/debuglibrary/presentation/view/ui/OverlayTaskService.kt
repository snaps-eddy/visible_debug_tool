package com.webling.debuglibrary.presentation.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weblinglibrary.R
import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.webling.debuglibrary.presentation.BindServiceCallback
import com.webling.debuglibrary.presentation.viewmodel.OverlayContract
import com.webling.debuglibrary.presentation.viewmodel.OverlayTaskViewModel
import kotlinx.coroutines.*

@SuppressLint("ClickableViewAccessibility")
class OverlayTaskService : LifecycleService() {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val viewModel: OverlayTaskViewModel = OverlayTaskViewModel(GetLogcatUseCase())

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
    private val svLog: NestedScrollView by lazy { rootView.findViewById(R.id.sv_logs) }
    private val logContainer: LinearLayout by lazy { rootView.findViewById(R.id.log_view_container) }
    private val ivMove: ImageView by lazy { rootView.findViewById(R.id.iv_move) }
    private val btnMenu: ImageButton by lazy { rootView.findViewById(R.id.btn_menu) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }
    private val ivClose: ImageView by lazy { rootView.findViewById(R.id.iv_close) }
    private val cbZoom: CheckBox by lazy { rootView.findViewById(R.id.cb_zoom) }
    private val spLog: Spinner by lazy { rootView.findViewById(R.id.sp_log) }

    private var touchX = 0
    private var touchY = 0
    private var viewX = 0
    private var viewY = 0

    private val screenRatio = 3
    private val screenFullRatio = 1.5

    private val viewMoveListener = View.OnTouchListener { _, event ->
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

    private val logSelectorListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tvLog.text = logEvents[position]
            viewModel.requestLogcats(logEvents[position])
            logContainer.removeAllViews()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    val logEvents = listOf("All", "API", "WEB_VIEW", "DATA", "eddy white", "eddy gege")

    private var isScrollBottom = false

    override fun onCreate() {
        super.onCreate()

        initObservers()

        svLog.viewTreeObserver.addOnGlobalLayoutListener {
            svLog.post {
                if(isScrollBottom) {
                    svLog.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }
        svLog.setOnScrollChangeListener { _, _, _, _, _ ->
            isScrollBottom = !svLog.canScrollVertically(1)
        }

        windowManager.addView(rootView, rootViewParams)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        bindView(intent)
        return binder
    }

    private fun bindView(intent: Intent) {

        btnMenu.setOnClickListener {
        }

        rootView.setOnLongClickListener {
            unBindCallback.onUnbindService()
            true
        }

        tvLog.setOnClickListener {
            viewModel.setEvent(OverlayContract.Event.OnTagItemClick())
        }

        ivMove.setOnTouchListener(viewMoveListener)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logEvents)
        spLog.adapter = adapter
        spLog.onItemSelectedListener = logSelectorListener

        ivClose.setOnClickListener {
            viewModel.setEvent(OverlayContract.Event.OnCloseClick)
        }

        cbZoom.setOnClickListener {
            if (cbZoom.isChecked) {
                svLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenFullRatio).toInt())
                }
            } else {
                svLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                }
            }
        }

    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (val state = it.logsState) {
                        OverlayContract.LogsState.Idle -> {
                            restorationView()
                        }
                        OverlayContract.LogsState.Loading -> {

                        }

                        is OverlayContract.LogsState.Success -> {
                            resizeView()
                            svLog.updateLayoutParams {
                                width = Resources.getSystem().displayMetrics.widthPixels
                                height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                            }
                        }
                    }

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect {
                    when (it) {
                        is OverlayContract.SideEffect.FetchLogs -> {
                            val logContentView = createLogTextView(it.log.content)
                            logContainer.addView(logContentView)
                        }
                    }
                }
            }
        }
    }

    private fun restorationView() {
        ivClose.isVisible = false
        spLog.isVisible = false
        svLog.isVisible = false
        cbZoom.isVisible = false
        cbZoom.isChecked = true
        tvLog.text = "All"

        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.LEFT_OF)
        moveLayoutParams.addRule(RelativeLayout.RIGHT_OF, tvLog.id)
        ivMove.layoutParams = moveLayoutParams

        windowManager.updateViewLayout(rootView, rootViewParams)
    }

    private fun resizeView() {
        ivClose.isVisible = true
        spLog.isVisible = true
        svLog.isVisible = true
        cbZoom.isVisible = true
        cbZoom.isChecked = false


        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.RIGHT_OF)
        moveLayoutParams.addRule(RelativeLayout.LEFT_OF, ivClose.id)
        ivMove.layoutParams = moveLayoutParams
    }

    private fun createLogTextView(log: String) = TextView(this@OverlayTaskService).apply { text = log }

    internal fun setUnBindServiceCallback(callback: BindServiceCallback) { this.unBindCallback = callback }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(rootView)
    }
}