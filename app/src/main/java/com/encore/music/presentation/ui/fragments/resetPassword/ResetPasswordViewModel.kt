package com.encore.music.presentation.ui.fragments.resetPassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.SendPasswordResetEmailUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val validateEmail: ValidateEmail,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<ResetPasswordUiState>()
    val uiState: SharedFlow<ResetPasswordUiState> = _uiState

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>(savedStateHandle.toRoute<Screen.ResetPassword>().email)
    }

    fun onEvent(event: ResetPasswordUiEvent) {
        when (event) {
            is ResetPasswordUiEvent.OnEmailValueChange -> {
                email.value = event.email
                viewModelScope.launch {
                    _uiState.emit(ResetPasswordUiState.EmailError(null))
                }
            }

            is ResetPasswordUiEvent.ResetPassword -> {
                sendPasswordResetEmail(event.email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        val emailResult = validateEmail.execute(email)

        viewModelScope.launch {
            _uiState.emit(ResetPasswordUiState.EmailError(emailResult.error))
        }

        if (!emailResult.successful) return

        sendPasswordResetEmailUseCase(email)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        _uiState.emit(ResetPasswordUiState.ResetPasswordError(result.message!!))
                    }

                    is Result.Loading -> {
                        _uiState.emit(ResetPasswordUiState.ResetPasswordLoading)
                    }

                    is Result.Success -> {
                        _uiState.emit(ResetPasswordUiState.ResetPasswordSuccess)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
