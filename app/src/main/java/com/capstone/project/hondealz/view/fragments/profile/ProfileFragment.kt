package com.capstone.project.hondealz.view.fragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.databinding.DialogReportBugBinding
import com.capstone.project.hondealz.databinding.FragmentProfileBinding
import com.capstone.project.hondealz.view.editprofile.EditProfileActivity
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.main.MainActivity
import com.capstone.project.hondealz.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference
    private lateinit var honDealzRepository: HonDealzRepository
    private lateinit var userManualViewModel: UserManualViewModel
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        honDealzRepository = HonDealzRepository(ApiConfig.getApiService(), userPreference)
        userManualViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance(requireContext()))[UserManualViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languageCardView.setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.logoutButton.setOnClickListener { logout() }
        binding.panduanCardView.setOnClickListener {
            val intent = Intent(requireContext(), UserManualActivity::class.java)
            intent.putExtra(UserManualActivity.EXTRA_PDF_URL, userManualViewModel.pdfUrl)
            startActivity(intent)
        }
        binding.aboutCardView.setOnClickListener {
            (activity as? MainActivity)?.goToAboutActivity()
        }
        binding.reportBugButton.setOnClickListener { showReportBugDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            val userModel = honDealzRepository.getSession().first()
            val userDataResult = honDealzRepository.getUserData()

            if (userDataResult is ResultState.Success) {
                val userData = userDataResult.data.user
                binding.apply {
                    tvfullName.text = userData?.name ?: ""
                    tvUsername.text = userData?.username ?: ""
                    tvEmail.text = userModel.email
                    profileViewModel.updateUserData(userData?.name ?: "", userModel.email)
                }
            }
        }

        lifecycleScope.launch {
            profileViewModel.getUserData().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.profileContent.visibility = View.GONE
                        binding.loadingIndicator.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.profileContent.visibility = View.VISIBLE
                        binding.loadingIndicator.visibility = View.GONE
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
                        binding.loadingIndicator.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launch {
            val isDarkMode = userPreference.isDarkMode().first()
            binding.themeSwitch.isChecked = isDarkMode
            updateTheme(isDarkMode)
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                userPreference.saveDarkMode(isChecked)
                updateTheme(isChecked)
            }
        }
    }

    private fun updateTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logout() {
        lifecycleScope.launch {
            userPreference.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun showReportBugDialog() {
        val dialogBinding = DialogReportBugBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSend.setOnClickListener {
            val subject = dialogBinding.etSubject.text.toString()
            val message = dialogBinding.etMessage.text.toString()
            sendEmail(subject, message)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendEmail(subject: String, message: String) {
        val email = profileViewModel.email.value ?: binding.tvEmail.text.toString()
        val fullname = profileViewModel.fullname.value ?: binding.tvfullName.text.toString()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("rafie7498@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(
                Intent.EXTRA_TEXT,
                "Fullname: $fullname\n" +
                        "Email: $email\n\n" +
                        message
            )
        }
        startActivity(Intent.createChooser(intent, "Send email"))
    }
}