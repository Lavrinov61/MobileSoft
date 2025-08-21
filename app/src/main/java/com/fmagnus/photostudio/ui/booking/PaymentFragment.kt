package com.fmagnus.photostudio.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fmagnus.photostudio.databinding.FragmentPaymentBinding
import com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentFragmentArgs by navArgs()
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchServiceDetails(args.serviceId)
        observeViewModel()

        binding.payButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(context, "Please enter your name and phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.startPaymentProcess(args.serviceId, args.slotId, name, phone)
        }
    }

    private fun observeViewModel() {
        viewModel.service.observe(viewLifecycleOwner) { service ->
            if (service != null) {
                binding.serviceText.text = service.name
                binding.priceText.text = "$${service.price}"
            } else {
                // Handle error case
                binding.serviceText.text = "Error"
                binding.priceText.text = "N/A"
                binding.payButton.isEnabled = false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // We can use this for the initial loading of service details
            binding.progressBar.isVisible = isLoading && viewModel.paymentState.value == com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel.PaymentState.Idle
        }

        viewModel.paymentState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state is com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel.PaymentState.Processing
            binding.payButton.isEnabled = state !is com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel.PaymentState.Processing

            when (state) {
                is com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel.PaymentState.Success -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    // TODO: Here we would call the createBooking transaction
                    // For now, just navigate back to the home screen
                    // findNavController().navigate(R.id.action_global_homeFragment)
                }
                is com.fmagnus.photostudio.ui.viewmodel.PaymentViewModel.PaymentState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Idle
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
