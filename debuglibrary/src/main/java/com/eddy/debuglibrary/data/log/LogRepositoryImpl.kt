package com.eddy.debuglibrary.data.log

import com.eddy.debuglibrary.data.log.entity.Level
import com.eddy.debuglibrary.data.log.entity.Log
import com.eddy.debuglibrary.data.log.local.LogLocalDataSource
import com.eddy.debuglibrary.data.log.remote.LogRemoteDataSource
import com.eddy.debuglibrary.domain.log.LogRepository
import com.eddy.debuglibrary.domain.log.model.LogLevel
import com.eddy.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class LogRepositoryImpl(
    private val remote: LogRemoteDataSource,
    private val local: LogLocalDataSource
): LogRepository {

    private val exceptChar =
        listOf(
        "WifiMulticast","WifiHW","MtpService","PushClient","ViewRootImpl","InputTransport","InputMethodManager","mali|TextView", "activityThread", "TrafficStats",
        "tagSocket", "ConversionRepoter","nativeloader", "DecorView", "BLASTBufferQueue", "OpenGLRenderer", "chromium" ,"CCodec" ,"Codec2Client","ReflectedParamUpdater",
        "ColorUtils" ,"MediaCodec" ,"SurfaceUtils", "CCodecBufferChannel","BufferQueueProducer","cr_MediaCodecBridge","BufferPoolAccessor"
    )

    override fun getLogcatData(filterWord: String): Flow<List<LogModel>> {
        return remote.logDataCollect()
            .filterNot { exceptChar.any { char -> it.contains(char) } }
            .filter { it.contains(filterWord) }
            .map {
                when {
                    it.contains(Level.V.containWord) -> { Log(content = it, level = Level.V) }
                    it.contains(Level.D.containWord) -> { Log(content = it, level = Level.D) }
                    it.contains(Level.I.containWord) -> { Log(content = it, level = Level.I) }
                    it.contains(Level.W.containWord) -> { Log(content = it, level = Level.W) }
                    it.contains(Level.E.containWord) -> { Log(content = it, level = Level.E) }
                    it.contains(Level.F.containWord) -> { Log(content = it, level = Level.F) }
                    it.contains(Level.S.containWord) -> { Log(content = it, level = Level.S) }
                    else ->  { Log(content = it, level = Level.S) }
                }
            }.map { local.insertLog(it) }
            .flatMapConcat { local.getAllLog() }
            .flowOn(Dispatchers.IO)
            .map {
                it.map {
                    when(it.level) {
                        Level.V -> { LogModel(content = it.content, logLevel = LogLevel.V)  }
                        Level.D -> { LogModel(content = it.content, logLevel = LogLevel.D)  }
                        Level.I -> { LogModel(content = it.content, logLevel = LogLevel.I)  }
                        Level.W -> { LogModel(content = it.content, logLevel = LogLevel.W)  }
                        Level.E -> { LogModel(content = it.content, logLevel = LogLevel.E)  }
                        Level.F -> { LogModel(content = it.content, logLevel = LogLevel.F)  }
                        Level.S -> { LogModel(content = it.content, logLevel = LogLevel.S)  }
                        else -> { LogModel(content = it.content, logLevel = LogLevel.S)  }
                    }
                }
            }
    }


    override fun clearLog() {
        remote.clearLog()
    }

    override fun deleteLogData() {
        local.deleteAllLog()
    }
}