package com.example.gamevault.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamevault.model.Profile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    suspend fun get(): Profile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(profile: Profile)
}