package com.example.pokebook.ui.activity

import android.animation.ObjectAnimator
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokebook.data.pokemonData.PokemonDataRepository
import com.example.pokebook.ui.AppViewModelProvider
import com.example.pokebook.ui.screen.Navigation.BottomNavigationView
import com.example.pokebook.ui.theme.PokeBookTheme
import com.example.pokebook.ui.viewModel.Main.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewModelProvider.Factoryを使ってViewModelを初期化
        viewModel = ViewModelProvider(this, AppViewModelProvider.Factory)[MainViewModel::class.java]

        installSplashScreen()

        val content: View = findViewById(android.R.id.content)

        viewModel.readJson()

        // スプラッシュ画面の表示時間延長を判定
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // 準備ができた場合は、trueで描画開始、準備中の場合は、falseで一時停止。
                    return if (viewModel.isReady.value == true) {
                        // HOMEタブ表示
                        setContent {
                            PokeBookTheme {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    BottomNavigationView()
                                }
                            }
                        }

                        // rootのOnPreDrawListenerを削除する。
                        content.viewTreeObserver.removeOnPreDrawListener(this).apply {
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}
