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
import com.capstone.project.hondealz.data.response.UserDataResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _userData = MutableLiveData<ResultState<UserDataResponse>>()
    val userData: LiveData<ResultState<UserDataResponse>> = _userData

    fun fetchUserData() {
        viewModelScope.launch {
            // Log untuk debugging
            Log.d("HomeViewModel", "Fetching user data...")

            _userData.value = ResultState.Loading
            _userData.value = repository.getUserData()
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    // Tambahkan method untuk mendapatkan session
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}