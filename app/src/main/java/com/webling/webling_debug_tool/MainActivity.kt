package com.webling.webling_debug_tool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.webling_debug_tool.R
import com.webling.debuglibrary.presentation.WeblingDebugTool

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

    }

    fun checkPermission() {
        // 마시멜로우 이상일 경우
        if (!Settings.canDrawOverlays(this)) {              // 체크
            val intent: Intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            ActivityCompat.startActivityForResult(this, intent, 9999, null)
        } else {
           val sdf =  WeblingDebugTool(this)
            sdf.bindService()
            sdf.onLogCollect("eddy test")

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
                sdf.onLogCollect("eddy test")
            }
        }

    }
}