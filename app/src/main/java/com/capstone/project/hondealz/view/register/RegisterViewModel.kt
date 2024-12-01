package com.capstone.project.hondealz.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: HonDealzRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> = _registerResult

    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _registerResult.value = ResultState.Loading
            _registerResult.value = repository.register(name, username, email, password, confirmPassword)
        }
    }
}
