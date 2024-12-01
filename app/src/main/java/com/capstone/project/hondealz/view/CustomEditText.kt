package com.capstone.project.hondealz.view

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.capstone.project.hondealz.R
import com.google.android.material.textfield.TextInputEditText

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                validateInput()
            }
        }
    }

    internal fun validateInput() {
        val input = text.toString()
        when (inputType) {
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_CLASS_TEXT -> {
                validateName(input)
            }

            InputType.TYPE_CLASS_TEXT -> {
                validateUsername(input)
            }

            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                validateEmail(input)
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
                validatePassword(input)
            }
        }
        // Tambahkan validasi khusus untuk konfirmasi password
        if (id == R.id.confirm_password_edit_text) {
            validateConfirmPassword(input)
        } 
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        if (confirmPassword.isEmpty()) {
            setError(context.getString(R.string.empty_confirm_password_message), null)
            return
        }

        // Ambil password dari input sebelumnya (password_edit_text)
        val passwordEditText = rootView.findViewById<CustomEditText>(R.id.password_edit_text)
        val password = passwordEditText.text.toString()

        if (confirmPassword != password) {
            setError(context.getString(R.string.confirm_password_not_match), null)
        } else {
            error = null
        }
    }

    private fun validateName(name: String) {
        if (name.isEmpty()) {
            setError(context.getString(R.string.empty_name_message), null)
        } else if (name.length < 3) {
            setError(context.getString(R.string.characters_name_message), null)
        } else {
            error = null
        }
    }

    private fun validateUsername(username: String) {
        if (username.isEmpty()) {
            setError(context.getString(R.string.empty_username_message), null)
        } else if (username.length < 3) {
            setError(context.getString(R.string.characters_username_message), null)
        } else if (!username.matches(context.getString(R.string.username_format).toRegex())) {
            setError(context.getString(R.string.username_format_message), null)
        } else {
            error = null
        }
    }

    private fun validateEmail(email: String) {
        if (email.isEmpty()) {
            setError(context.getString(R.string.empty_email_message), null)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(context.getString(R.string.email_format_message), null)
        } else {
            error = null
        }
    }

    private fun validatePassword(password: String) {
        val errorMessages = mutableListOf<String>()

        if (password.isEmpty()) {
            setError(context.getString(R.string.empty_password_message), null)
            return
        }

        if (password.length < 8) {
            errorMessages.add(context.getString(R.string.password_length_message))
        }

        if (!password.matches(Regex(".*[A-Z].*"))) {
            errorMessages.add(context.getString(R.string.password_uppercase_message))
        }

        if (!password.matches(Regex(".*[a-z].*"))) {
            errorMessages.add(context.getString(R.string.password_lowercase_message))
        }

        if (!password.matches(Regex(".*\\d.*"))) {
            errorMessages.add(context.getString(R.string.password_digit_message))
        }

        if (errorMessages.isNotEmpty()) {
            val combinedErrorMessage = errorMessages.joinToString("\n")
            setError(combinedErrorMessage, null)
        } else {
            error = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }
}