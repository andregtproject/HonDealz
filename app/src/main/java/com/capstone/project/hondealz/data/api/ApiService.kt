package com.capstone.project.hondealz.data.api

import com.capstone.project.hondealz.data.response.LoginResponse
import com.capstone.project.hondealz.data.response.RegisterResponse
import com.capstone.project.hondealz.data.response.UserDataResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirmPassword: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("user")
    fun getUserData(
        @Header("Authorization") token: String
    ): Call<UserDataResponse>

    @FormUrlEncoded
    @PUT("user")
    fun updateUserData(
        @Header("Authorization") token: String,
        @FieldMap requestBody: Map<String, String>
    ): Call<UserDataResponse>
}