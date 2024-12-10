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
import java.time.Year

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
        when (id) {
            // Registration and Login validations
            R.id.name_edit_text -> validateName(input)
            R.id.username_edit_text -> validateUsername(input)
            R.id.email_edit_text -> validateEmail(input)
            R.id.password_edit_text -> validatePassword(input)
            R.id.confirm_password_edit_text -> validateConfirmPassword(input)

            // Scan Detail validations
            R.id.motor_name_edit_text -> validateMotorName(input)
            R.id.motor_year_edit_text -> validateMotorYear(input)
            R.id.mileage_edit_text -> validateMileage(input)
            R.id.province_edit_text -> validateProvince(input)
            R.id.engine_size_edit_text -> validateEngineSize(input)
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

    private fun validateMotorName(motorName: String) {
        if (motorName.isEmpty()) {
            setError(context.getString(R.string.empty_motor_name_message), null)
        } else if (motorName.length < 2) {
            setError(context.getString(R.string.motor_name_length_message), null)
        } else {
            error = null
        }
    }

    private fun validateMotorYear(yearStr: String) {
        if (yearStr.isEmpty()) {
            setError(context.getString(R.string.empty_motor_year_message), null)
            return
        }
    }

    private fun validateMileage(mileageStr: String) {
        if (mileageStr.isEmpty()) {
            setError(context.getString(R.string.empty_mileage_message), null)
            return
        }

        try {
            val mileage = mileageStr.toInt()
            when {
                mileage < 0 -> {
                    setError(context.getString(R.string.negative_mileage_message), null)
                }
                mileage > 1000000 -> {
                    setError(context.getString(R.string.excessive_mileage_message), null)
                }
                else -> {
                    error = null
                }
            }
        } catch (e: NumberFormatException) {
            setError(context.getString(R.string.invalid_mileage_message), null)
        }
    }

    private fun validateProvince(province: String) {
        if (province.isEmpty()) {
            setError(context.getString(R.string.empty_province_message), null)
        } else if (province.length < 3) {
            setError(context.getString(R.string.province_length_message), null)
        } else {
            error = null
        }
    }

    private fun validateEngineSize(engineSizeStr: String) {
        if (engineSizeStr.isEmpty()) {
            setError(context.getString(R.string.empty_engine_size_message), null)
            return
        }

        try {
            val engineSize = engineSizeStr.toInt()
            when {
                engineSize < 50 -> {
                    setError(context.getString(R.string.small_engine_size_message), null)
                }
                engineSize > 2000 -> {
                    setError(context.getString(R.string.large_engine_size_message), null)
                }
                else -> {
                    error = null
                }
            }
        } catch (e: NumberFormatException) {
            setError(context.getString(R.string.invalid_engine_size_message), null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }
}