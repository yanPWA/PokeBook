package com.example.pokebook.data.searchType

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokebook.model.PokemonListItem
import com.example.pokebook.model.PokemonTypeSearchResult
import com.example.pokebook.ui.viewModel.Home.PokemonListUiData

/**
 * 初回起動時に検索タイプ一覧を取得するためのエンティティ
 * データベーステーブル
 */
@Entity(tableName = "searchTypeList")
data class SearchTypeList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val typeName: String = "",
    val typeNumber: Int = 0,
    val pokemonNumber: Int = 0,
    val englishName: String = "",
    val japaneseName: String? = "",
    val imageUrl: String? = "",
    val speciesNumber: Int = 0
)

/**
 * PokemonTypeSearchResult -> List<SearchTypeList>
 */
fun PokemonTypeSearchResult.toSearchTypeList(): List<SearchTypeList> {
    val resultList = mutableListOf<SearchTypeList>()
    val typeName = this.typeName.firstOrNull { it.language.name == "ja" }?.name ?: ""
    val typeNumber = this.id

    this.pokemon.forEach { pokemonItem ->
        resultList.add(
            SearchTypeList(
                typeName = typeName,
                typeNumber = typeNumber,
                pokemonNumber = Uri.parse(pokemonItem.pokemonItem.url).lastPathSegment?.toInt()
                    ?: 0,
                englishName = pokemonItem.pokemonItem.name,
            )
        )
    }
    return resultList
}

/**
 * List<PokemonListUiData> -> List<SearchTypeList>
 */
fun List<PokemonListUiData>.toSearchTypeListByPokemonListUiData(): List<SearchTypeList> {
    return this.map { item ->
        SearchTypeList(
            japaneseName = item.displayName,
            pokemonNumber = item.pokemonNumber
        )
    }
}
