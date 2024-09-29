package com.encore.music.presentation.ui.fragments.resetPassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.SendPasswordResetEmailUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ResetPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val validateEmail: ValidateEmail,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val email: String = savedStateHandle.toRoute<Screen.ResetPassword>().email

    init {
        _uiState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun onEvent(event: ResetPasswordUiEvent) {
        when (event) {
            is ResetPasswordUiEvent.OnEmailValueChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        email = event.email,
                        emailError = null,
                    )
                }
            }

            is ResetPasswordUiEvent.ResetPassword -> {
                sendPasswordResetEmail(event.email)
            }

            ResetPasswordUiEvent.UserMessageShown -> {
                _uiState.update { currentState ->
                    currentState.copy(userMessage = null)
                }
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        val emailResult = validateEmail.execute(email)

        _uiState.update { currentState ->
            currentState.copy(emailError = emailResult.error)
        }

        if (!emailResult.successful) return

        sendPasswordResetEmailUseCase(email)
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
                                isPasswordResetEmailSend = true,
                                openProgressDialog = false,
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
