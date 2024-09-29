package com.encore.music.presentation.ui.fragments.signIn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.GoogleSignInUseCase
import com.encore.music.domain.usecase.authentication.SignInUseCase
import com.encore.music.domain.usecase.datastore.GetLoginPreferencesUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val getLoginPreferencesUseCase: GetLoginPreferencesUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    init {
        getLoginPreferences()
    }

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.OnEmailValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        email = event.email,
                        emailError = null,
                    )
                }
            }

            is SignInUiEvent.OnPasswordValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        password = event.password,
                        passwordError = null,
                    )
                }
            }

            is SignInUiEvent.OnRememberSwitchCheckedChange -> {
                _uiState.update { currentState ->
                    currentState.copy(remember = event.checked)
                }
            }

            is SignInUiEvent.SignIn -> {
                signInWithEmailAndPassword(
                    email = event.email,
                    password = event.password,
                    remember = event.remember,
                )
            }

            is SignInUiEvent.SignInWithGoogle -> {
                signInWithGoogle(event.token)
            }

            SignInUiEvent.UserMessageShown -> {
                _uiState.update { currentState ->
                    currentState.copy(userMessage = null)
                }
            }
        }
    }

    private fun signInWithEmailAndPassword(
        email: String,
        password: String,
        remember: Boolean,
    ) {
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)

        _uiState.update { currentState ->
            currentState.copy(
                emailError = emailResult.error,
                passwordError = passwordResult.error,
            )
        }

        val hasError =
            listOf(
                emailResult,
                passwordResult,
            ).any { !it.successful }

        if (hasError) return

        signInUseCase(email, password, remember)
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

    private fun signInWithGoogle(idToken: String) {
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

    private fun getLoginPreferences() {
        viewModelScope.launch {
            getLoginPreferencesUseCase().collectLatest { preferences ->
                Log.d("TAG", "getLoginPreferences: $preferences")
                _uiState.update { currentState ->
                    currentState.copy(
                        email = preferences.email,
                        password = preferences.password,
                        remember = preferences.remember,
                    )
                }
            }
        }
    }
}
