package com.eddy.webling_debug_tool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.eddy.debuglibrary.createDebugTool
import com.example.webling_debug_tool.R
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val tags: List<String> by lazy { listOf("eddy white", "sdf", "몰라~") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivTest = findViewById<ImageView>(R.id.iv_test)

        val ss  = "{\"productCode\":\"00800600220102\",\"templateCode\":\"045021042633\",\"paperCode\":\"160001\",\"sizeCode\":\"411016\",\"frameCode\":\"414001\",\"frameType\":\"415002\",\"quantity\":\"1\",\"gtmV4\":{\"add_to_cart\":{\"item_name\":\"포토북\",\"item_id\":\"\",\"price\":21900,\"item_brand\":\"\",\"item_category\":\"포토북\",\"item_category2\":\"스냅북\",\"item_category3\":\"\",\"item_variant\":\"8 X 8/하드커버/무광/무광지\",\"quantity\":\"1\"}},\"projectCount\":1}"
        thread(start = true) {
            var i = 0

            while (i < 10) {
                Log.d("test","eddy test \neddy test \neddy test\n eddy testn\neddy test\neddy test\neddy test\neddy test\n eddy test")
                i++
                Thread.sleep(1500)
            }
        }

        ivTest.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }

        val dd = createDebugTool(context = applicationContext) {
            setAutoPermissionCheck(true)
        }
        dd.bindService()

    }

}