package com.capstone.project.hondealz.view.profile.editprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityEditProfileBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[EditProfileViewModel::class.java]

        viewModel.getUserProfile()

        lifecycleScope.launch {
            viewModel.userProfileResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        val userData = result.data.user
                        binding.apply {
                            etUsername.setText(userData?.username ?: "")
                            etFullName.setText(userData?.name ?: "")
                            etEmail.setText(userData?.email ?: "")
                        }
                        userEmail = userData?.email
                    }

                    is ResultState.Error -> {
                        showErrorToast(result.statusCode, result.error)
                    }

                    else -> {}
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmNewPassword = binding.etConfirmNewPassword.text.toString().trim()

            val isDataChanged = username != (binding.etUsername.tag as? String) ||
                    fullName != (binding.etFullName.tag as? String) ||
                    email != (binding.etEmail.tag as? String)

            if (newPassword == confirmNewPassword) {
                viewModel.updatePassword(oldPassword, newPassword, confirmNewPassword)
                if (isDataChanged) {
                    viewModel.updateUserData(username, fullName, email)
                } else {
                    showSuccessToast(getString(R.string.success_update_profile))
                    finish()
                }
            } else {
                showErrorToast(0, getString(R.string.password_mismatch))
            }
        }

        lifecycleScope.launch {
            viewModel.updateUserDataResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> binding.btnSave.isEnabled = false
                    is ResultState.Success -> {
                        showSuccessToast(getString(R.string.success_update_profile))
                        binding.btnSave.isEnabled = true
                        finish()
                    }

                    is ResultState.Error -> {
                        if (result.statusCode != 400 || result.error != getString(R.string.nothing_to_update)) {
                            showErrorToast(result.statusCode, result.error)
                        }
                        binding.btnSave.isEnabled = true
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updatePasswordResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> binding.btnSave.isEnabled = false
                    is ResultState.Success -> {
                        showSuccessToast(result.data)
                        binding.btnSave.isEnabled = true
                    }

                    is ResultState.Error -> {
                        showErrorToast(result.statusCode, result.error)
                        binding.btnSave.isEnabled = true
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showSuccessToast(message: String) {
        MotionToast.createColorToast(
            this, getString(R.string.success), message,
            MotionToastStyle.SUCCESS, MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION, null
        )
    }

    private fun showErrorToast(responseCode: Int, message: String) {
        val errorMessage = when (responseCode) {
            400 -> getString(R.string.request_already_made)
            404 -> getString(R.string.unauthorized_account)
            422 -> getString(R.string.error_validation)
            500 -> getString(R.string.internal_server_error)
            else -> message
        }
        MotionToast.createColorToast(
            this, getString(R.string.fail), errorMessage,
            MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION, null
        )
    }
}