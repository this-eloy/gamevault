package com.example.gamevault.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gamevault.model.Game

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY name")
    suspend fun getAll(): List<Game>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: Int): Game?

    @Query("SELECT * FROM games WHERE appId = :appId LIMIT 1")
    suspend fun getByAppId(appId: Int): Game?

    @Insert
    suspend fun insert(game: Game): Long

    @Update
    suspend fun update(game: Game)

    @Delete
    suspend fun delete(game: Game)
}