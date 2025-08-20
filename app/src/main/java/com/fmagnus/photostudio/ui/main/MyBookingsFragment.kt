package com.fmagnus.photostudio.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fmagnus.photostudio.databinding.FragmentMyBookingsBinding
import com.fmagnus.photostudio.ui.viewmodel.MyBookingsViewModel

class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyBookingsViewModel by viewModels()
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchUserBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter()
        binding.bookingsRecyclerView.adapter = bookingAdapter
    }

    private fun observeViewModel() {
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            bookingAdapter.submitList(bookings)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
