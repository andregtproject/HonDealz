package com.capstone.project.hondealz.view.scan.result

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityResultBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.scan.detail.ScanDetailViewModel
import java.text.NumberFormat
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ScanDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_MODEL = "extra_model"
        const val EXTRA_YEAR = "extra_year"
        const val EXTRA_MILEAGE = "extra_mileage"
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_TAX_STATUS = "extra_tax_status"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val motorModel = intent.getStringExtra(EXTRA_MODEL)
        val motorYear = intent.getIntExtra(EXTRA_YEAR, 0)
        val mileage = intent.getIntExtra(EXTRA_MILEAGE, 0)
        val location = intent.getStringExtra(EXTRA_LOCATION)
        val taxStatus = intent.getStringExtra(EXTRA_TAX_STATUS)
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)

        // Setup back button
        binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

        // Display motor details
        displayMotorDetails(motorModel, motorYear, mileage, location, taxStatus, imageUri)

        if (motorModel != null && motorYear > 0 && mileage > 0 && !location.isNullOrEmpty() && !taxStatus.isNullOrEmpty()) {
            viewModel.predictPrice(
                model = motorModel,
                year = motorYear,
                mileage = mileage,
                location = location,
                tax = taxStatus
            )
        } else {
            Toast.makeText(this, "Tidak cukup data untuk prediksi harga", Toast.LENGTH_SHORT).show()
        }


        // Observe price prediction
        setupPricePredictionObserver()
        playAnimation()

        // Setup save button
        setupSaveButton()
    }

    private fun playAnimation() {
        val motorImage = ObjectAnimator.ofFloat(binding.cardMotorImage, View.ALPHA, 1f).setDuration(100)
        val motorDetail = ObjectAnimator.ofFloat(binding.cardMotorDetail, View.ALPHA, 1f).setDuration(100)
        val motorPrice = ObjectAnimator.ofFloat(binding.cardPrediction, View.ALPHA, 1f).setDuration(100)
        val saveButton = ObjectAnimator.ofFloat(binding.saveButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                motorImage,
                motorDetail,
                motorPrice,
                saveButton
            )
            startDelay = 100
        }.start()
    }

    private fun displayMotorDetails(
        motorModel: String?,
        motorYear: Int,
        mileage: Int,
        location: String?,
        taxStatus: String?,
        imageUri: Uri?
    ) {
        // Display motor image
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(binding.imageMotor)
        }

        // Set motor details
        binding.tvMotorName.text = motorModel ?: "N/A"
        binding.tvMotorYear.text = motorYear.toString()
        binding.tvMileage.text = "${NumberFormat.getNumberInstance(Locale("id", "ID")).format(mileage)} km"
        binding.tvLocation.text = location ?: "N/A"
        binding.tvTaxStatus.text = taxStatus ?: "N/A"
    }

    private fun setupPricePredictionObserver() {
        viewModel.priceResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    showLoading(false)
                    val priceResponse = result.data

                    // Format prices with Indonesian number format
                    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

                    binding.tvPredictionPrice.text = "Rp ${numberFormat.format(priceResponse.predictedPrice)}"
                    binding.tvPriceMin.text = "Rp ${numberFormat.format(priceResponse.minPrice)}"
                    binding.tvPriceMax.text = "Rp ${numberFormat.format(priceResponse.maxPrice)}"
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mendapatkan prediksi harga: ${result.error}", Toast.LENGTH_SHORT).show()
                    // Optional: Set default or fallback values
                    binding.tvPredictionPrice.text = "Rp -"
                    binding.tvPriceMin.text = "Rp -"
                    binding.tvPriceMax.text = "Rp -"
                }
                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            // Implement save functionality if needed
            Toast.makeText(this, "Fitur simpan akan segera hadir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !isLoading
    }
}