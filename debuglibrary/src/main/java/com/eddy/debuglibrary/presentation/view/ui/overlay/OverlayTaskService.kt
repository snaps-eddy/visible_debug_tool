package com.eddy.debuglibrary.presentation.view.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eddy.debuglibrary.di.AppContainer
import com.eddy.debuglibrary.di.DiManager
import com.eddy.debuglibrary.presentation.viewmodel.OverlayContract
import com.eddy.debuglibrary.presentation.viewmodel.OverlayTaskViewModel
import com.eddy.debuglibrary.util.Constants
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("ClickableViewAccessibility")
internal class OverlayTaskService : LifecycleService(), OverlayTaskCallback {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }
    private val viewModel: OverlayTaskViewModel by lazy { OverlayTaskViewModel(appContainer.getLogcatUseCase, appContainer.clearLogUseCase, appContainer.deleteLogUseCase, appContainer.resourceProvider) }
    private val sharedPreferences: SharedPreferences by lazy {getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)}

    private val binder = OverlayDebugToolPopUpBinder()
    private val view: OverlayTaskView by lazy { OverlayTaskView(context = applicationContext, callback = this) }

    private lateinit var unBindCallback: () -> Unit

    override fun onCreate() {
        super.onCreate()
        view.onCreateView()
        initObservers()
        saveFilterKeywordList()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val searchKeyword = intent?.getStringExtra(SEARCH_KEYWORD) ?: ""

        view.onSearchKeyword(searchKeyword)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        view.onDestroyView()
        return super.onUnbind(intent)
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (val state = it.logsState) {
                        OverlayContract.LogsState.Idle -> {
                            onClickDelete()
                            view.init()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.distinctUntilChanged().collect {
                    when (it) {
                        is OverlayContract.SideEffect.FetchLogs -> view.addLogTextView(it.logs)
                    }
                }
            }
        }
    }

    private fun saveFilterKeywordList() {
        if(sharedPreferences.getString(Constants.SharedPreferences.EDDY_LOG_FILTER_KEYWORD, null) == null) {
            var arrayListPrefs = ArrayList<String>()
            var stringPrefs : String? = null

            arrayListPrefs.add(0, "normal")
            stringPrefs = GsonBuilder().create().toJson(
                arrayListPrefs,
                object : TypeToken<ArrayList<String>>() {}.type
            )
            sharedPreferences.edit().apply {
                putString(Constants.SharedPreferences.EDDY_LOG_FILTER_KEYWORD, stringPrefs)
                apply()
            }
        }
    }

    internal fun setUnBindServiceCallback(block: () -> Unit) {
        this.unBindCallback = block
    }

    private fun onClickClose() {
        viewModel.setEvent(OverlayContract.Event.OnCloseClick)
    }

    private fun onClickTagItem(tag: String) {
        viewModel.setEvent(OverlayContract.Event.OnClickKeyWordItem(tag))
    }

    private fun onLongClickCloseService() {
        unBindCallback.invoke()
    }

    private fun onClickDelete() {
        viewModel.setEvent(OverlayContract.Event.DeleteLog)
    }

    override fun onDestroy() {
        super.onDestroy()
        view.onDestroyView()
        stopSelf()
    }

    override val onClickClose: () -> Unit = ::onClickClose
    override val onClickTagItem: (tag: String) -> Unit = ::onClickTagItem
    override val onLongClickCloseService: () -> Unit = ::onLongClickCloseService
    override val onClickDelete: () -> Unit = ::onClickDelete

    companion object {
        const val SEARCH_KEYWORD = "Search Keyword"
    }
}