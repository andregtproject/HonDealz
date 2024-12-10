package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(

	@field:SerializedName("message")
	val message: String? = null
)

// TODO : menambahkan loading pada proses forgot password.
// TODO : Rename Panduan to "Panduan User" / "User Guide"
