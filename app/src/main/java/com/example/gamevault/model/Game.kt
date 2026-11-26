package com.example.gamevault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appId: Int? = null,
    val name: String,
    val imageUri: String? = null,
    val playtimeMinutes: Int = 0,

    val source: String = SOURCE_MANUAL
) {
    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_STEAM = "steam"
    }
}