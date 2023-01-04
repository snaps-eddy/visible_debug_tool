package com.webling.debuglibrary.domain.log

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LogcatCollector {
    fun collect(searchTag: String): Flow<String> {
        val pid = android.os.Process.myPid()
        val commandArray = mutableListOf("logcat", "-v", "time", "--pid=$pid", "-s","*:D")

        val process = Runtime.getRuntime().exec(commandArray.toTypedArray())
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        return internalCollect(bufferedReader, searchTag)

    }

    private fun internalCollect(
        bufferedReader: BufferedReader,
        searchTag: String
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