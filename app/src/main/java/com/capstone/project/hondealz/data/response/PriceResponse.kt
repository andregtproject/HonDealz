package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class PriceResponse(

	@field:SerializedName("predicted_price")
	val predictedPrice: Int? = null,

	@field:SerializedName("max_price")
	val maxPrice: Int? = null,

	@field:SerializedName("min_price")
	val minPrice: Int? = null
)
