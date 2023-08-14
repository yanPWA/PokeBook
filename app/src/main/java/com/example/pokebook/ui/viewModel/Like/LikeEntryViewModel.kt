package com.example.pokebook.ui.viewModel.Like

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.LikesRepository
import com.example.pokebook.ui.screen.createDummyList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LikeEntryViewModel(private val likesRepository: LikesRepository) : ViewModel() {
    /**
     * 現在のlikeの状態を保持する
     */
    private var _uiState: MutableStateFlow<LikeUiState> =
        MutableStateFlow(LikeUiState.InitialState)
    val uiState = _uiState.asStateFlow()
    val currentUiState = uiState

    private var likeListUiState by mutableStateOf(LikeListUiState())

    // 表示用リスト
    private val uiDataList = mutableListOf<PokemonListUiData>()


    init {
//        getAllList()
    }


    /**
     * 引数で指定された値で[LikeListUiState]を更新する。このメソッドは、入力値のバリデーションもトリガする
     */
    fun updateLikeListUiState(likeDetails: LikeDetails) {
        likeListUiState =
            LikeListUiState(likeDetails = likeDetails)
    }

    /**
     * Room データベースにアイテムを挿入
     */
    suspend fun saveLike() {
        likesRepository.insertItem(likeListUiState.likeDetails.toLike())
    }

    /**
     * Room データベースからアイテムを削除
     */
    suspend fun deleteLike() {
        likesRepository.deleteItem(likeListUiState.likeDetails.toLike())
    }

    /**
     * データベースから全てのアイテムを取得
     */
    private fun getAllList() = viewModelScope.launch {
        uiDataList.clear()
        _uiState.emit(LikeUiState.Loading)

//        likesRepository.getAllItemsStream().apply {
//            this.collect { value ->
//                uiDataList += value.toPokemonListUiDataList()
//            }
//        }
//        _uiState.emit(LikeUiState.Fetched(uiDataList = uiDataList))
//        Log.d("test","完了なはず：${uiState.value}")


        runCatching {
            likesRepository.getAllItemsStream().apply {
                this.collect { value ->
                    uiDataList += value.toPokemonListUiDataList()
                }
            }
        }
            .onSuccess {
                _uiState.emit(LikeUiState.Fetched(uiDataList = uiDataList))
                Log.d("test", "取得終了：${uiState.value}")

            }
            .onFailure {
                // 何もしない
            }
    }

    /**
     * 詳細画面へ遷移
     */
    fun onClickCard() {

    }

}