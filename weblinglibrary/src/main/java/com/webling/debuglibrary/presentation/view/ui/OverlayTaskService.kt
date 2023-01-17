package com.webling.debuglibrary.presentation.view.ui

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
import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.webling.debuglibrary.presentation.BindServiceCallback
import com.webling.debuglibrary.presentation.view.OverlayTaskCallback
import com.webling.debuglibrary.presentation.viewmodel.OverlayContract
import com.webling.debuglibrary.presentation.viewmodel.OverlayTaskViewModel
import kotlinx.coroutines.*

@SuppressLint("ClickableViewAccessibility")
class OverlayTaskService : LifecycleService(), OverlayTaskCallback {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val viewModel: OverlayTaskViewModel = OverlayTaskViewModel(GetLogcatUseCase())
    private val binder = OverlayDebugToolPopUpBinder()
    private lateinit var unBindCallback: BindServiceCallback
    private val view: OverlayTaskView by lazy { OverlayTaskView(context = applicationContext, callback = this) }

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
                        OverlayContract.LogsState.Loading -> {

                        }

                        OverlayContract.LogsState.Success -> {

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
                            view.addLogView(it.log.content)
                        }
                    }
                }
            }
        }
    }

    internal fun setUnBindServiceCallback(callback: BindServiceCallback) {
        this.unBindCallback = callback
    }

    private fun onClickClose() {
        viewModel.setEvent(OverlayContract.Event.OnCloseClick)
    }

    private fun onClickTagItem(tag: String) {
        viewModel.setEvent(OverlayContract.Event.OnClickTagItem(tag))
    }

    private fun onLongClickCloseService() {
        unBindCallback.onUnbindService()
    }

    override val onClickClose: () -> Unit = ::onClickClose
    override val onClickTagItem: (tag: String) -> Unit = ::onClickTagItem
    override val onLongClickCloseService: () -> Unit = ::onLongClickCloseService
}