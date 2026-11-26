package com.example.gamevault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gamevault.model.Game
import com.example.gamevault.model.Profile

@Database(entities = [Game::class, Profile::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE games ADD COLUMN source TEXT NOT NULL DEFAULT 'steam'")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS profile (" +
                            "id INTEGER NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "photoPath TEXT, " +
                            "PRIMARY KEY(id))"
                )
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "games.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
    }
}