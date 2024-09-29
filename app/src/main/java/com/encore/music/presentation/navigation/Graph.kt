package com.encore.music.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Graph {
    @Serializable
    data object Onboarding : Graph()

    @Serializable
    data object Main : Graph()
}
