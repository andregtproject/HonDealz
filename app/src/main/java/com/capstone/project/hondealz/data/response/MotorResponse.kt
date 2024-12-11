package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class MotorResponse(

	@field:SerializedName("id_picture")
	val idPicture: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("model")
	val model: String? = null
)
