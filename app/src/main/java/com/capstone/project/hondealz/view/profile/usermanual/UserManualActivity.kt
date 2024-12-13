package com.capstone.project.hondealz.view.profile.usermanual

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project.hondealz.databinding.ActivityUserManualBinding

class UserManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManualBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.webViewe.webViewClient = WebViewClient()
        binding.webViewe.settings.javaScriptEnabled = true

        val pdfUrl = intent.getStringExtra(EXTRA_PDF_URL)
        if (pdfUrl != null) {
            binding.webViewe.loadUrl(pdfUrl)
        }
    }

    companion object {
        const val EXTRA_PDF_URL = "extra_pdf_url"
    }
}