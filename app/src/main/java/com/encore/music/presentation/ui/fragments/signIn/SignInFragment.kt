package com.encore.music.presentation.ui.fragments.signIn

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
import com.encore.music.core.Navigation
import com.encore.music.core.utils.GoogleAuthUtils
import com.encore.music.databinding.FragmentSignInBinding
import com.encore.music.presentation.navigation.navigateToMain
import com.encore.music.presentation.navigation.navigateToResetPassword
import com.encore.music.presentation.navigation.navigateToSignUp
import com.encore.music.presentation.ui.fragments.ProgressDialogFragment
import com.encore.music.presentation.utils.getNavigationResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.encore.music.R.string as Strings

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModel()
    private val navController by lazy { findNavController() }
    private val googleAuthUtils by lazy { GoogleAuthUtils(requireContext()) }
    private val progressDialog by lazy { ProgressDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        navController.getNavigationResult<String>(
            viewLifecycleOwner,
            Navigation.Args.RESET_PASSWORD_EMAIL,
        ) { email ->
            showMessage(getString(Strings.success_send_password_reset_email, email))
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
        val rememberObserver =
            Observer<Boolean> { isChecked ->
                if (binding.rememberSwitch.isChecked != isChecked) {
                    binding.rememberSwitch.isChecked = isChecked
                }
            }

        viewModel.email.observe(viewLifecycleOwner, emailObserver)
        viewModel.password.observe(viewLifecycleOwner, passwordObserver)
        viewModel.remember.observe(viewLifecycleOwner, rememberObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is SignInUiState.EmailError -> {
                            binding.emailField.error = uiState.message?.asString(requireContext())
                        }

                        is SignInUiState.PasswordError -> {
                            binding.passwordField.error =
                                uiState.message?.asString(requireContext())
                        }

                        is SignInUiState.SignInError -> {
                            showMessage(uiState.message.asString(requireContext()))
                            if (progressDialog.isAdded) progressDialog.dismiss()
                        }

                        SignInUiState.SignInLoading -> {
                            if (!progressDialog.isAdded) {
                                progressDialog.show(
                                    childFragmentManager,
                                    ProgressDialogFragment.TAG,
                                )
                            }
                        }

                        SignInUiState.SignInSuccess -> {
                            if (progressDialog.isAdded) progressDialog.dismiss()
                            navController.navigateToMain()
                        }
                    }
                }
            }
        }

        binding.emailField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignInUiEvent.OnEmailValueChange(text.toString()))
        }

        binding.passwordField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignInUiEvent.OnPasswordValueChange(text.toString()))
        }

        binding.rememberSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onEvent(SignInUiEvent.OnRememberSwitchCheckedChange(isChecked))
        }

        binding.signInButton.setOnClickListener {
            viewModel.onEvent(
                SignInUiEvent.SignIn(
                    email =
                        binding.emailField.editText
                            ?.text
                            .toString(),
                    password =
                        binding.passwordField.editText
                            ?.text
                            .toString(),
                    remember = binding.rememberSwitch.isChecked,
                ),
            )
        }

        binding.forgotPassword.setOnClickListener {
            navController.navigateToResetPassword(
                binding.emailField.editText
                    ?.text
                    ?.toString()
                    .orEmpty(),
            )
        }

        binding.googleLoginButton.setOnClickListener {
            lifecycleScope.launch {
                googleAuthUtils.initGoogleSignIn(
                    filterByAuthorizedAccounts = true,
                    onSignInSuccess = { token ->
                        viewModel.onEvent(SignInUiEvent.SignInWithGoogle(token))
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

        binding.signUp.setOnClickListener {
            navController.navigateToSignUp()
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
