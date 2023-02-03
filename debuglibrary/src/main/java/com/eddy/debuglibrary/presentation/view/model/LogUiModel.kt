package com.eddy.debuglibrary.presentation.view.model

import com.eddy.debuglibrary.domain.log.model.LogLevel
import com.example.debuglibrary.R

data class LogUiModel(
    val content: String,
    val contentLevel: LogLevel
) {
    val contentColor: Int = when (contentLevel) {
        LogLevel.V -> {
            R.color.log_level_v_color
        }
        LogLevel.D -> {
            R.color.log_level_d_color
        }
        LogLevel.I -> {
            R.color.log_level_i_color
        }
        LogLevel.W -> {
            R.color.log_level_w_color
        }
        LogLevel.E -> {
            R.color.log_level_e_color
        }
        LogLevel.F -> {
            R.color.log_level_e_color
        }
        LogLevel.S -> {
            R.color.log_level_i_color
        }
    }
}