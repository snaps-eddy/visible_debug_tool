package com.eddy.debuglibrary.data.log.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
data class Log(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "level")
    val level: Level,
)

@Parcelize
sealed class Level(val containWord: String) : Parcelable {

    object V : Level(this.V)
    object D : Level(this.D)
    object I : Level(this.I)
    object W : Level(this.W)
    object E : Level(this.E)
    object F : Level(this.F)
    object S : Level(this.S)

    companion object {
        private const val V: String = "V/"
        private const val D: String = "D/"
        private const val I: String = "I/"
        private const val W: String = "W/"
        private const val E: String = "E/"
        private const val F: String = "F/"
        private const val S: String = "S/"
    }
}