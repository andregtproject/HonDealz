package com.capstone.project.hondealz.view.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState

class ProfileViewModel(private val honDealzRepository: HonDealzRepository) : ViewModel() {
    fun getUserData(): LiveData<ResultState<com.capstone.project.hondealz.data.response.UserDataResponse>> = liveData {
        emit(ResultState.Loading)
        emit(honDealzRepository.getUserData())
    }
}