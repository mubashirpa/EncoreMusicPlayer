package com.encore.music.presentation.ui.activities

import androidx.lifecycle.ViewModel
import com.encore.music.domain.usecase.authentication.HasUserUseCase

class MainViewModel(
    hasUserUseCase: HasUserUseCase,
) : ViewModel() {
    var isLoggedIn = false

    init {
        isLoggedIn = hasUserUseCase()
    }
}
