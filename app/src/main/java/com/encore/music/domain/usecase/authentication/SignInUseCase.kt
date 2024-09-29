package com.encore.music.domain.usecase.authentication

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.preferences.LoginPreferences
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.DatastoreRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.encore.music.R.string as Strings

class SignInUseCase(
    private val repository: AuthenticationRepository,
    private val dataStoreRepository: DatastoreRepository,
) {
    operator fun invoke(
        email: String,
        password: String,
        remember: Boolean = false,
    ): Flow<Result<User>> =
        flow {
            try {
                emit(Result.Loading())
                if (remember) {
                    dataStoreRepository.saveLoginPreferences(
                        LoginPreferences(
                            email = email,
                            password = password,
                            remember = true,
                        ),
                    )
                } else {
                    dataStoreRepository.clearLoginPreferences()
                }
                val user =
                    repository.signInWithEmailAndPassword(email, password)
                emit(Result.Success(user))
            } catch (e: FirebaseAuthException) {
                when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> {
                        emit(Result.Error(UiText.StringResource(Strings.auth_error_wrong_password)))
                    }

                    "ERROR_USER_NOT_FOUND" -> {
                        emit(
                            Result.Error(
                                UiText.StringResource(
                                    Strings.auth_error_user_not_found,
                                    email,
                                ),
                            ),
                        )
                    }

                    "ERROR_USER_DISABLED" -> {
                        emit(Result.Error(UiText.StringResource(Strings.auth_error_user_disabled)))
                    }

                    "ERROR_TOO_MANY_REQUESTS" -> {
                        emit(Result.Error(UiText.StringResource(Strings.auth_error_too_many_requests)))
                    }

                    else -> {
                        emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                    }
                }
            } catch (e: FirebaseNetworkException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
            }
        }
}
