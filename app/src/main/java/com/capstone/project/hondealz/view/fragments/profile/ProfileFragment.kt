package com.capstone.project.hondealz.view.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.databinding.FragmentProfileBinding
import com.capstone.project.hondealz.view.EditProfileActivity
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.main.MainActivity
import com.capstone.project.hondealz.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var honDealzRepository: HonDealzRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        honDealzRepository = HonDealzRepository(ApiConfig.getApiService(), userPreference)
        setupViewModel()
        binding.logoutButton.setOnClickListener { logout() }
        binding.panduanCardView.setOnClickListener {
            val pdfUrl = "https://drive.google.com/file/d/12vUeulo2NfY3n8VgvQhfZtAhypEFZ-Qa/view?usp=sharing"
            val intent = Intent(requireContext(), UserManualActivity::class.java)
            intent.putExtra(UserManualActivity.EXTRA_PDF_URL, pdfUrl)
            startActivity(intent)
        }
        binding.aboutCardView.setOnClickListener {
            (activity as? MainActivity)?.goToAboutActivity()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val userModel = honDealzRepository.getSession().first()
            val userDataResult = honDealzRepository.getUserData()

            if (userDataResult is ResultState.Success) {
                val userData = userDataResult.data.user
                binding.apply {
                    tvfullName.text = userData?.name ?: ""
                    tvUsername.text = userData?.username ?: ""
                    tvEmail.text = userModel.email
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            profileViewModel.getUserData().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.profileContent.visibility = View.GONE
                    }
                    is ResultState.Success -> {
                        binding.profileContent.visibility = View.VISIBLE
                        val userData = result.data.user
                        binding.tvfullName.text = userData?.name
                        binding.tvUsername.text = userData?.username
                        binding.tvEmail.text = userData?.email

                        binding.editIcon.setOnClickListener {
                            val intent = Intent(requireContext(), EditProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    is ResultState.Error -> {
                        binding.profileContent.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewModel() {
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[ProfileViewModel::class.java]
    }

    private fun logout() {
        lifecycleScope.launch {
            userPreference.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
            requireActivity().finish()
        }
    }
}