package com.example.pokebook.ui.viewModel

interface DefaultHeader {
    /**
     * 「次へ」ボタン押下してポケモンリスト取得
     */
    fun onClickNext()
    /**
     * 「戻る」ボタン押下して一つ前のポケモンリストを取得
     */
    fun onClickBack()
}