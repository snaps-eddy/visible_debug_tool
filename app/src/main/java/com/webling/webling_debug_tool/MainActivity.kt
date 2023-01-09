package com.webling.webling_debug_tool

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.webling_debug_tool.R
import com.webling.debuglibrary.presentation.WeblingDebugTool
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()


        thread(start = true) {
            var i = 0

            while (i < 10) {
                Log.d("eddy gege", "이거 돼? $i" )
                Log.d("eddy white", "이거 돼? $i" )
                i++
                Thread.sleep(1500)
            }
        }

    }

    fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent: Intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            ActivityCompat.startActivityForResult(this, intent, 9999, null)
        } else {
           val sdf =  WeblingDebugTool(this)
            sdf.bindService()
            sdf.onLogCollect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9999) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {
                val sdf =  WeblingDebugTool(this)
                sdf.bindService()
                sdf.onLogCollect()
                sdf.onUnbindService()
            }
        }
    }

}