package com.encore.music.presentation.ui.activities

sealed class MainEvent {
    data object StartService : MainEvent()
}
