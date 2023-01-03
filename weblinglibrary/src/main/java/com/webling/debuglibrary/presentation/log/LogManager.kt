package com.webling.debuglibrary.presentation.log

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import androidx.annotation.RequiresPermission
import com.webling.debuglibrary.presentation.Manager
import com.webling.debuglibrary.presentation.ui.OverlayDebugToolPopUp
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class LogManager(
    private val context: Context,
) : Manager {

    @RequiresPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    override fun startService() {
        val intent = Intent(context, OverlayDebugToolPopUp::class.java)
        context.startService(intent)
    }

    private fun getDebugLog() {
        val pid = android.os.Process.myPid()
        val commandArray = mutableListOf("logcat", "-v", "time", "--pid=$pid")
        val logcat = Runtime.getRuntime().exec(commandArray.toTypedArray())
        val br = BufferedReader(InputStreamReader(logcat?.inputStream), 4 * 1024)
        var line: String
        val separator = System.getProperty("line.separator")
        while (br.readLine().also { line = it } != null) {
            emitter.onNext(line + separator)
        }
    }

//    private fun eddyTest() {
//        val pid = Process.myPid()
//        val commandArray = ArrayList<String>()
//        commandArray.add("logcat")
//        commandArray.add("-v")
//        commandArray.add("time")
//        commandArray.add("--pid=$pid")
//        try {
//            val logcat = Runtime.getRuntime().exec("logcat -v time --pid=$pid")
//            val br = BufferedReader(InputStreamReader(logcat.inputStream), 1024)
//            val separator = System.getProperty("line.separator")
//            val flowable: Flowable<String> = Flowable.< String > create < kotlin . String ? > { emitter ->
//                var line: String? = null
//                while (br.readLine().also { line = it } != null) {
//                    emitter.onNext(line + separator)
//                }
//            }, BackpressureStrategy.BUFFER)
//            .throttleWithTimeout(500L, TimeUnit.MILLISECONDS)
//            flowable.subscribe { str -> Log.d("test", "eddy test 123l  $str") }
//        } catch (e: Exception) {
//            Dlog.e("test", e)
//        }
////        while (br.readLine().also { line = it } != null) {
////            emitter.onNext(line + separator)
////        }
//    }

    companion object {
        const val RESOURCE_ID = "RESOURCE_ID"
    }
}