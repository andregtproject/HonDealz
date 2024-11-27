package com.capstone.project.hondealz.view.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.data.response.RegisterResponse
import com.capstone.project.hondealz.databinding.ActivityRegisterBinding
import com.capstone.project.hondealz.view.MainActivity
import com.capstone.project.hondealz.view.login.LoginActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val telephone = binding.telephoneEditText.text.toString()

            if (validateInput(name, username, email, password, telephone)) {
                performRegistration(name, username, email, password, telephone)
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(
        name: String,
        username: String,
        email: String,
        password: String,
        telephone: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                binding.nameEditText.error = "Name cannot be empty"
                false
            }
            username.isEmpty() -> {
                binding.usernameEditText.error = "Username cannot be empty"
                false
            }
            email.isEmpty() -> {
                binding.emailEditText.error = "Email cannot be empty"
                false
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = "Password cannot be empty"
                false
            }
            telephone.isEmpty() -> {
                binding.telephoneEditText.error = "Telephone cannot be empty"
                false
            }
            else -> true
        }
    }

    private fun performRegistration(
        name: String,
        username: String,
        email: String,
        password: String,
        telephone: String
    ) {
        val apiService = ApiConfig.getApiService()
        apiService.register(name, username, email, password, telephone)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { registerResponse ->
                            registerResponse.accessToken?.let { token ->
                                saveUserSession(email, token)
                            }
                        }
                    } else {
                        val errorMessage = when (response.code()) {
                            400 -> "User with this data is already registered"
                            else -> "Registration failed: ${response.message()}"
                        }
                        Toast.makeText(
                            this@RegisterActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
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