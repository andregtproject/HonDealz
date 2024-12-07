package com.capstone.project.hondealz.view.forgotpassword

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityForgotPasswordBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                forgotPasswordViewModel.forgotPassword(email)
            } else {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            forgotPasswordViewModel.forgotPasswordResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultState.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@ForgotPasswordActivity, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is ResultState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        val errorMessage = when (result.statusCode) {
                            400 -> "Permintaan sudah dilakukan sebelumnya. Silakan coba lagi nanti."
                            404 -> "Email tidak terdaftar."
                            422 -> "Validasi error. Periksa kembali input Anda."
                            500 -> "Terjadi kesalahan server. Silakan coba lagi nanti."
                            else -> result.error
                        }
                        Toast.makeText(this@ForgotPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

    }
}