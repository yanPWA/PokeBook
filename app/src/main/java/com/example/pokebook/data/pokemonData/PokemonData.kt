package com.example.pokebook.data.pokemonData

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.pokebook.json.PokemonDataByJson
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies

/**
 * 初回起動時にjsonファイルから取得するためのエンティティ
 * データベーステーブル
 */
@Entity(tableName = "pokemonData")
data class PokemonData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val englishName: String? = "",
    val japaneseName: String = "",
//    val type:List<String>
    val hp: Int? = 0,
    val attack: Int? = 0,
    val defense: Int? = 0,
    val speed: Int? = 0,
    val imageUrl: String? = "",
    val speciesNumber: String? = ""
)

/**
 * PokemonPersonalData -> PokemonData
 */
fun PokemonPersonalData.pokemonPersonalDataToPokemonData(): PokemonData = PokemonData(
    imageUrl = this.sprites.other.officialArtwork.imgUrl ?: "",
    speciesNumber = if (!this.species.url.isNullOrEmpty()) Uri.parse(this.species.url).lastPathSegment else ""
)

/**
 * PokemonSpecies -> PokemonData
 */
fun PokemonSpecies.convertSpeciesToPokemonData(): PokemonData = PokemonData(
)