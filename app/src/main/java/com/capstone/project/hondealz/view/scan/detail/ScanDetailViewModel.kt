package com.capstone.project.hondealz.view.scan.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.MotorResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanDetailViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _motorResult = MutableLiveData<ResultState<MotorResponse>>()
    val motorResult: LiveData<ResultState<MotorResponse>> = _motorResult

    fun predictMotor(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val file = File(imageUri.path ?: return@launch)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val result = repository.predictMotor(body)
                _motorResult.value = result
            } catch (e: Exception) {
                _motorResult.value = ResultState.Error(0, e.message ?: "Gagal memprediksi motor")
            }
        }
    }
}