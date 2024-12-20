package com.capstone.project.hondealz.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.di.Injection
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.view.history.HistoryViewModel
import com.capstone.project.hondealz.view.home.HomeViewModel
import com.capstone.project.hondealz.view.login.LoginViewModel
import com.capstone.project.hondealz.view.main.MainViewModel
import com.capstone.project.hondealz.view.profile.ProfileViewModel
import com.capstone.project.hondealz.view.profile.editprofile.EditProfileViewModel
import com.capstone.project.hondealz.view.profile.usermanual.UserManualViewModel
import com.capstone.project.hondealz.view.register.RegisterViewModel
import com.capstone.project.hondealz.view.scan.detail.ScanDetailViewModel
import com.capstone.project.hondealz.view.scan.result.ResultViewModel

class ViewModelFactory private constructor(
    private val repository: HonDealzRepository,
    private val userPreference: UserPreference,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, userPreference) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(UserManualViewModel::class.java) -> {
                UserManualViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ScanDetailViewModel::class.java) -> {
                ScanDetailViewModel(repository, context) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    UserPreference.getInstance(context.dataStore),
                    context
                ).also { instance = it }
            }
        }
    }
}