package com.encore.music.presentation.ui.fragments.search

sealed class SearchUiEvent {
    data object OnRetry : SearchUiEvent()
}
