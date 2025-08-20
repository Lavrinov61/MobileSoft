package com.fmagnus.photostudio.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fmagnus.photostudio.databinding.FragmentBookingBinding
import com.fmagnus.photostudio.ui.viewmodel.BookingViewModel

class BookingFragment : DialogFragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()
    private val args: BookingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // We could pass more details to show a richer summary
        binding.bookingDetailsText.text = "Confirming booking for service ID: ${args.serviceId}"

        binding.confirmButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(context, "Please enter your name and phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.createBooking(args.serviceId, args.slotId, name, phone)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.bookingResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = result.message == "Loading..."
            binding.confirmButton.isEnabled = result.message != "Loading..."

            if (result.message != "Loading...") {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                if (result.success) {
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
