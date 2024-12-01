package com.capstone.project.hondealz.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.pref.UserModel

class MainViewModel(private val repository: HonDealzRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}