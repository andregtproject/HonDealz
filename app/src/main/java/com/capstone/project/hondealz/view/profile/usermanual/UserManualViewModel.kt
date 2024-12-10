package com.capstone.project.hondealz.view.profile.usermanual

import androidx.lifecycle.ViewModel
import com.capstone.project.hondealz.data.HonDealzRepository

class UserManualViewModel(repository: HonDealzRepository) : ViewModel() {
    val pdfUrl = repository.getPdfUrl()
}