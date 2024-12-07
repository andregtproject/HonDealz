package com.capstone.project.hondealz.view.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project.hondealz.R

class UserManualActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_manual_activity)

        val webView: WebView = findViewById(R.id.webViewe)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        val pdfUrl = intent.getStringExtra(EXTRA_PDF_URL)
        if (pdfUrl != null) {
            webView.loadUrl(pdfUrl)
        }
    }

    companion object {
        const val EXTRA_PDF_URL = "extra_pdf_url"
    }
}