package com.encore.music.domain.usecase.authentication

import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke(): Flow<User?> = repository.currentUser
}
