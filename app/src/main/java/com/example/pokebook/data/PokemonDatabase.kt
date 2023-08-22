package com.example.pokebook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pokebook.data.like.Like
import com.example.pokebook.data.like.LikeDao
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataDao

@Database(entities = [Like::class, PokemonData::class ], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun likeDao(): LikeDao
    abstract fun pokemonDataDao(): PokemonDataDao

    companion object {
        @Volatile
        private var Instance: PokemonDatabase? = null

        fun getDatabase(context: Context): PokemonDatabase {
            // インスタンスがnullの場合は新しいDBインスタンスを作成して返す
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PokemonDatabase::class.java, "like_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
