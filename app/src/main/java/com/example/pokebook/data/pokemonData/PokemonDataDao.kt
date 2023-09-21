package com.example.pokebook.data.pokemonData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update

@Dao
@TypeConverters(StringListTypeConverter::class)
interface PokemonDataDao {
    //挿入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pokemonList: List<PokemonData>)

    // 同じ主キーを持つエンティティを更新
    @Update
    suspend fun update(pokemon: PokemonData)

    // 同じ主キーを持つエンティティを削除
    @Delete
    suspend fun delete(pokemon: PokemonData)

    @Query("SELECT * from pokemonData ORDER BY japaneseName ASC")
    fun getAllItems(): List<PokemonData>

    // japaneseName完全一致の検索
    @Query("SELECT * FROM pokemonData WHERE LOWER(japaneseName) = LOWER(:keyword) OR LOWER(englishName) = LOWER(:keyword)")
    fun searchPokemonByKeyword(keyword: String): PokemonData

    // id完全一致の検索
    @Query("SELECT * FROM pokemonData WHERE id = :id")
    fun searchById(id: Int): PokemonData

    // 指定された範囲のデータの検索
    @Query("SELECT id,imageUrl,japaneseName,speciesNumber FROM pokemonData WHERE id BETWEEN :startId AND :endId")
    fun getAllItemsBetweenIds(startId: Int, endId: Int): List<PokemonData>

    //　指定したIDのimageUrlとspeciesNumberにデータを挿入
    @Query("UPDATE pokemonData SET imageUrl = :imageUrl,speciesNumber = :speciesNumber WHERE id = :id")
    suspend fun updatePokemonData(id: Int, imageUrl: String, speciesNumber: String?)

    // 指定したpokemonNumberの必要なカラムを更新
    @Query("UPDATE pokemonData SET englishName = :englishName, japaneseName = :japaneseName, description = :description,hp = :hp, attack = :attack, defense = :defense, speed = :speed, imageUrl = :imageUrl,speciesNumber = :speciesNumber,genus=:genus,type=:type,evolutionChainNumber=:evolutionChainNumber WHERE id = :pokemonNumber")
    suspend fun updatePokemonAllData(
        pokemonNumber: Int? = null,
        englishName: String? = null,
        japaneseName: String? = null,
        description: String? = null,
        hp: Int? = null,
        attack: Int? = null,
        defense: Int? = null,
        speed: Int? = null,
        imageUrl: String? = null,
        genus: String?,
        type: List<String>?,
        speciesNumber: String?,
        evolutionChainNumber: String?
    )
}
