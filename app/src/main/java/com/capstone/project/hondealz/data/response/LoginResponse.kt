package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("expire")
	val expire: Int? = null
)
