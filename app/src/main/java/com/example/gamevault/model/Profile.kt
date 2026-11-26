package com.example.gamevault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val description: String = "",
    val photoPath: String? = null
)