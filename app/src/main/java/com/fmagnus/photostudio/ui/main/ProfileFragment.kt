package com.fmagnus.photostudio.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.fmagnus.photostudio.R
import com.fmagnus.photostudio.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.text = auth.currentUser?.email ?: "No user logged in"

        binding.verifyPhoneButton.setOnClickListener {
            // To navigate to a destination outside the nested graph, we find the parent NavController
            (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
                ?.navController?.navigate(R.id.action_mainFragment_to_phoneVerificationFragment)
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            (requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
                ?.navController?.navigate(R.id.action_global_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
