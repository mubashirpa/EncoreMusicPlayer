package com.encore.music.presentation.ui.fragments.onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.core.Constants
import com.encore.music.databinding.FragmentOnboardingBinding
import com.encore.music.presentation.navigation.navigateToSignIn
import java.io.IOException
import java.io.InputStream

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val window by lazy { requireActivity().window }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        try {
            val inputStream: InputStream =
                requireContext().assets.open(Constants.ONBOARDING_BG_IMAGE_FILE_NAME)
            val drawable = Drawable.createFromStream(inputStream, null)
            binding.background.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.getStartedButton.setOnClickListener {
            findNavController().navigateToSignIn()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            !isDarkMode
        _binding = null
    }
}
