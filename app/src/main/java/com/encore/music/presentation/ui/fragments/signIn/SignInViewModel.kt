package com.encore.music.presentation.ui.fragments.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.usecase.authentication.GoogleSignInUseCase
import com.encore.music.domain.usecase.authentication.SignInUseCase
import com.encore.music.domain.usecase.datastore.GetLoginPreferencesUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val getLoginPreferencesUseCase: GetLoginPreferencesUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<SignInUiState>()
    val uiState: SharedFlow<SignInUiState> = _uiState

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val remember: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        getLoginPreferences()
    }

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.OnEmailValueChange -> {
                email.value = event.email
                viewModelScope.launch {
                    _uiState.emit(SignInUiState.EmailError(null))
                }
            }

            is SignInUiEvent.OnPasswordValueChange -> {
                password.value = event.password
                viewModelScope.launch {
                    _uiState.emit(SignInUiState.PasswordError(null))
                }
            }

            is SignInUiEvent.OnRememberSwitchCheckedChange -> {
                remember.value = event.isChecked
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
        }
    }

    private fun signInWithEmailAndPassword(
        email: String,
        password: String,
        remember: Boolean,
    ) {
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)

        viewModelScope.launch {
            _uiState.emit(SignInUiState.EmailError(emailResult.error))
            _uiState.emit(SignInUiState.PasswordError(passwordResult.error))
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
                        _uiState.emit(SignInUiState.SignInError(result.message!!))
                    }

                    is Result.Loading -> {
                        _uiState.emit(SignInUiState.SignInLoading)
                    }

                    is Result.Success -> {
                        _uiState.emit(SignInUiState.SignInSuccess)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun signInWithGoogle(idToken: String) {
        googleSignInUseCase(idToken)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        _uiState.emit(SignInUiState.SignInError(result.message!!))
                    }

                    is Result.Loading -> {
                        _uiState.emit(SignInUiState.SignInLoading)
                    }

                    is Result.Success -> {
                        _uiState.emit(SignInUiState.SignInSuccess)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getLoginPreferences() {
        viewModelScope.launch {
            getLoginPreferencesUseCase().firstOrNull()?.let { preferences ->
                email.value = preferences.email
                password.value = preferences.password
                remember.value = preferences.remember
            }
        }
    }
}
