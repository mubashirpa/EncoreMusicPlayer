package com.encore.music.presentation.ui.fragments.resetPassword

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
import com.encore.music.databinding.FragmentResetPasswordBinding
import com.encore.music.presentation.ui.fragments.ProgressDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResetPasswordViewModel by viewModel()
    private val progressDialog by lazy { ProgressDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

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

                binding.emailField.editText?.setText(email)

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
                        .map { it.isPasswordResetEmailSend }
                        .distinctUntilChanged()
                        .collect { success ->
                            if (success) {
                                findNavController().navigateUp()
                            }
                        }
                }

                viewModel.uiState.collect { uiState ->
                    binding.emailField.error = uiState.emailError?.asString(requireContext())

                    uiState.userMessage?.let { message ->
                        Snackbar
                            .make(
                                binding.root,
                                message.asString(requireContext()),
                                Snackbar.LENGTH_LONG,
                            ).show()

                        // Once the message is displayed and
                        // dismissed, notify the ViewModel.
                        viewModel.onEvent(ResetPasswordUiEvent.UserMessageShown)
                    }
                }
            }
        }

        binding.emailField.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(ResetPasswordUiEvent.OnEmailValueChange(text.toString()))
        }

        binding.resetPasswordButton.setOnClickListener {
            viewModel.onEvent(
                ResetPasswordUiEvent.ResetPassword(
                    binding.emailField.editText
                        ?.text
                        .toString(),
                ),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
