package com.example.pokebook.network

import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.model.PokemonSpecies
import com.example.pokebook.model.PokemonTypeSearchResult
import com.example.pokebook.network.RetrofitInstance.Companion.getRetrofitInstance
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://pokeapi.co/api/v2/"

class RetrofitInstance {
    companion object {
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType()))
                .baseUrl(BASE_URL)
                .build()
        }
    }
}

interface PokeApiService {
    /**
     * ポケモン一覧取得（クエリなし）
     */
    @GET("pokemon")
    suspend fun getPokemonList(): Pokemon

    /**
     * ポケモン一覧取得（クエリあり）
     */
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: String,
        @Query("limit") limit: String
    ): Pokemon

    /**
     * ポケモン個体情報取得
     */
    @GET("pokemon/{path}")
    suspend fun getPokemonPersonalData(@Path("path") number: Int): PokemonPersonalData

    /**
     * ポケモン個体説明取得
     */
    @GET("pokemon-species/{path}")
    suspend fun getPokemonSpecies(@Path("path") number: Int): PokemonSpecies

    /**
     * ポケモンタイプ別検索
     */
    @GET("type/{path}")
    suspend fun getPokemonByType(@Path("path") typeNumber: String): PokemonTypeSearchResult
}

object PokeApi {
    val retrofitService: PokeApiService by lazy {
        getRetrofitInstance().create(PokeApiService::class.java)
    }
}
