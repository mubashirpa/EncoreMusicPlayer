package com.encore.music.domain.usecase.authentication

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.utils.AuthenticationUtils
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
                val errorMessage =
                    AuthenticationUtils.authErrors[e.errorCode] ?: Strings.auth_unknown_exception
                emit(Result.Error(UiText.StringResource(errorMessage)))
            } catch (e: FirebaseNetworkException) {
                emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
            }
        }
}
