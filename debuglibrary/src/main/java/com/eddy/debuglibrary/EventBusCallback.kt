package com.eddy.debuglibrary

internal interface EventBusCallback {
    fun allow()
    fun deny()
    fun neverDeny()
}