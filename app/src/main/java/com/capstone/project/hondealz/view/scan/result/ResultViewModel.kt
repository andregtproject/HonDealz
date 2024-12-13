package com.capstone.project.hondealz.view.scan.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.PriceResponse
import com.capstone.project.hondealz.data.response.SpesificHistoryResponse
import kotlinx.coroutines.launch

class ResultViewModel(
    private val repository: HonDealzRepository
) : ViewModel() {
    private val _specificHistoryResult = MutableLiveData<ResultState<SpesificHistoryResponse>>()
    val specificHistoryResult: LiveData<ResultState<SpesificHistoryResponse>> = _specificHistoryResult

    private val _priceResult = MutableLiveData<ResultState<PriceResponse>>()
    val priceResult: LiveData<ResultState<PriceResponse>> = _priceResult

    fun getSpecificHistory(id: Int) {
        viewModelScope.launch {
            _specificHistoryResult.value = ResultState.Loading
            try {
                val result = repository.getSpecificHistory(id)
                _specificHistoryResult.value = result
            } catch (e: Exception) {
                _specificHistoryResult.value = ResultState.Error(0, e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun predictPrice(
        idPicture: Int,
        model: String,
        year: Int,
        mileage: Int,
        location: String,
        tax: String
    ) {
        viewModelScope.launch {
            _priceResult.value = ResultState.Loading
            try {
                val result = repository.predictPrice(
                    idPicture,
                    model,
                    year,
                    mileage,
                    location,
                    tax
                )
                _priceResult.value = result
            } catch (e: Exception) {
                _priceResult.value = ResultState.Error(0, e.message ?: "Terjadi kesalahan")
            }
        }
    }
}