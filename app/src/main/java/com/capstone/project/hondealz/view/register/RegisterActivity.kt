package com.capstone.project.hondealz.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityRegisterBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.login.LoginActivity
import com.capstone.project.hondealz.view.main.MainActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginTextView()
        setupView()
        setupAction()
        observeViewModel()
        playAnimation()
    }

    private fun setupLoginTextView() {
        val loginText = getString(R.string.text_navigate_login)
        val spannableString = SpannableString(loginText)

        val firstPartColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val secondPartColor = ContextCompat.getColor(this, R.color.colorAccent)

        spannableString.setSpan(
            ForegroundColorSpan(firstPartColor),
            0,
            loginText.indexOf(getString(R.string.login)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(secondPartColor),
            loginText.indexOf(getString(R.string.login)),
            loginText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginTextView.text = spannableString
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        val appImage = ObjectAnimator.ofFloat(binding.appImage, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val subtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameInputLayout, View.ALPHA, 1f).setDuration(100)
        val usernameEditTextLayout =
            ObjectAnimator.ofFloat(binding.usernameInputLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailInputLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordInputLayout, View.ALPHA, 1f).setDuration(100)
        val confirmPasswordEditTextLayout =
            ObjectAnimator.ofFloat(binding.confirmPasswordInputLayout, View.ALPHA, 1f)
                .setDuration(100)
        val register =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                appImage,
                title,
                subtitle,
                nameEditTextLayout,
                usernameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                confirmPasswordEditTextLayout,
                register,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameEditText.requestFocus()
                    return@setOnClickListener
                }

                username.isEmpty() -> {
                    binding.usernameEditText.requestFocus()
                    return@setOnClickListener
                }

                email.isEmpty() -> {
                    binding.emailEditText.requestFocus()
                    return@setOnClickListener
                }

                password.isEmpty() -> {
                    binding.passwordEditText.requestFocus()
                    return@setOnClickListener
                }

                confirmPassword.isEmpty() -> {
                    binding.confirmPasswordEditText.requestFocus()
                    return@setOnClickListener
                }
            }

            if (binding.nameEditText.error == null &&
                binding.usernameEditText.error == null &&
                binding.emailEditText.error == null &&
                binding.passwordEditText.error == null &&
                binding.confirmPasswordEditText.error == null
            ) {
                showLoading(true)
                viewModel.register(name, username, email, password, confirmPassword)
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    showSuccessToast()
                    navigateToMainActivity()
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showErrorToast(result.statusCode, result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
    }

    private fun showSuccessToast() {
        MotionToast.createColorToast(
            this,
            getString(R.string.toast_success_register),
            getString(R.string.toast_success_message_register),
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    private fun showErrorToast(responseCode: Int, message: String) {
        val errorMessage = when (responseCode) {
            400 -> getString(R.string.user_already_registered)
            415 -> getString(R.string.file_not_supported)
            422 -> getString(R.string.validation_error)
            500 -> getString(R.string.internal_server_error)
            else -> getString(R.string.unknown_error, message)
        }

        MotionToast.createColorToast(
            this,
            getString(R.string.toast_fail_register),
            errorMessage,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}