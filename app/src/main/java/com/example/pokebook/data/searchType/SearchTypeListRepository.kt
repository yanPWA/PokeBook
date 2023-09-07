package com.example.pokebook.data.searchType

/**
 * 初回のポケモンタイプ別一覧取得で作成されたDBに対して操作を行うRepository
 */
interface SearchTypeListRepository {
    /**
     * 指定されたデータ・ソースからすべての項目を取得
     */
    suspend fun insert(searchTypeList: List<SearchTypeList>)

    /**
     * データソースの項目を更新
     */
    suspend fun update(searchType: SearchTypeList)

    /**
     * typeNumber完全一致の検索(全カラム取得)
     */
    suspend fun searchByTypeNumber(typeNumber: Int): List<SearchTypeList>

    /**
     * typeNumber完全一致の検索(pokemonNumberカラム取得)
     */
    suspend fun searchPokemonNumberByTypeNumber(typeNumber: Int): List<Int>

    /**
     * 該当するpokemonNumberのjapaneseNameとimageUrlを保存する
     */
    suspend fun updateSpeciesNumberAndImageUrl(
        pokemonNumber: Int,
        imageUrl: String,
        speciesNumber:Int
    )

    /**
     * 該当するpokemonNumberのjapaneseNameを保存する
     */
    suspend fun updateJapaneseName(
        pokemonNumber: Int,
        japaneseName: String
        )
}
