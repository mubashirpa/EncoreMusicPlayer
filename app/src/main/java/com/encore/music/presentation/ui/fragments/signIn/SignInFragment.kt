package com.encore.music.presentation.ui.fragments.signIn

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
import com.encore.music.core.Navigation
import com.encore.music.core.utils.GoogleAuthUtils
import com.encore.music.databinding.FragmentSignInBinding
import com.encore.music.presentation.navigation.navigateToMain
import com.encore.music.presentation.navigation.navigateToResetPassword
import com.encore.music.presentation.navigation.navigateToSignUp
import com.encore.music.presentation.ui.fragments.dialog.ProgressDialogFragment
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

        val emailField = binding.emailField.editText
        val passwordField = binding.passwordField.editText
        val rememberSwitch = binding.rememberSwitch

        navController.getNavigationResult<String>(
            viewLifecycleOwner,
            Navigation.Args.RESET_PASSWORD_EMAIL,
        ) { email ->
            showMessage(getString(Strings.success_send_password_reset_email, email))
        }

        viewModel.email.observe(viewLifecycleOwner) { email ->
            if (emailField?.text.toString() != email) {
                emailField?.setText(email)
            }
        }

        viewModel.password.observe(viewLifecycleOwner) { password ->
            if (passwordField?.text.toString() != password) {
                passwordField?.setText(password)
            }
        }

        viewModel.remember.observe(viewLifecycleOwner) { isChecked ->
            if (rememberSwitch.isChecked != isChecked) {
                rememberSwitch.isChecked = isChecked
            }
        }

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

        emailField?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignInUiEvent.OnEmailValueChange(text.toString()))
        }

        passwordField?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(SignInUiEvent.OnPasswordValueChange(text.toString()))
        }

        rememberSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onEvent(SignInUiEvent.OnRememberSwitchCheckedChange(isChecked))
        }

        binding.signInButton.setOnClickListener {
            viewModel.onEvent(
                SignInUiEvent.SignIn(
                    email = emailField?.text.toString(),
                    password = passwordField?.text.toString(),
                    remember = rememberSwitch.isChecked,
                ),
            )
        }

        binding.forgotPassword.setOnClickListener {
            navController.navigateToResetPassword(emailField?.text?.toString().orEmpty())
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
