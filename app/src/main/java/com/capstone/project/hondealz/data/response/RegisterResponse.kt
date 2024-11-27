package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("expire")
	val expire: Int? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class User(

	@field:SerializedName("photo_profile")
	val photoProfile: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("telephone")
	val telephone: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
