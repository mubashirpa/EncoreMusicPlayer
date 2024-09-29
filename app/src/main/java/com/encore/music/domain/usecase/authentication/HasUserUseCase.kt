package com.encore.music.domain.usecase.authentication

import com.encore.music.domain.repository.AuthenticationRepository

class HasUserUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke() = repository.hasUser
}
