package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class SpesificHistoryResponse(

	@field:SerializedName("predicted_price")
	val predictedPrice: Int? = null,

	@field:SerializedName("max_price")
	val maxPrice: Int? = null,

	@field:SerializedName("min_price")
	val minPrice: Int? = null,

	@field:SerializedName("year")
	val year: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("model")
	val model: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("tax")
	val tax: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("mileage")
	val mileage: Int? = null
)
