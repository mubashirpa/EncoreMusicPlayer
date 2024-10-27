package com.encore.music.presentation.ui.activities

import com.encore.music.core.UiText

sealed class MainEvent {
    data class ShowMessage(
        val message: UiText,
    ) : MainEvent()

    data object StartService : MainEvent()
}
