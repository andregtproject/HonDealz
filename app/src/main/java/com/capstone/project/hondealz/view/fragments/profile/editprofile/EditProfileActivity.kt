package com.capstone.project.hondealz.view.fragments.profile.editprofile

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

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[EditProfileViewModel::class.java]

        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

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
                    is ResultState.Loading -> {

                    }
                    null -> {}
                }
            }
        }

        binding.btnSave.setOnClickListener {
            saveProfileChanges()
        }

        lifecycleScope.launch {
            viewModel.updateUserDataResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.btnSave.isEnabled = false
                    }
                    is ResultState.Success -> {
                        showSuccessToast("Profil berhasil diperbarui")
                        binding.btnSave.isEnabled = true
                        finish()
                    }
                    is ResultState.Error -> {
                        showErrorToast(result.statusCode, result.error)
                        binding.btnSave.isEnabled = true
                    }
                    null -> {}
                }
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordConfirmationDialog()
        }

        lifecycleScope.launch {
            viewModel.forgotPasswordResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        showSuccessToast(getString(R.string.reset_password_link_sent, userEmail))
                        binding.btnForgotPassword.isEnabled = true
                    }
                    is ResultState.Error -> {
                        showErrorToast(result.statusCode, result.error)
                        binding.btnForgotPassword.isEnabled = true
                    }
                    is ResultState.Loading -> {
                        binding.btnForgotPassword.isEnabled = false
                    }
                    null -> {}
                }
            }
        }

    }

    private fun saveProfileChanges() {
        val username = binding.etUsername.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        viewModel.updateUserData(username, fullName, email)
    }

    private fun showForgotPasswordConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_password))
            .setMessage(getString(R.string.confirmation_message_reset_password, userEmail))
            .setPositiveButton("Ya") { _, _ ->
                userEmail?.let { email ->
                    performForgotPassword(email)
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performForgotPassword(email: String) {
        viewModel.forgotPassword(email)
    }

    private fun showSuccessToast(message: String) {
        MotionToast.createColorToast(
            this,
            "Berhasil",
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    private fun showErrorToast(responseCode: Int, message: String) {
        val errorMessage = when (responseCode) {
            400 -> "Permintaan sudah dilakukan sebelumnya. Silakan coba lagi nanti."
            404 -> "Email tidak terdaftar."
            422 -> "Validasi error. Periksa kembali input Anda."
            500 -> "Terjadi kesalahan server. Silakan coba lagi nanti."
            else -> "Gagal mengirim link reset password: $message"
        }

        MotionToast.createColorToast(
            this,
            "Gagal",
            errorMessage,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }
}