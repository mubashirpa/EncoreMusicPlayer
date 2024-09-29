package com.encore.music.domain.usecase.authentication

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.repository.AuthenticationRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.encore.music.R.string as Strings

class SendPasswordResetEmailUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke(email: String): Flow<Result<Boolean>> =
        flow {
            try {
                emit(Result.Loading())
                repository.sendPasswordResetEmail(email)
                emit(Result.Success(true))
            } catch (e: FirebaseAuthInvalidUserException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_error_user_not_found, email)))
            } catch (e: FirebaseNetworkException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
            }
        }
}
