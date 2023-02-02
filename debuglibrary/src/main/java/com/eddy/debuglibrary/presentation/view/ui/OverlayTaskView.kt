package com.eddy.debuglibrary.presentation.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleService
import com.eddy.debuglibrary.domain.log.model.LogLevel
import com.eddy.debuglibrary.domain.log.model.LogModel
import com.eddy.debuglibrary.presentation.view.OverlayTaskCallback
import com.example.debuglibrary.R

internal class OverlayTaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val callback: OverlayTaskCallback
) : FrameLayout(context, attrs, defStyleAttr) {

    private val rootView: RelativeLayout by lazy { inflate.inflate(R.layout.view_in_overlay_popup, null) as RelativeLayout }
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
    private var isExpandView = false

    private val screenRatio = 3
    private val screenFullRatio = 1.5

    private lateinit var logEvents: List<String>

    private val inflate: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }
    private val svLog: NestedScrollView by lazy { rootView.findViewById(R.id.sv_logs) }
    private val logContainer: LinearLayout by lazy { rootView.findViewById(R.id.log_view_container) }
    private val ivMove: ImageView by lazy { rootView.findViewById(R.id.iv_move) }
    private val btnMenu: ImageButton by lazy { rootView.findViewById(R.id.btn_menu) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }
    private val ivClose: ImageView by lazy { rootView.findViewById(R.id.iv_close) }
    private val cbZoom: CheckBox by lazy { rootView.findViewById(R.id.cb_zoom) }
    private val spLog: Spinner by lazy { rootView.findViewById(R.id.sp_log) }
    private val ivTrashLog: ImageView by lazy { rootView.findViewById(R.id.iv_trash_log) }

    private val logSelectorListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tvLog.text = logEvents[position]
            callback.onClickTagItem.invoke(logEvents[position])
            logContainer.removeAllViews()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private var isScrollBottom = false

    fun addLogTextView(log: LogModel) {
        val logContentView = createLogTextView(log)
        logContainer.addView(logContentView)
    }

    fun searchLog(log: String) {

    }

    fun init() {
        setClickListener()
    }

    fun setTagSpinnerAdapter(tags: List<String>) {
        logEvents = tags

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, logEvents)
        spLog.adapter = adapter
    }

    fun onCreateView() {
        windowManager.addView(rootView, rootViewParams)
    }

    private fun createLogTextView(log: LogModel): TextView {
        return when (log.logLevel) {
            LogLevel.V -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_v_color))
                }
            }
            LogLevel.D -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_d_color))
                }
            }
            LogLevel.I -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_i_color))
                }
            }
            LogLevel.W -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_w_color))
                }
            }
            LogLevel.E -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_e_color))
                }
            }
            LogLevel.F -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_e_color))
                }
            }
            LogLevel.S -> {
                TextView(context).apply {
                    text = log.content
                    setTextColor(ContextCompat.getColor(context, R.color.log_level_i_color))
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListener() {
        ivTrashLog.setOnClickListener {
            callback.onClickClear.invoke()
            logContainer.removeAllViews()
        }

        btnMenu.setOnClickListener {

        }

        ivMove.setOnTouchListener(viewMoveListener)
        ivClose.setOnClickListener {
            applyContractView()
            callback.onClickClose.invoke()
        }

        tvLog.setOnClickListener {
            if(isExpandView.not()) applyExpandView()
        }

        rootView.setOnLongClickListener {
            callback.onLongClickCloseService.invoke()
            windowManager.removeView(rootView)
            true
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
        svLog.viewTreeObserver.addOnGlobalLayoutListener {
            svLog.post {
                if (isScrollBottom) {
                    svLog.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }
        svLog.setOnScrollChangeListener { _, _, _, _, _ ->
            isScrollBottom = !svLog.canScrollVertically(1)
        }
        spLog.onItemSelectedListener = logSelectorListener
    }

    private fun applyExpandView() {
        svLog.updateLayoutParams {
            width = Resources.getSystem().displayMetrics.widthPixels
            height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
        }
        isExpandView = true
        ivClose.isVisible = true
        spLog.isVisible = true
        svLog.isVisible = true
        cbZoom.isVisible = true
        cbZoom.isChecked = false
        ivTrashLog.isVisible = true
        tvLog.text = logEvents[0]
        spLog.setSelection(0)

        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.RIGHT_OF)
        moveLayoutParams.addRule(RelativeLayout.LEFT_OF, ivClose.id)
        ivMove.layoutParams = moveLayoutParams

        callback.onClickTagItem.invoke("normal")
    }

    private fun applyContractView() {
        isExpandView = false
        ivClose.isVisible = false
        spLog.isVisible = false
        svLog.isVisible = false
        cbZoom.isVisible = false
        cbZoom.isChecked = true
        ivTrashLog.isVisible = false
        tvLog.text = "Log"

        val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
        moveLayoutParams.removeRule(RelativeLayout.LEFT_OF)
        moveLayoutParams.addRule(RelativeLayout.RIGHT_OF, tvLog.id)
        ivMove.layoutParams = moveLayoutParams

        windowManager.updateViewLayout(rootView, rootViewParams)
    }

    @SuppressLint("ClickableViewAccessibility")
    private val viewMoveListener = OnTouchListener { _, event ->
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

}