package com.encore.music.domain.usecase.authentication

import com.encore.music.domain.repository.AuthenticationRepository

class SignOutUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke() {
        repository.signOut()
    }
}
