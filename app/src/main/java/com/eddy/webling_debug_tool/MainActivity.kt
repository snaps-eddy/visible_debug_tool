package com.eddy.webling_debug_tool

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eddy.debuglibrary.createDebugTool
import com.example.webling_debug_tool.R
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val tags: List<String> by lazy { listOf("eddy white", "sdf", "몰라~") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thread(start = true) {
            var i = 0

            while (i < 10) {
                Log.d("eddy gege", "이거 돼? $i")
                Log.d("eddy white", "이거 돼? $i")
                i++
                Thread.sleep(1500)
            }
        }

        createDebugTool(context = this) {
            setAutoPermissionCheck(true)
            setSearchKeyWordList(tags)
        }.bindService()


    }
}