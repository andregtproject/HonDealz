package com.capstone.project.hondealz.view.scan.result

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityResultBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_MODEL = "extra_model"
        const val EXTRA_YEAR = "extra_year"
        const val EXTRA_MILEAGE = "extra_mileage"
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_TAX_STATUS = "extra_tax_status"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_ID_PICTURE = "extra_id_picture"
        const val EXTRA_HISTORY_ID = "extra_history_id"
        const val EXTRA_BACKUP_IMAGE_URL = "extra_backup_image_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val historyId = intent.getIntExtra(EXTRA_HISTORY_ID, 0)
        val fromHistory = historyId > 0

        binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

        if (fromHistory) {
            viewModel.getSpecificHistory(historyId)
            setupSpecificHistoryObserver()
        } else {
            val idPicture = intent.getIntExtra(EXTRA_ID_PICTURE, 0)
            val motorModel = intent.getStringExtra(EXTRA_MODEL)
            val motorYear = intent.getIntExtra(EXTRA_YEAR, 0)
            val mileage = intent.getIntExtra(EXTRA_MILEAGE, 0)
            val location = intent.getStringExtra(EXTRA_LOCATION)
            val taxStatus = intent.getStringExtra(EXTRA_TAX_STATUS)
            val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)

            displayMotorDetails(motorModel, motorYear, mileage, location, taxStatus,
                imageUri.toString()
            )

            if (motorModel != null && motorYear > 0 && mileage > 0 && !location.isNullOrEmpty() && !taxStatus.isNullOrEmpty()) {
                viewModel.predictPrice(
                    idPicture = idPicture,
                    model = motorModel,
                    year = motorYear,
                    mileage = mileage,
                    location = location,
                    tax = taxStatus
                )
            } else {
                Toast.makeText(this, "Tidak cukup data untuk prediksi harga", Toast.LENGTH_SHORT).show()
            }

            setupPricePredictionObserver()
        }

        playAnimation()
    }

    private fun setupSpecificHistoryObserver() {
        viewModel.specificHistoryResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    showLoading(false)
                    val historyDetail = result.data

                    Log.d("ResultActivity", "History Detail: $historyDetail")
                    Log.d("ResultActivity", "Image URL: ${historyDetail.imageUrl}")

                    val imageUrl = historyDetail.imageUrl.takeUnless { it.isNullOrEmpty() }
                        ?: intent.getStringExtra(EXTRA_BACKUP_IMAGE_URL)
                        ?: ""

                    if (imageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(binding.imageMotor)
                    } else {
                        binding.imageMotor.setImageResource(R.drawable.ic_image_placeholder)
                    }

                    displayMotorDetails(
                        motorModel = historyDetail.model,
                        motorYear = historyDetail.year ?: 0,
                        mileage = historyDetail.mileage ?: 0,
                        location = historyDetail.location,
                        taxStatus = historyDetail.tax,
                        imageUri = null
                    )

                    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                    binding.tvPredictionPrice.text = "Rp ${numberFormat.format(historyDetail.predictedPrice ?: 0)}"
                    binding.tvPriceMin.text = "Rp ${numberFormat.format(historyDetail.minPrice ?: 0)}"
                    binding.tvPriceMax.text = "Rp ${numberFormat.format(historyDetail.maxPrice ?: 0)}"
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mendapatkan detail riwayat: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }
    private fun playAnimation() {
        val motorImage = ObjectAnimator.ofFloat(binding.cardMotorImage, View.ALPHA, 1f).setDuration(100)
        val motorDetail = ObjectAnimator.ofFloat(binding.cardMotorDetail, View.ALPHA, 1f).setDuration(100)
        val motorPrice = ObjectAnimator.ofFloat(binding.cardPrediction, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                motorImage,
                motorDetail,
                motorPrice
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
        imageUri: String?
    ) {
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(binding.imageMotor)
        }

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

                    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

                    binding.tvPredictionPrice.text = "Rp ${numberFormat.format(priceResponse.predictedPrice)}"
                    binding.tvPriceMin.text = "Rp ${numberFormat.format(priceResponse.minPrice)}"
                    binding.tvPriceMax.text = "Rp ${numberFormat.format(priceResponse.maxPrice)}"
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mendapatkan prediksi harga: ${result.error}", Toast.LENGTH_SHORT).show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}