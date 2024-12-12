package com.capstone.project.hondealz.data

import android.util.Log
import com.capstone.project.hondealz.data.api.ApiService
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.response.ListHistoryResponse
import com.capstone.project.hondealz.data.response.LoginResponse
import com.capstone.project.hondealz.data.response.MotorResponse
import com.capstone.project.hondealz.data.response.PriceResponse
import com.capstone.project.hondealz.data.response.RegisterResponse
import com.capstone.project.hondealz.data.response.UserDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import org.json.JSONObject

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

                Log.d("Repository", "Token: $token")

                val response = apiService.getUserData(token).execute()

                Log.d("Repository", "Response Code: ${response.code()}")
                Log.d("Repository", "Response Message: ${response.message()}")
                Log.d("Repository", "Response Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    response.body()?.let { userDataResponse ->
                        ResultState.Success(userDataResponse)
                    } ?: ResultState.Error(response.code(), "User data response is empty")
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        Log.e("Repository", "Token expired with code: ${response.code()}")
                        ResultState.Error(response.code(), "Token expired. Please login again.")
                    } else {
                        ResultState.Error(
                            response.code(),
                            response.message() ?: "Failed to get user data"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching user data", e)
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun updateUserData(
        username: String,
        name: String,
        email: String
    ): ResultState<UserDataResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"
                val requestBody = mapOf(
                    "username" to username,
                    "name" to name,
                    "email" to email
                )
                val response = apiService.updateUserData(token, requestBody).execute()
                if (response.isSuccessful) {
                    response.body()?.let { userDataResponse ->
                        if (email != userModel.email) {
                            val updatedUserModel = userModel.copy(email = email)
                            saveSession(updatedUserModel)
                        }
                        ResultState.Success(userDataResponse)
                    } ?: ResultState.Error(response.code(), "User data response is empty")
                } else {
                    ResultState.Error(
                        response.code(),
                        response.message() ?: "Failed to update user data"
                    )
                }
            } catch (e: Exception) {
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

    suspend fun forgotPassword(email: String): ResultState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.forgotPassword(email).execute()
                if (response.isSuccessful) {
                    ResultState.Success(response.body()?.message ?: "Berhasil")
                } else {
                    ResultState.Error(
                        response.code(),
                        response.message() ?: "Failed to update password"
                    )
                }
            } catch (e: Exception) {
                ResultState.Error(0, e.message.toString())
            }
        }
    }

    suspend fun updatePassword(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): ResultState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"
                val requestBody = mapOf(
                    "old_password" to oldPassword,
                    "new_password" to newPassword,
                    "confirm_new_password" to confirmNewPassword
                )
                val response = apiService.updatePassword(token, requestBody).execute()
                if (response.isSuccessful) {
                    ResultState.Success(response.body()?.message ?: "Password updated successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonError = JSONObject(errorBody ?: "")
                        jsonError.optString("detail", "Failed to update password")
                    } catch (e: Exception) {
                        "Failed to update password"
                    }
                    ResultState.Error(response.code(), errorMessage)
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

    fun getPdfUrl(): String {
        return "https://drive.google.com/file/d/12vUeulo2NfY3n8VgvQhfZtAhypEFZ-Qa/view?usp=sharing"
    }

    suspend fun predictMotor(imageFile: MultipartBody.Part): ResultState<MotorResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"
                val response = apiService.predictMotor(token, imageFile).execute()

                if (response.isSuccessful) {
                    response.body()?.let { motorResponse ->
                        Log.d("Repository", "Motor predicted: ${motorResponse.model}")
                        ResultState.Success(motorResponse)
                    } ?: ResultState.Error(response.code(), "Prediksi motor gagal")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonError = JSONObject(errorBody ?: "")
                        jsonError.optString("detail", "Gagal memprediksi motor")
                    } catch (e: Exception) {
                        "Gagal memprediksi motor"
                    }

                    Log.e("Repository", "Error prediksi motor: $errorMessage")
                    ResultState.Error(response.code(), errorMessage)
                }
            } catch (e: Exception) {
                Log.e("Repository", "Network error prediksi motor", e)
                ResultState.Error(0, "Terjadi kesalahan: ${e.message ?: "Kesalahan jaringan"}")
            }
        }
    }

    suspend fun predictPrice(
        idPicture: Int,
        model: String,
        year: Int,
        mileage: Int,
        location: String,
        tax: String
    ): ResultState<PriceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"

                Log.d("Repository", "Predict Price Params: idPicture = $idPicture, model=$model, year=$year, mileage=$mileage, location=$location, tax=$tax")

                val response = apiService.predictPrice(
                    token,
                    idPicture,
                    model,
                    year,
                    mileage,
                    location,
                    tax
                ).execute()

                if (response.isSuccessful) {
                    response.body()?.let { priceResponse ->
                        ResultState.Success(priceResponse)
                    } ?: ResultState.Error(response.code(), "Respon prediksi harga kosong")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonError = JSONObject(errorBody ?: "")
                        jsonError.optString("detail", "Gagal memprediksi harga")
                    } catch (e: Exception) {
                        "Gagal memprediksi harga"
                    }

                    Log.e("Repository", "Error prediksi harga: $errorMessage")
                    ResultState.Error(response.code(), errorMessage)
                }
            } catch (e: Exception) {
                Log.e("Repository", "Network error prediksi harga", e)
                ResultState.Error(0, "Terjadi kesalahan: ${e.message ?: "Kesalahan jaringan"}")
            }
        }
    }

    suspend fun getHistories(): ResultState<ListHistoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userModel = userPreference.getSession().first()
                val token = "Bearer ${userModel.token}"
                val response = apiService.getHistories(token).execute()

                if (response.isSuccessful) {
                    response.body()?.let { listHistoryResponse ->
                        ResultState.Success(listHistoryResponse)
                    } ?: ResultState.Error(response.code(), "History response is empty")
                } else {
                    ResultState.Error(
                        response.code(),
                        response.message() ?: "Failed to get histories"
                    )
                }
            } catch (e: Exception) {
                ResultState.Error(0, e.message ?: "Network error")
            }
        }
    }

}