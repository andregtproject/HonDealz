package com.capstone.project.hondealz.data

import android.util.Log
import com.capstone.project.hondealz.data.api.ApiService
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.response.LoginResponse
import com.capstone.project.hondealz.data.response.RegisterResponse
import com.capstone.project.hondealz.data.response.UserDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class HonDealzRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun login(email: String, password: String): ResultState<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(email, password).execute()
                Log.d("HonDealzRepository", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        loginResponse.accessToken?.let { token ->
                            val userModel = UserModel(email, token, true)
                            saveSession(userModel)
                            ResultState.Success(loginResponse)
                        } ?: ResultState.Error(response.code(), "No access token received")
                    } ?: ResultState.Error(response.code(), "Login response is empty")
                } else {
                    Log.e("HonDealzRepository", "Error Body: ${response.errorBody()?.string()}")
                    ResultState.Error(response.code(), response.message() ?: "Login failed")
                }
            } catch (e: Exception) {
                Log.e("HonDealzRepository", "Network error", e)
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

    suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ResultState<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    apiService.register(name, username, email, password, confirmPassword).execute()
                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        registerResponse.accessToken?.let { token ->
                            val userModel = UserModel(email, token, true)
                            saveSession(userModel)
                            ResultState.Success(registerResponse)
                        } ?: ResultState.Error(response.code(), "No access token received")
                    } ?: ResultState.Error(response.code(), "Register response is empty")
                } else {
                    ResultState.Error(response.code(), response.message() ?: "Registration failed")
                }
            } catch (e: Exception) {
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

    suspend fun getUserData(): ResultState<UserDataResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"

                val response = apiService.getUserData(token).execute()
                if (response.isSuccessful) {
                    response.body()?.let { userDataResponse ->
                        ResultState.Success(userDataResponse)
                    } ?: ResultState.Error(response.code(), "User data response is empty")
                } else {
                    ResultState.Error(
                        response.code(),
                        response.message() ?: "Failed to get user data"
                    )
                }
            } catch (e: Exception) {
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
}