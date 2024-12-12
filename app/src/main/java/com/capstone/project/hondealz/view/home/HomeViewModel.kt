package com.capstone.project.hondealz.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.response.ListHistoryResponse
import com.capstone.project.hondealz.data.response.UserDataResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _userData = MutableLiveData<ResultState<UserDataResponse>>()
    val userData: LiveData<ResultState<UserDataResponse>> = _userData

    private val _recentHistories = MutableLiveData<ResultState<ListHistoryResponse>>()
    val recentHistories: LiveData<ResultState<ListHistoryResponse>> = _recentHistories

    fun fetchUserData() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching user data...")
            _userData.value = ResultState.Loading
            _userData.value = repository.getUserData()
        }
    }

    fun fetchRecentHistories() {
        viewModelScope.launch {
            _recentHistories.value = ResultState.Loading
            val result = repository.getHistories()

            if (result is ResultState.Success) {
                val sortedHistories = result.data.histories
                    ?.filterNotNull()
                    ?.sortedByDescending { it.createdAt }
                    ?.take(5)

                val recentHistoriesResponse = ListHistoryResponse(sortedHistories)
                _recentHistories.value = ResultState.Success(recentHistoriesResponse)
            } else {
                _recentHistories.value = result
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}