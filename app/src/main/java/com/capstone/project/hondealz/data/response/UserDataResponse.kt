package com.capstone.project.hondealz.data.response

import com.google.gson.annotations.SerializedName

data class UserDataResponse(

    @field:SerializedName("user")
    val user: User? = null
)

data class User(

    @field:SerializedName("photo_profile")
    val photoProfile: String? = null,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("username")
    val username: String
)
