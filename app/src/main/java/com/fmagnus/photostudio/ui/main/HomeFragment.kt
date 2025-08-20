package com.fmagnus.photostudio.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fmagnus.photostudio.databinding.FragmentHomeBinding
import com.fmagnus.photostudio.ui.viewmodel.ServicesViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServicesViewModel by viewModels()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchServices()
    }

    private fun setupRecyclerView() {
        serviceAdapter = ServiceAdapter { service ->
            val action = HomeFragmentDirections.actionHomeFragmentToScheduleFragment(service.id)
            findNavController().navigate(action)
        }
        binding.servicesRecyclerView.adapter = serviceAdapter
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            serviceAdapter.submitList(services)
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
