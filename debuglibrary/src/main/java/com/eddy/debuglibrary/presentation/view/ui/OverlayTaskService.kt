package com.eddy.debuglibrary.presentation.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eddy.debuglibrary.di.AppContainer
import com.eddy.debuglibrary.di.DiManager
import com.eddy.debuglibrary.presentation.view.OverlayTaskCallback
import com.eddy.debuglibrary.presentation.viewmodel.OverlayContract
import com.eddy.debuglibrary.presentation.viewmodel.OverlayTaskViewModel
import kotlinx.coroutines.*

@SuppressLint("ClickableViewAccessibility")
internal class OverlayTaskService : LifecycleService(), OverlayTaskCallback {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }
    private val viewModel: OverlayTaskViewModel by lazy { OverlayTaskViewModel(appContainer.getLogcatUseCase) }

    private val binder = OverlayDebugToolPopUpBinder()
    private val view: OverlayTaskView by lazy { OverlayTaskView(context = applicationContext, callback = this) }

    private lateinit var unBindCallback: () -> Unit

    override fun onCreate() {
        super.onCreate()
        view.onCreateView()
        initObservers()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (val state = it.logsState) {
                        OverlayContract.LogsState.Idle -> {
                            view.init()
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
                            view.addLogTextView(it.log)
                        }
                        is OverlayContract.SideEffect.SearchLog -> {
                            view.searchLog(it.word)
                        }
                    }
                }
            }
        }
    }

    internal fun setUnBindServiceCallback(block: () -> Unit) {
        this.unBindCallback = block
    }

    internal fun setTagList(tags: List<String>?) {
        view.setTagSpinnerAdapter(tags)
    }

    private fun onClickClose() {
        viewModel.setEvent(OverlayContract.Event.OnCloseClick)
    }

    private fun onClickTagItem(tag: String) {
        viewModel.setEvent(OverlayContract.Event.OnClickTagItem(tag))
    }

    private fun onLongClickCloseService() {
        unBindCallback.invoke()
    }

    override val onClickClose: () -> Unit = ::onClickClose
    override val onClickTagItem: (tag: String) -> Unit = ::onClickTagItem
    override val onLongClickCloseService: () -> Unit = ::onLongClickCloseService
}