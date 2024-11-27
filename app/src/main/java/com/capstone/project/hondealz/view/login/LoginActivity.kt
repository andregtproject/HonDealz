package com.capstone.project.hondealz.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.data.response.LoginResponse
import com.capstone.project.hondealz.databinding.ActivityLoginBinding
import com.capstone.project.hondealz.view.MainActivity
import com.capstone.project.hondealz.view.register.RegisterActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.emailEditText.error = "Email cannot be empty"
                false
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = "Password cannot be empty"
                false
            }
            else -> true
        }
    }

    private fun performLogin(email: String, password: String) {
        val apiService = ApiConfig.getApiService()
        apiService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        loginResponse.accessToken?.let { token ->
                            saveUserSession(email, token)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun saveUserSession(email: String, token: String) {
        lifecycleScope.launch {
            userPreference.saveSession(UserModel(email, token, true))
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}