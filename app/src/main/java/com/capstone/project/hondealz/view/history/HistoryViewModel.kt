package com.capstone.project.hondealz.view.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.ListHistoryResponse
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _histories = MutableLiveData<ResultState<ListHistoryResponse>>()
    val histories: LiveData<ResultState<ListHistoryResponse>> = _histories

    private var originalHistories: ListHistoryResponse? = null

    init {
        fetchHistories()
    }

    fun fetchHistories() {
        _histories.value = ResultState.Loading
        viewModelScope.launch {
            val result = repository.getHistories()
            _histories.value = result

            // Store original list for searching
            if (result is ResultState.Success) {
                originalHistories = result.data
            }
        }
    }

    fun searchHistories(query: String) {
        val currentHistories = originalHistories ?: return

        if (query.isEmpty()) {
            _histories.value = ResultState.Success(currentHistories)
            return
        }

        val filteredHistories = currentHistories.histories?.filter { history ->
            history?.model?.contains(query, true) == true ||
                    history?.year.toString().contains(query, true) ||
                    history?.location?.contains(query, true) == true
        }

        val filteredResponse = ListHistoryResponse(filteredHistories)
        _histories.value = ResultState.Success(filteredResponse)
    }
}