package com.capstone.project.hondealz.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.FragmentHomeBinding
import com.capstone.project.hondealz.view.ViewModelFactory

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

        homeViewModel.fetchUserData()

        homeViewModel.userData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvGreeting.visibility = View.GONE
                }

                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE

                    val username = result.data.user?.username ?: "User"
                    binding.tvGreeting.text = getString(R.string.greetings, username)
                }

                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvGreeting.visibility = View.VISIBLE
                    binding.tvGreeting.text = getString(R.string.greetings, "User")
                }
            }
        }

        setupAction()
    }

    private fun setupAction() {
        binding.estimasiButton.setOnClickListener {

        }
    }
}