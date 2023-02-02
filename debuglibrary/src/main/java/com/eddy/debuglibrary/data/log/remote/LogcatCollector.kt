package com.eddy.debuglibrary.data.log.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal class LogcatCollector {

    fun clearLog() {
        ProcessBuilder()
            .command("logcat", "-c")
            .redirectErrorStream(true)
            .start()
    }

    fun collect(): Flow<String> {
        val pid = android.os.Process.myPid()
        val commandArray = mutableListOf("logcat", "-v", "time", "--pid=$pid", "-s","*:D")

        val process = Runtime.getRuntime().exec(commandArray.toTypedArray())
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        return internalCollect(bufferedReader)
    }

    private fun internalCollect(
        bufferedReader: BufferedReader
    ): Flow<String> = flow {
        try {
            var line: String?
            val separator = System.getProperty("line.separator")
            while (bufferedReader.readLine().also { line = it } != null) {
                emit(line + separator)
            }
        } catch (e: IOException) {
            e.message?.let { Log.e(TAG, it) }
        }
    }
        .flowOn(Dispatchers.IO)


    companion object {
        const val TAG = "LogCatCollector"
    }

}