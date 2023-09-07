package com.example.pokebook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.pokebook.data.like.Like
import com.example.pokebook.data.like.LikeDao
import com.example.pokebook.data.pokemonData.PokemonData
import com.example.pokebook.data.pokemonData.PokemonDataDao
import com.example.pokebook.data.pokemonData.StringListTypeConverter
import com.example.pokebook.data.searchType.SearchTypeList
import com.example.pokebook.data.searchType.SearchTypeListDao

@Database(
    entities = [Like::class, PokemonData::class, SearchTypeList::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListTypeConverter::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun likeDao(): LikeDao
    abstract fun pokemonDataDao(): PokemonDataDao
    abstract fun searchTypeListDao(): SearchTypeListDao

    companion object {
        @Volatile
        private var Instance: PokemonDatabase? = null

        fun getDatabase(context: Context): PokemonDatabase {
            // インスタンスがnullの場合は新しいDBインスタンスを作成して返す
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PokemonDatabase::class.java, "pokemon_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
