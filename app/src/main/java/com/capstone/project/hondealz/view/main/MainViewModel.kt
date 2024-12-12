package com.capstone.project.hondealz.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.pref.UserModel
import com.capstone.project.hondealz.data.pref.UserPreference

class MainViewModel(
    private val repository: HonDealzRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getUserPreference(): UserPreference {
        return userPreference
    }
}