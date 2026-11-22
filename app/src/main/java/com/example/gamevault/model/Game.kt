package com.example.gamevault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appId: Int? = null,
    val name: String,
    val imageUri: String? = null,
    val playtimeMinutes: Int = 0
)