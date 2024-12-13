package com.capstone.project.hondealz.view.login

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
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityLoginBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.main.MainActivity
import com.capstone.project.hondealz.view.register.RegisterActivity
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var userEmail: String? = null

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRegisterTextView()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupRegisterTextView() {
        val regisText = getString(R.string.text_navigate_register)
        val spannableString = SpannableString(regisText)

        val firstPartColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val secondPartColor = ContextCompat.getColor(this, R.color.colorAccent)

        spannableString.setSpan(
            ForegroundColorSpan(firstPartColor),
            0,
            regisText.indexOf(getString(R.string.register)),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(secondPartColor),
            regisText.indexOf(getString(R.string.register)),
            regisText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.registerTextView.text = spannableString
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
        val message = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailInputLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordInputLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val register =
            ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                appImage,
                title,
                message,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                register
            )
            startDelay = 100
        }.start()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                email.isEmpty() -> {
                    binding.emailEditText.requestFocus()
                    return@setOnClickListener
                }

                password.isEmpty() -> {
                    binding.passwordEditText.requestFocus()
                    return@setOnClickListener
                }
            }

            if (binding.emailEditText.error == null && binding.passwordEditText.error == null) {
                showLoading(true)
                viewModel.login(email, password)
            }
        }

        binding.forgotText.setOnClickListener {
            showForgotPasswordConfirmationDialog()
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            viewModel.forgotPasswordResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        binding.forgotText.isEnabled = false
                    }

                    is ResultState.Success -> {
                        showSuccessToast(getString(R.string.reset_password_link_sent, userEmail))
                        binding.forgotText.isEnabled = true
                    }

                    is ResultState.Error -> {
                        showErrorToast(result.statusCode, result.error)
                        binding.forgotText.isEnabled = true
                    }

                    null -> {}
                }
            }
        }

        viewModel.loginResult.observe(this) { result ->
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

    private fun showForgotPasswordConfirmationDialog() {
        userEmail = binding.emailEditText.text.toString().trim()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_password))
            .setMessage(getString(R.string.confirmation_message_reset_password, userEmail))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                userEmail?.let { email ->
                    performForgotPassword(email)
                }
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun performForgotPassword(email: String) {
        viewModel.forgotPassword(email)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
    }

    private fun showSuccessToast(message: String? = null) {
        val toastMessage = message ?: getString(R.string.toast_success_message_login)
        val toastTitle = if (message != null)
            getString(R.string.reset_password)
        else
            getString(R.string.toast_success_login)

        MotionToast.createColorToast(
            this,
            toastTitle,
            toastMessage,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    private fun showErrorToast(responseCode: Int, message: String) {
        val errorMessage = when (responseCode) {
            401 -> getString(R.string.unauthorized)
            422 -> getString(R.string.validation_error)
            500 -> getString(R.string.internal_server_error)
            else -> getString(R.string.unknown_error, message)
        }

        MotionToast.createColorToast(
            this,
            getString(R.string.toast_fail_login),
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