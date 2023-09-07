package com.example.pokebook.data.searchType

class OfflineSearchTypeListRepository(private val searchTypeListDao: SearchTypeListDao) :
    SearchTypeListRepository {
    override suspend fun insert(searchTypeList: List<SearchTypeList>) =
        searchTypeListDao.insert(searchTypeList)

    override suspend fun update(searchType: SearchTypeList) =
        searchTypeListDao.update(searchType)

    override suspend fun searchByTypeNumber(typeNumber: Int): List<SearchTypeList> =
        searchTypeListDao.searchByTypeNumber(typeNumber)

    override suspend fun searchPokemonNumberByTypeNumber(typeNumber: Int): List<Int> =
        searchTypeListDao.searchPokemonNumberByTypeNumber(typeNumber)

    override suspend fun updateJapaneseName(pokemonNumber: Int, japaneseName: String) =
        searchTypeListDao.updateJapaneseName(pokemonNumber, japaneseName)

    override suspend fun updateSpeciesNumberAndImageUrl(
        pokemonNumber: Int,
        imageUrl: String,
        speciesNumber: Int
    ) = searchTypeListDao.updateSpeciesNumberAndImageUrl(
        pokemonNumber,
        imageUrl,
        speciesNumber
    )
}
