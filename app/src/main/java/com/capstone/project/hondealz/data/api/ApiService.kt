package com.capstone.project.hondealz.data.api

import com.capstone.project.hondealz.data.response.ForgotPasswordResponse
import com.capstone.project.hondealz.data.response.LoginResponse
import com.capstone.project.hondealz.data.response.MotorResponse
import com.capstone.project.hondealz.data.response.RegisterResponse
import com.capstone.project.hondealz.data.response.UserDataResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

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

    @FormUrlEncoded
    @POST("user/forgot-password")
    fun forgotPassword(@Field("email") email: String): Call<ForgotPasswordResponse>

    @Multipart
    @POST("ai-models/motor-image-recognition")
    fun predictMotor(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<MotorResponse>
}