package com.example.weblinglibrary.log

sealed interface LogState {

    object D: LogState
    object I: LogState
    object E: LogState
    object W: LogState
    object V: LogState

}