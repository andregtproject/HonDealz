package com.capstone.project.hondealz.view.history.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityHistoryDetailBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private val viewModel: HistoryDetailViewModel by viewModels { ViewModelFactory.getInstance(this) }

    companion object {
        const val EXTRA_HISTORY_ID = "extra_history_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Riwayat"

        val historyId = intent.getIntExtra(EXTRA_HISTORY_ID, -1)

        if (historyId != -1) {
            loadHistoryDetail(historyId)
        } else {
            Toast.makeText(this, "Invalid history ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadHistoryDetail(id: Int) {
        viewModel.getSpecificHistory(id).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val historyDetail = result.data

                    // Format harga dengan rupiah
                    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

                    // Tambahkan logging untuk debug
                    Log.d("HistoryDetailActivity", "Image URL: ${historyDetail.imageUrl}")
                    Log.d("HistoryDetailActivity", "Predicted Price: ${historyDetail.predictedPrice}")
                    Log.d("HistoryDetailActivity", "Min Price: ${historyDetail.minPrice}")
                    Log.d("HistoryDetailActivity", "Max Price: ${historyDetail.maxPrice}")

                    val formattedPredictedPrice = "Rp ${numberFormat.format(historyDetail.predictedPrice ?: 0)}"
                    val formattedMinPrice = "Rp ${numberFormat.format(historyDetail.minPrice ?: 0)}"
                    val formattedMaxPrice = "Rp ${numberFormat.format(historyDetail.maxPrice ?: 0)}"

                    // Load gambar motor
                    Glide.with(this)
                        .load(historyDetail.imageUrl ?: R.drawable.ic_image_placeholder)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(binding.ivMotorDetail)

                    // Set data ke view
                    binding.tvMotorNameDetail.text = historyDetail.model ?: "N/A"
                    binding.tvMotorYearDetail.text = "Tahun: ${historyDetail.year ?: "N/A"}"
                    binding.tvPredictedPriceDetail.text = formattedPredictedPrice
                    binding.tvMinPriceDetail.text = "Harga Minimum: $formattedMinPrice"
                    binding.tvMaxPriceDetail.text = "Harga Maksimum: $formattedMaxPrice"
                    binding.tvMileageDetail.text = "Kilometer: ${historyDetail.mileage ?: 0} km"
                    binding.tvLocationDetail.text = "Lokasi: ${historyDetail.location ?: "N/A"}"
                    binding.tvTaxDetail.text = "Status Pajak: ${historyDetail.tax ?: "N/A"}"

                    // Format tanggal
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        historyDetail.createdAt?.let { dateString ->
                            val cleanDateString = dateString.split("T").firstOrNull() ?: dateString
                            val parsedDate = inputFormat.parse(cleanDateString)
                            binding.tvPredictionDateDetail.text = "Tanggal Prediksi: ${parsedDate?.let { outputFormat.format(it) } ?: cleanDateString}"
                        }
                    } catch (e: Exception) {
                        binding.tvPredictionDateDetail.text = "Tanggal Prediksi: ${historyDetail.createdAt ?: "N/A"}"
                    }
                }
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}