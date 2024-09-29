package com.encore.music.presentation.ui.fragments.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.CreateUserUseCase
import com.encore.music.domain.usecase.authentication.GoogleSignInUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.domain.usecase.validation.ValidateName
import com.encore.music.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SignUpViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.OnEmailValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        email = event.email,
                        emailError = null,
                    )
                }
            }

            is SignUpUiEvent.OnNameValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        name = event.name,
                        nameError = null,
                    )
                }
            }

            is SignUpUiEvent.OnPasswordValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        password = event.password,
                        passwordError = null,
                    )
                }
            }

            is SignUpUiEvent.SignUp -> {
                createUserWithEmailAndPassword(
                    name = event.name,
                    email = event.email,
                    password = event.password,
                )
            }

            is SignUpUiEvent.SignUpWithGoogle -> {
                signUpWithGoogle(event.token)
            }

            SignUpUiEvent.UserMessageShown -> {
                _uiState.update { currentState ->
                    currentState.copy(userMessage = null)
                }
            }
        }
    }

    private fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ) {
        val nameResult = validateName.execute(name)
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)

        _uiState.update { currentState ->
            currentState.copy(
                nameError = nameResult.error,
                emailError = emailResult.error,
                passwordError = passwordResult.error,
            )
        }

        val hasError =
            listOf(
                nameResult,
                emailResult,
                passwordResult,
            ).any { !it.successful }

        if (hasError) return

        createUserUseCase(name, email, password)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                        }
                    }

                    is Result.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(openProgressDialog = true)
                        }
                    }

                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isUserLoggedIn = true,
                                openProgressDialog = false,
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun signUpWithGoogle(idToken: String) {
        googleSignInUseCase(idToken).onEach { result ->
            when (result) {
                is Result.Empty -> {}

                is Result.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            openProgressDialog = false,
                            userMessage = result.message,
                        )
                    }
                }

                is Result.Loading -> {
                    _uiState.update { currentState ->
                        currentState.copy(openProgressDialog = true)
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isUserLoggedIn = true,
                            openProgressDialog = false,
                        )
                    }
                }
            }
        }
    }
}
