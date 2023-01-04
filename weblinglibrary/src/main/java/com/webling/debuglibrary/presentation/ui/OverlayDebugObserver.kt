package com.webling.debuglibrary.presentation.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.webling.debuglibrary.domain.log.LogcatCollector

internal class OverlayDebugObserver: DefaultLifecycleObserver {

    private val logcatCollector by lazy { LogcatCollector() }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
//        val logData = logcatCollector.collect("eddy test")
//
//        CoroutineScope(Dispatchers.IO).launch {
//            logData.collect{
//                Log.d("eddy test","eddy testlknlkn $it")
//            }
//        }

    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

}