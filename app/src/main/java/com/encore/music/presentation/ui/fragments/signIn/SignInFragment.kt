package com.encore.music.presentation.ui.fragments.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.core.utils.GoogleAuthUtils
import com.encore.music.databinding.FragmentSignInBinding
import com.encore.music.presentation.navigation.Graph
import com.encore.music.presentation.navigation.Screen
import com.encore.music.presentation.ui.fragments.ProgressDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.encore.music.R.string as Strings

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModel()
    private val googleAuthUtils by lazy { GoogleAuthUtils(requireContext()) }
    private val progressDialog by lazy { ProgressDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val email = viewModel.uiState.value.email
                val password = viewModel.uiState.value.password

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
                                findNavController().run {
                                    popBackStack(Graph.Onboarding, true)
                                    navigate(Graph.Main)
                                }
                            }
                        }
                }

                viewModel.uiState.collect { uiState ->
                    binding.emailField.error = uiState.emailError?.asString(requireContext())
                    binding.passwordField.error = uiState.passwordError?.asString(requireContext())
                    binding.rememberSwitch.isChecked = uiState.remember

                    uiState.userMessage?.let { message ->
                        Snackbar
                            .make(
                                binding.root,
                                message.asString(requireContext()),
                                Snackbar.LENGTH_LONG,
                            ).show()

                        // Once the message is displayed and
                        // dismissed, notify the ViewModel.
                        viewModel.onEvent(SignInUiEvent.UserMessageShown)
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
                        binding.emailField.editText!!
                            .text
                            .toString(),
                    password =
                        binding.passwordField.editText!!
                            .text
                            .toString(),
                    remember = binding.rememberSwitch.isChecked,
                ),
            )
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(
                Screen.ResetPassword(
                    binding.emailField.editText!!
                        .text
                        .toString(),
                ),
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
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    },
                )
            }
        }

        binding.facebookLoginButton.setOnClickListener {
            Snackbar.make(binding.root, Strings.coming_soon, Snackbar.LENGTH_LONG).show()
        }

        binding.signUp.setOnClickListener {
            findNavController().navigate(Screen.SignUp) {
                popUpTo(Screen.SignIn) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
