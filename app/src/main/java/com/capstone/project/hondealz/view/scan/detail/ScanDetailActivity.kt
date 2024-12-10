package com.capstone.project.hondealz.view.scan.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.databinding.ActivityScanDetailBinding
import com.capstone.project.hondealz.view.ViewModelFactory

class ScanDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanDetailBinding
    private val viewModel by viewModels<ScanDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil URI gambar dari intent
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)

        // Tampilkan gambar di ImageView
        imageUri?.let {
            binding.imageUpload.setImageURI(it)
        }

        playAnimation()
        setupObservers()
        setupAction(imageUri)
    }

    private fun playAnimation() {
        val motorImage = ObjectAnimator.ofFloat(binding.cardImage, View.ALPHA, 1f).setDuration(100)
        val motorName = ObjectAnimator.ofFloat(binding.motorNameInputLayout, View.ALPHA, 1f).setDuration(100)
        val motorYear = ObjectAnimator.ofFloat(binding.motorYearInputLayout, View.ALPHA, 1f).setDuration(100)
        val mileage = ObjectAnimator.ofFloat(binding.mileageInputLayout, View.ALPHA, 1f).setDuration(100)
        val province = ObjectAnimator.ofFloat(binding.provinceInputLayout, View.ALPHA, 1f).setDuration(100)
        val engineSize = ObjectAnimator.ofFloat(binding.engineSizeInputLayout, View.ALPHA, 1f).setDuration(100)
        val analyzeButton = ObjectAnimator.ofFloat(binding.analyzeButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                motorImage,
                motorName,
                motorYear,
                mileage,
                province,
                engineSize,
                analyzeButton
            )
            startDelay = 100
        }.start()
    }

    private fun setupObservers() {
        viewModel.motorResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    showLoading(false)
                    val motorModel = result.data.model ?: "Motor Tidak Dikenali"
                    binding.motorNameEditText.setText(motorModel)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun setupAction(imageUri: Uri?) {
        binding.analyzeButton.setOnClickListener {
            // Ambil nilai dari input
            val motorName = binding.motorNameEditText.text.toString()
            val motorYear = binding.motorYearEditText.text.toString()
            val mileage = binding.mileageEditText.text.toString()
            val province = binding.provinceEditText.text.toString()
            val engineSize = binding.engineSizeEditText.text.toString()

            // Validasi input
            when {
                motorName.isEmpty() -> {
                    binding.motorNameEditText.requestFocus()
                    return@setOnClickListener
                }

                motorYear.isEmpty() -> {
                    binding.motorYearInputLayout.requestFocus()
                    return@setOnClickListener
                }

                mileage.isEmpty() -> {
                    binding.mileageEditText.requestFocus()
                    return@setOnClickListener
                }

                province.isEmpty() -> {
                    binding.provinceEditText.requestFocus()
                    return@setOnClickListener
                }

                engineSize.isEmpty() -> {
                    binding.engineSizeEditText.requestFocus()
                    return@setOnClickListener
                }
            }

            // Proses prediksi motor jika semua input terisi
            imageUri?.let {
                viewModel.predictMotor(it)
            } ?: run {
                Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.analyzeButton.isEnabled = !isLoading
    }
}