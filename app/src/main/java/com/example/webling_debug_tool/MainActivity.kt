package com.example.webling_debug_tool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weblinglibrary.ShowLibrary

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val d = ShowLibrary.sToast(this," 이거 되는거 맞냐!?...")
    }
}