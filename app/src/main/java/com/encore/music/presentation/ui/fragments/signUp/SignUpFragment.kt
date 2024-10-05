package com.encore.music.presentation.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.core.utils.GoogleAuthUtils
import com.encore.music.databinding.FragmentSignUpBinding
import com.encore.music.presentation.navigation.navigateToMain
import com.encore.music.presentation.navigation.navigateToSignIn
import com.encore.music.presentation.ui.fragments.dialog.ProgressDialogFragment
import com.google.android.material.snackbar.Snackbar
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

        val nameObserver =
            Observer<String> { email ->
                if (binding.nameField.editText
                        ?.text
                        .toString() != email
                ) {
                    binding.nameField.editText?.setText(email)
                }
            }
        val emailObserver =
            Observer<String> { email ->
                if (binding.emailField.editText
                        ?.text
                        .toString() != email
                ) {
                    binding.emailField.editText?.setText(email)
                }
            }
        val passwordObserver =
            Observer<String> { password ->
                if (binding.passwordField.editText
                        ?.text
                        .toString() != password
                ) {
                    binding.passwordField.editText?.setText(password)
                }
            }

        viewModel.name.observe(viewLifecycleOwner, nameObserver)
        viewModel.email.observe(viewLifecycleOwner, emailObserver)
        viewModel.password.observe(viewLifecycleOwner, passwordObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is SignUpUiState.EmailError -> {
                            binding.emailField.error = uiState.message?.asString(requireContext())
                        }

                        is SignUpUiState.NameError -> {
                            binding.nameField.error = uiState.message?.asString(requireContext())
                        }

                        is SignUpUiState.PasswordError -> {
                            binding.passwordField.error =
                                uiState.message?.asString(requireContext())
                        }

                        is SignUpUiState.SignUpError -> {
                            showMessage(uiState.message.asString(requireContext()))
                            if (progressDialog.isAdded) progressDialog.dismiss()
                        }

                        SignUpUiState.SignUpLoading -> {
                            if (!progressDialog.isAdded) {
                                progressDialog.show(
                                    childFragmentManager,
                                    ProgressDialogFragment.TAG,
                                )
                            }
                        }

                        SignUpUiState.SignUpSuccess -> {
                            if (progressDialog.isAdded) progressDialog.dismiss()
                            navController.navigateToMain()
                        }
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
                        showMessage(message)
                    },
                )
            }
        }

        binding.facebookLoginButton.setOnClickListener {
            showMessage(getString(Strings.coming_soon))
        }

        binding.signIn.setOnClickListener {
            navController.navigateToSignIn()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
