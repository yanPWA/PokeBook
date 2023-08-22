package com.example.pokebook.ui.viewModel.Like

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokebook.data.like.LikesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikeEntryViewModel(private val likesRepository: LikesRepository) : ViewModel() {
    /**
     * 現在のlikeの状態を保持する
     */
    private var _uiState: MutableStateFlow<LikeUiState> =
        MutableStateFlow(LikeUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private var _conditionState: MutableStateFlow<LikeScreenConditionState> =
        MutableStateFlow(LikeScreenConditionState())
    val conditionState = _conditionState.asStateFlow()

    private val _uiEvent: MutableStateFlow<List<LikeUiEvent>> = MutableStateFlow(listOf())
    val uiEvent: Flow<LikeUiEvent?>
        get() = _uiEvent.map { it.firstOrNull() }

    // イベントの通知
    private fun send(event: LikeUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value + event)
    }

    // イベントの消費
    fun processed(event: LikeUiEvent) = viewModelScope.launch {
        _uiEvent.emit(_uiEvent.value.filterNot { it == event })
    }

    // 表示用リスト
    private val uiDataList = mutableListOf<LikeDetails>()
    
    /**
     * Room データベースにアイテムを挿入
     */
    suspend fun saveLike(item: LikeDetails) {
        likesRepository.insertItem(
            item.toLike()
        )
    }

    /**
     * Room データベースからアイテムを削除
     */
    suspend fun deleteLike(item: LikeDetails) {
        likesRepository.deleteItem(
            item.toLike()
        )
    }

    /**
     * データベースから全てのアイテムを取得
     */
    fun getAllList() = viewModelScope.launch {
        uiDataList.clear()
        _uiState.emit(LikeUiState.Loading)

        runCatching {
            withContext(Dispatchers.IO) {
                likesRepository.getAllItemsStream().apply {
                    uiDataList += this.toPokemonListUiDataList()
                }
            }
        }
            .onSuccess {
                _uiState.emit(LikeUiState.Fetched(uiDataList = uiDataList))
            }
            .onFailure {
                Log.d("test", "Error(it)：$it")
                send(LikeUiEvent.Error(it))
                _uiState.emit(LikeUiState.ResultError)
            }
    }

    /**
     * Likeフラグを更新
     */
    fun updateIsLike(isLike: Boolean, pokemonNumber: Int) {
        uiDataList.map { data ->
            if (data.pokemonNumber == pokemonNumber) {
                data.isLike = isLike
            }
        }
    }
}
