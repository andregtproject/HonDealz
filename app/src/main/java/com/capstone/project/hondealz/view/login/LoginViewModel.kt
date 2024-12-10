package com.capstone.project.hondealz.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult


    private val _forgotPasswordResult = MutableStateFlow<ResultState<String>?>(null)
    val forgotPasswordResult: StateFlow<ResultState<String>?> = _forgotPasswordResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading
            _loginResult.value = repository.login(email, password)
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordResult.value = ResultState.Loading
            _forgotPasswordResult.value = repository.forgotPassword(email)
        }
    }
}