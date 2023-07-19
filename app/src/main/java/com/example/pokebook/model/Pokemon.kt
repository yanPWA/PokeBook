package com.example.pokebook.model

import androidx.annotation.DrawableRes

sealed interface Pokemon

data class Profile(
    val number: Int = 0,
    val name: String = "",
    val type: String = "",
    val attribution: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val height: Int = 0,
    val weight: Int = 0
) : Pokemon

data class Performance(
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0
) : Pokemon
