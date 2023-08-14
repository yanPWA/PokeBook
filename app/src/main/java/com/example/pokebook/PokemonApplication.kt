package com.example.pokebook

import android.app.Application
import com.example.pokebook.data.AppContainer
import com.example.pokebook.data.AppDataContainer

class PokemonApplication : Application() {

    /**
     * 他のクラスが依存関係を取得するために使用するAppContainerインスタンス
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}