package com.encore.music.presentation.ui.fragments.resetPassword

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
import com.encore.music.databinding.FragmentResetPasswordBinding
import com.encore.music.presentation.ui.fragments.dialog.ProgressDialogFragment
import com.encore.music.presentation.utils.setNavigationResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResetPasswordViewModel by viewModel()
    private val navController by lazy { findNavController() }
    private val progressDialog by lazy { ProgressDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = binding.emailField.editText

        viewModel.email.observe(viewLifecycleOwner) { email ->
            if (emailField?.text.toString() != email) {
                emailField?.setText(email)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ResetPasswordUiState.EmailError -> {
                            binding.emailField.error = uiState.message?.asString(requireContext())
                        }

                        is ResetPasswordUiState.ResetPasswordError -> {
                            showMessage(uiState.message.asString(requireContext()))
                            if (progressDialog.isAdded) progressDialog.dismiss()
                        }

                        ResetPasswordUiState.ResetPasswordLoading -> {
                            if (!progressDialog.isAdded) {
                                progressDialog.show(
                                    childFragmentManager,
                                    ProgressDialogFragment.TAG,
                                )
                            }
                        }

                        ResetPasswordUiState.ResetPasswordSuccess -> {
                            if (progressDialog.isAdded) progressDialog.dismiss()
                            navController.setNavigationResult(
                                Navigation.Args.RESET_PASSWORD_EMAIL,
                                emailField?.text.toString(),
                            )
                            navController.navigateUp()
                        }
                    }
                }
            }
        }

        emailField?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(ResetPasswordUiEvent.OnEmailValueChange(text.toString()))
        }

        binding.resetPasswordButton.setOnClickListener {
            viewModel.onEvent(ResetPasswordUiEvent.ResetPassword(emailField?.text.toString()))
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
