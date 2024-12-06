package com.capstone.project.hondealz.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.data.HonDealzRepository
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.api.ApiConfig
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.data.response.UserDataResponse
import com.capstone.project.hondealz.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var honDealzRepository: HonDealzRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference.getInstance(dataStore)
        honDealzRepository = HonDealzRepository(ApiConfig.getApiService(), userPreference)

        lifecycleScope.launch {
            val userModel = honDealzRepository.getSession().first()
            val userDataResult = honDealzRepository.getUserData()

            if (userDataResult is ResultState.Success) {
                val userData = userDataResult.data.user
                binding.apply {
                    etUsername.setText(userData?.username ?: "")
                    etFullName.setText(userData?.name ?: "")
                    etEmail.setText(userModel.email)
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val newUsername = binding.etUsername.text.toString()
            val newFullName = binding.etFullName.text.toString()
            val newEmail = binding.etEmail.text.toString()

        }
    }
}