package com.capstone.project.hondealz.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState

class ProfileViewModel(private val honDealzRepository: HonDealzRepository) : ViewModel() {
    private val _fullname = MutableLiveData<String>()
    val fullname: LiveData<String> = _fullname

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    fun getUserData(): LiveData<ResultState<com.capstone.project.hondealz.data.response.UserDataResponse>> = liveData {
        emit(ResultState.Loading)
        emit(honDealzRepository.getUserData())
    }

    fun updateUserData(fullname: String, email: String) {
        _fullname.value = fullname
        _email.value = email
    }
}