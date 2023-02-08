package com.eddy.debuglibrary.presentation.view.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleService
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.eddy.debuglibrary.presentation.view.model.LogUiModel
import com.eddy.debuglibrary.presentation.view.ui.overlay.epoxy.LogController
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
//    private val svLog: NestedScrollView by lazy { rootView.findViewById(R.id.sv_logs) }
//    private val logContainer: LinearLayout by lazy { rootView.findViewById(R.id.log_view_container) }
    private val ivMove: ImageView by lazy { rootView.findViewById(R.id.iv_move) }
    private val btnMenu: ImageButton by lazy { rootView.findViewById(R.id.btn_menu) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }
    private val ivClose: ImageView by lazy { rootView.findViewById(R.id.iv_close) }
    private val cbZoom: CheckBox by lazy { rootView.findViewById(R.id.cb_zoom) }
    private val spLog: Spinner by lazy { rootView.findViewById(R.id.sp_log) }
    private val ivTrashLog: ImageView by lazy { rootView.findViewById(R.id.iv_trash_log) }
    private val logController: LogController by lazy { LogController() }
    private val rvLog: EpoxyRecyclerView by lazy { rootView.findViewById(R.id.rv_logs) }

    private val logSelectorListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tvLog.text = logEvents[position]
            callback.onClickTagItem.invoke(logEvents[position])
            rvLog.removeAllViewsInLayout()

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private var isScrollBottom = false

    fun addLogTextView(log: List<LogUiModel>) {
        logController.setData(log)
        if (isScrollBottom) rvLog.smoothScrollToPosition(log.size - 1)
    }

    fun searchLog(log: String) {

    }

    fun init() {
        setRv()
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

    fun onDestroyView() {
        windowManager.removeView(rootView)
    }

    private fun setRv() {
        rvLog.apply {
            setController(logController)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListener() {
        ivTrashLog.setOnClickListener {
            callback.onClickDelete.invoke()
            logController.setData(listOf())
        }

        btnMenu.setOnClickListener {

        }

        ivMove.setOnTouchListener(viewMoveListener)
        ivClose.setOnClickListener {
            applyContractView()
            callback.onClickClose.invoke()
        }

        tvLog.setOnClickListener { if (isExpandView.not()) applyExpandView() }

        rootView.setOnLongClickListener {
            callback.onLongClickCloseService.invoke()
            true
        }

        cbZoom.setOnClickListener {
            if (cbZoom.isChecked) {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenFullRatio).toInt())
                }
            } else {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                }
            }
        }
        rvLog.setOnScrollChangeListener { _, _, _, _, _ ->
            isScrollBottom = !rvLog.canScrollVertically(1)
        }
        spLog.onItemSelectedListener = logSelectorListener
    }

    private fun applyExpandView() {
        rvLog.updateLayoutParams {
            width = Resources.getSystem().displayMetrics.widthPixels
            height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
        }
        isExpandView = true
        ivClose.isVisible = true
        spLog.isVisible = true
        rvLog.isVisible = true
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
        rvLog.isVisible = false
        cbZoom.isVisible = false
        cbZoom.isChecked = true
        ivTrashLog.isVisible = false
        tvLog.text = resources.getText(R.string.log)
        rvLog.removeAllViewsInLayout()

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