package com.capstone.project.hondealz.view.profile.editprofile

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

    private val _userProfileResult = MutableStateFlow<ResultState<UserDataResponse>?>(null)
    val userProfileResult: StateFlow<ResultState<UserDataResponse>?> = _userProfileResult

    private val _updatePasswordResult = MutableStateFlow<ResultState<String>?>(null)
    val updatePasswordResult: StateFlow<ResultState<String>?> = _updatePasswordResult

    fun updatePassword(oldPassword: String, newPassword: String, confirmNewPassword: String) {
        viewModelScope.launch {
            _updatePasswordResult.value = ResultState.Loading
            _updatePasswordResult.value =
                honDealzRepository.updatePassword(oldPassword, newPassword, confirmNewPassword)
        }
    }

    fun updateUserData(newUsername: String, newFullName: String, newEmail: String) {
        viewModelScope.launch {
            _updateUserDataResult.value = ResultState.Loading
            _updateUserDataResult.value =
                honDealzRepository.updateUserData(newUsername, newFullName, newEmail)
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            _userProfileResult.value = ResultState.Loading
            _userProfileResult.value = honDealzRepository.getUserData()
        }
    }

}