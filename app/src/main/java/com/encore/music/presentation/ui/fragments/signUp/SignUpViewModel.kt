package com.encore.music.presentation.ui.fragments.signUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.CreateUserUseCase
import com.encore.music.domain.usecase.authentication.GoogleSignInUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.domain.usecase.validation.ValidateName
import com.encore.music.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<SignUpUiState>()
    val uiState: SharedFlow<SignUpUiState> = _uiState

    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.OnEmailValueChange -> {
                email.value = event.email
                viewModelScope.launch {
                    _uiState.emit(SignUpUiState.EmailError(null))
                }
            }

            is SignUpUiEvent.OnNameValueChange -> {
                name.value = event.name
                viewModelScope.launch {
                    _uiState.emit(SignUpUiState.NameError(null))
                }
            }

            is SignUpUiEvent.OnPasswordValueChange -> {
                password.value = event.password
                viewModelScope.launch {
                    _uiState.emit(SignUpUiState.PasswordError(null))
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

        viewModelScope.launch {
            _uiState.emit(SignUpUiState.NameError(nameResult.error))
            _uiState.emit(SignUpUiState.EmailError(emailResult.error))
            _uiState.emit(SignUpUiState.PasswordError(passwordResult.error))
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
                        _uiState.emit(SignUpUiState.SignUpError(result.message!!))
                    }

                    is Result.Loading -> {
                        _uiState.emit(SignUpUiState.SignUpLoading)
                    }

                    is Result.Success -> {
                        _uiState.emit(SignUpUiState.SignUpSuccess)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun signUpWithGoogle(idToken: String) {
        googleSignInUseCase(idToken).onEach { result ->
            when (result) {
                is Result.Empty -> {}

                is Result.Error -> {
                    _uiState.emit(SignUpUiState.SignUpError(result.message!!))
                }

                is Result.Loading -> {
                    _uiState.emit(SignUpUiState.SignUpLoading)
                }

                is Result.Success -> {
                    _uiState.emit(SignUpUiState.SignUpSuccess)
                }
            }
        }
    }
}
