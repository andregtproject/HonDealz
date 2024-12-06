package com.capstone.project.hondealz.view.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.response.UserDataResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(private val honDealzRepository: HonDealzRepository) : ViewModel() {

    private val _updateUserDataResult = MutableStateFlow<ResultState<UserDataResponse>?>(null)
    val updateUserDataResult: StateFlow<ResultState<UserDataResponse>?> = _updateUserDataResult

    fun updateUserData(newUsername: String, newFullName: String, newEmail: String) {
        viewModelScope.launch {
            _updateUserDataResult.value = ResultState.Loading
            _updateUserDataResult.value = honDealzRepository.updateUserData(newUsername, newFullName, newEmail)
        }
    }
}