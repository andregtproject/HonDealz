package com.capstone.project.hondealz.view.scan.detail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.MotorResponse
import com.capstone.project.hondealz.data.response.PriceResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanDetailViewModel(
    private val repository: HonDealzRepository,
    private val context: Context
) : ViewModel() {
    private val _motorResult = MutableLiveData<ResultState<MotorResponse>>()
    val motorResult: LiveData<ResultState<MotorResponse>> = _motorResult
    private val _priceResult = MutableLiveData<ResultState<PriceResponse>>()
    val priceResult: LiveData<ResultState<PriceResponse>> = _priceResult

    fun predictMotor(imageUri: Uri) {
        viewModelScope.launch {
            _motorResult.value = ResultState.Loading

            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(imageUri)

                if (inputStream == null) {
                    _motorResult.value = ResultState.Error(0, "Gagal membaca gambar")
                    return@launch
                }

                val tempFile = File(context.cacheDir, "temp_image.jpg")
                tempFile.outputStream().use { fileOut ->
                    inputStream.copyTo(fileOut)
                }

                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)

                val result = repository.predictMotor(body)
                _motorResult.value = result

                tempFile.delete()
            } catch (e: Exception) {
                _motorResult.value = ResultState.Error(0, "Gagal memprediksi motor: ${e.message}")
            }
        }
    }

    fun predictPrice(model: String, year: Int, mileage: Int, location: String, tax: String) {
        viewModelScope.launch {
            _priceResult.value = ResultState.Loading
            try {
                val result = repository.predictPrice(model, year, mileage, location, tax)
                _priceResult.value = result
            } catch (e: Exception) {
                _priceResult.value = ResultState.Error(0, e.message ?: "Terjadi kesalahan")
            }
        }
    }

}