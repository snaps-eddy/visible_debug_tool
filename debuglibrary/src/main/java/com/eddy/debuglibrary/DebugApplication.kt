package com.eddy.debuglibrary

import android.app.Application
import com.eddy.debuglibrary.di.AppContainer

class DebugApplication: Application() {

    val appContainer = AppContainer()
}