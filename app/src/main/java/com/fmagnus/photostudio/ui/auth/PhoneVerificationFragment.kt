package com.fmagnus.photostudio.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fmagnus.photostudio.databinding.FragmentPhoneVerificationBinding
import com.fmagnus.photostudio.ui.viewmodel.PhoneVerificationViewModel

class PhoneVerificationFragment : Fragment() {

    private var _binding: FragmentPhoneVerificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneVerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyButton.setOnClickListener {
            val telegramId = binding.telegramIdEditText.text.toString().trim()
            if (telegramId.isEmpty()) {
                Toast.makeText(context, "Please enter your Telegram username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // In a real app, we might need the bot name from the user, or it could be a constant.
            // For now, let's assume a placeholder bot name.
            val botUsername = "YOUR_BOT_USERNAME" // THIS MUST BE REPLACED BY THE USER
            viewModel.startVerification(telegramId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.verificationState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is PhoneVerificationViewModel.VerificationState.AwaitingBot
            binding.statusText.isVisible = state is PhoneVerificationViewModel.VerificationState.AwaitingBot
            binding.verifyButton.isEnabled = state !is PhoneVerificationViewModel.VerificationState.AwaitingBot

            when (state) {
                is PhoneVerificationViewModel.VerificationState.AwaitingBot -> {
                    binding.statusText.text = "Opening Telegram to verify..."
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.deepLinkUrl))
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open Telegram. Is it installed?", Toast.LENGTH_LONG).show()
                    }
                }
                is PhoneVerificationViewModel.VerificationState.Success -> {
                    Toast.makeText(context, "Phone successfully verified: ${state.phoneNumber}", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is PhoneVerificationViewModel.VerificationState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                PhoneVerificationViewModel.VerificationState.Idle -> {
                    // Initial state
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
