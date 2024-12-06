package com.capstone.project.hondealz.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val honDealzRepository: HonDealzRepository) : ViewModel() {

    private val _forgotPasswordResult = MutableStateFlow<ResultState<String>?>(null)
    val forgotPasswordResult: StateFlow<ResultState<String>?> = _forgotPasswordResult

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordResult.value = ResultState.Loading
            _forgotPasswordResult.value = honDealzRepository.forgotPassword(email)
        }
    }
}