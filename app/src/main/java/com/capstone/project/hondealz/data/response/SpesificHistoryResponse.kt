package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class SpesificHistoryResponse(
	@SerializedName("id") val id: Int?,
	@SerializedName("image_url") val imageUrl: String?,
	@SerializedName("model") val model: String?,
	@SerializedName("year") val year: Int?,
	@SerializedName("mileage") val mileage: Int?,
	@SerializedName("location") val location: String?,
	@SerializedName("tax") val tax: String?,
	@SerializedName("predicted_price") val predictedPrice: Int?,
	@SerializedName("min_price") val minPrice: Int?,
	@SerializedName("max_price") val maxPrice: Int?,
	@SerializedName("created_at") val createdAt: String?
)