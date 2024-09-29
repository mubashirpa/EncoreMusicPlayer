package com.encore.music.domain.usecase.authentication

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.repository.AuthenticationRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.encore.music.R.string as Strings

class CreateUserUseCase(
    private val repository: AuthenticationRepository,
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): Flow<Result<User>> =
        flow {
            try {
                emit(Result.Loading())
                val user =
                    repository.createUserWithEmailAndPassword(name, email, password)
                emit(Result.Success(user))
            } catch (e: FirebaseAuthUserCollisionException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_error_email_already_in_use)))
            } catch (e: FirebaseNetworkException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
            }
        }
}
