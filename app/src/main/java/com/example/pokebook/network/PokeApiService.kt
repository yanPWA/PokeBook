package com.example.pokebook.network

import com.example.pokebook.model.Pokemon
import com.example.pokebook.model.PokemonPersonalData
import com.example.pokebook.network.RetrofitInstance.Companion.getRetrofitInstance
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://pokeapi.co/api/v2/"

class RetrofitInstance {
    companion object {
        fun getRetrofitInstance(): Retrofit {
            val httpLogging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClientBuilder = OkHttpClient.Builder().addInterceptor(httpLogging)

            return Retrofit.Builder()
                .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory(MediaType.get("application/json")))
                .baseUrl(BASE_URL)
                .client(httpClientBuilder.build())
                .build()
        }
    }
}

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(): Pokemon

    @GET("pokemon/{path}")
    suspend fun getPokemonPersonalData(@Path("path") number: String): PokemonPersonalData
}

object PokeApi {
    val retrofitService: PokeApiService by lazy {
        getRetrofitInstance().create(PokeApiService::class.java)
    }
}