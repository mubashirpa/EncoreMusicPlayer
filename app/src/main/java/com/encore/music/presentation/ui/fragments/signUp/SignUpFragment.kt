package com.encore.music.presentation.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.core.utils.GoogleAuthUtils
import com.encore.music.databinding.FragmentSignUpBinding
import com.encore.music.presentation.navigation.navigateToMain
import com.encore.music.presentation.navigation.navigateToSignIn
import com.encore.music.presentation.ui.fragments.dialog.ProgressDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.encore.music.R.string as Strings

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModel()
    private val navController by lazy { findNavController() }
    private val googleAuthUtils by lazy { GoogleAuthUtils(requireContext()) }
    private val progressDialog by lazy { ProgressDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val name = viewModel.uiState.value.name
                val email = viewModel.uiState.value.email
                val password = viewModel.uiState.value.password

                binding.nameField.editText?.setText(name)
                binding.emailField.editText?.setText(email)
                binding.passwordField.editText?.setText(password)

                launch {
                    viewModel.uiState
                        .map { it.openProgressDialog }
                        .distinctUntilChanged()
                        .collect { open ->
                            if (open) {
                                progressDialog.show(
                                    childFragmentManager,
                                    ProgressDialogFragment.TAG,
                                )
                            } else {
                                if (progressDialog.isAdded) progressDialog.dismiss()
                            }
                        }
                }

                launch {
                    viewModel.uiState
                        .map { it.isUserLoggedIn }
                        .distinctUntilChanged()
                        .collect { success ->
                            if (success) {
                                navController.navigateToMain()
                            }
                        }
                }

                viewModel.uiState.collect { uiState ->
                    binding.nameField.error = uiState.nameError?.asString(requireContext())
                    binding.emailField.error = uiState.emailError?.asString(requireContext())
                    binding.passwordField.error = uiState.passwordError?.asString(requireContext())

                    uiState.userMessage?.let { message ->
                        Snackbar
                            .make(
                                binding.root,
                                message.asString(requireContext()),
                                Snackbar.LENGTH_LONG,
                            ).show()

                        // Once the message is displayed and
                        // dismissed, notify the ViewModel.
                        viewModel.onEvent(SignUpUiEvent.UserMessageShown)
                    }
                }
            }
        }

        binding.nameField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignUpUiEvent.OnNameValueChange(text.toString()))
        }

        binding.emailField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignUpUiEvent.OnEmailValueChange(text.toString()))
        }

        binding.passwordField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignUpUiEvent.OnPasswordValueChange(text.toString()))
        }

        binding.signUpButton.setOnClickListener {
            viewModel.onEvent(
                SignUpUiEvent.SignUp(
                    name =
                        binding.nameField.editText!!
                            .text
                            .toString(),
                    email =
                        binding.emailField.editText!!
                            .text
                            .toString(),
                    password =
                        binding.passwordField.editText!!
                            .text
                            .toString(),
                ),
            )
        }

        binding.googleLoginButton.setOnClickListener {
            lifecycleScope.launch {
                googleAuthUtils.initGoogleSignIn(
                    onSignInSuccess = { token ->
                        viewModel.onEvent(SignUpUiEvent.SignUpWithGoogle(token))
                    },
                    onSignInFailure = { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    },
                )
            }
        }

        binding.facebookLoginButton.setOnClickListener {
            Snackbar.make(binding.root, Strings.coming_soon, Snackbar.LENGTH_LONG).show()
        }

        binding.signIn.setOnClickListener {
            navController.navigateToSignIn()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
