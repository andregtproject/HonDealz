package com.capstone.project.hondealz.view.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.FragmentHomeBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.login.LoginActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tambahkan log untuk debugging
        Log.d("HomeFragment", "onViewCreated called")

        // Observasi status login sebelum fetch user data
        homeViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel.isLogin) {
                homeViewModel.fetchUserData()
            }
        }

        homeViewModel.userData.observe(viewLifecycleOwner) { result ->
            // Tambahkan log untuk debugging
            Log.d("HomeFragment", "User data state: $result")

            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvGreeting.visibility = View.GONE
                }

                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE
                    val username = result.data.user?.username
                    binding.tvGreeting.text = getString(R.string.greetings, username)
                }

                is ResultState.Error -> {
                    // Log error untuk debugging
                    Log.e("HomeFragment", "Error: ${result.error}, Status Code: ${result.statusCode}")

                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE

                    // Pastikan pengecekan token expired
                    if (result.statusCode == 401 || result.statusCode == 403) {
                        showTokenExpiredDialog()
                    }
                }
            }
        }

        setupAction()
    }

    private fun showTokenExpiredDialog() {
        // Pastikan dialog ditampilkan di main thread
        activity?.runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle("Session Expired")
                .setMessage("Your session has expired. Please login again.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    homeViewModel.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun setupAction() {
        binding.estimasiButton.setOnClickListener {
            // Implementasi action button
        }
    }
}