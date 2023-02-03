package com.eddy.debuglibrary.data.log.entity

import androidx.room.TypeConverter
import com.google.gson.Gson

class LogConverters {
    @TypeConverter
    fun mapToJson(value: Level): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToMap(value: String): Level {
        return when {
            value.contains(Level.V.containWord) -> { Gson().fromJson(value, Level.V::class.java) }
            value.contains(Level.D.containWord) -> { Gson().fromJson(value, Level.D::class.java) }
            value.contains(Level.I.containWord) -> { Gson().fromJson(value, Level.I::class.java) }
            value.contains(Level.W.containWord) ->{ Gson().fromJson(value, Level.W::class.java) }
            value.contains(Level.E.containWord) -> { Gson().fromJson(value, Level.E::class.java) }
            value.contains(Level.F.containWord) -> { Gson().fromJson(value, Level.F::class.java) }
            value.contains(Level.S.containWord) -> { Gson().fromJson(value, Level.S::class.java) }
            else -> { Gson().fromJson(value, Level.S::class.java) }
        }
    }
}