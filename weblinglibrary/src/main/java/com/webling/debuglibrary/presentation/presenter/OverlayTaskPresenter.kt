package com.webling.debuglibrary.presentation.presenter

import com.webling.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.webling.debuglibrary.presentation.model.log.LogUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

class OverlayTaskPresenter(
    private val getLogcatUseCase: GetLogcatUseCase,
    private val overlayView: OverlayTaskContract.OverlayView
) : OverlayTaskContract.Presenter, CoroutineScope {

    private lateinit var job: Job

    override fun getLogData(searchTag: String?) {
        deInit()
        job = launch {
            val logUiModels = CopyOnWriteArrayList<LogUiModel>()
            getLogcatUseCase.invoke(if (!searchTag.isNullOrEmpty()) searchTag else "").collect {
                if (it.isNotEmpty()) {
                    logUiModels.add(LogUiModel(it))
                    overlayView.setLogData(logUiModels)
                }
            }
        }
    }

    override fun deInit() {
        if (this::job.isInitialized) {
            job.cancel()
        }
//        if (logUiModels.size > 0) logUiModels.clear()
    }

    override val coroutineContext: CoroutineContext
        get() = newSingleThreadContext("weblingPresenter")

}