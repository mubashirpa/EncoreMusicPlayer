package com.encore.music.presentation.ui.fragments.library

import com.encore.music.core.UiText

sealed class LibraryUiEvent {
    data class OnOpenCreatePlaylistBottomSheetChange(
        val open: Boolean,
    ) : LibraryUiEvent()

    data class OnOpenProgressDialogChange(
        val open: Boolean,
    ) : LibraryUiEvent()

    data class OnShowSnackBar(
        val message: UiText,
    ) : LibraryUiEvent()
}
