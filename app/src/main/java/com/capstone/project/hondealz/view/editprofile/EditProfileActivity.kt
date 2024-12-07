package com.capstone.project.hondealz.view.editprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var honDealzRepository: HonDealzRepository
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference.getInstance(dataStore)
        honDealzRepository = HonDealzRepository(ApiConfig.getApiService(), userPreference)

        lifecycleScope.launch {
            val userModel = honDealzRepository.getSession().first()
            userEmail = userModel.email
            val userDataResult = honDealzRepository.getUserData()

            if (userDataResult is ResultState.Success) {
                val userData = userDataResult.data.user
                binding.apply {
                    etUsername.setText(userData?.username ?: "")
                    etFullName.setText(userData?.name ?: "")
                    etEmail.setText(userModel.email)
                }
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordConfirmationDialog()
        }
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
        lifecycleScope.launch {
            when (val result = honDealzRepository.forgotPassword(email)) {
                is ResultState.Loading -> {
                    // Bisa tambahkan progress dialog
                }
                is ResultState.Success -> {
                    showSuccessToast()
                }
                is ResultState.Error -> {
                    showErrorToast(result.statusCode, result.error)
                }
            }
        }
    }

    private fun showSuccessToast() {
        MotionToast.createColorToast(
            this,
            "Berhasil",
            "Link reset password telah dikirim ke email $userEmail. Silakan periksa email Anda.",
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