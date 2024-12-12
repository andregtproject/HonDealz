package com.capstone.project.hondealz.view.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.SpesificHistoryResponse
import kotlinx.coroutines.launch

class HistoryDetailViewModel(private val repository: HonDealzRepository) : ViewModel() {

    fun getSpecificHistory(id: Int): LiveData<ResultState<SpesificHistoryResponse>> {
        val result = MutableLiveData<ResultState<SpesificHistoryResponse>>()

        // Set loading state awal
        result.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getSpecificHistory(id)
                result.value = response
            } catch (e: Exception) {
                result.value = ResultState.Error(0, e.message ?: "Terjadi kesalahan")
            }
        }

        return result
    }
}