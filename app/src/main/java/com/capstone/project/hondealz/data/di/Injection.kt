package com.capstone.project.hondealz.data.di

import android.content.Context
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): HonDealzRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return HonDealzRepository(apiService, userPreference)
    }
}