package com.capstone.project.hondealz.view.scan.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.ResultState
import com.capstone.project.hondealz.data.reduceFileImage
import com.capstone.project.hondealz.data.uriToFile
import com.capstone.project.hondealz.databinding.ActivityScanDetailBinding
import com.capstone.project.hondealz.view.ViewModelFactory

class ScanDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanDetailBinding
    private val viewModel by viewModels<ScanDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        private val MOTOR_NAMES = arrayOf(
            "All New Honda Vario 125 & 150",
            "All New Honda Vario 125 & 150 Keyless",
            "Vario 110",
            "Vario 110 ESP",
            "Vario 160",
            "Vario Techno 110",
            "Vario Techno 125 FI"
        )

        private val MOTOR_YEAR_RANGES = mapOf(
            "Vario 110" to (2006..2014),
            "Vario Techno 110" to (2009..2013),
            "Vario Techno 125 FI" to (2012..2015),
            "Vario 110 ESP" to (2015..2020),
            "All New Honda Vario 125 & 150" to (2015..2018),
            "All New Honda Vario 125 & 150 Keyless" to (2018..2022),
            "Vario 160" to (2022..2024)
        )

        private val LOCATIONS = arrayOf(
            "Jakarta",
            "Jawa Barat",
            "Banten",
            "Jawa Tengah",
            "Yogyakarta",
            "Jawa Timur",
            "Bali",
            "Lainnya"
        )

        private val TAX_STATUS = arrayOf(
            "hidup",
            "mati"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup dropdown for motor names
        setupMotorNameDropdown()
        setupMotorYearDropdown()
        setupLocationDropdown()
        setupTaxStatusDropdown()

        // Ambil URI gambar dari intent
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)

        // Tampilkan gambar di ImageView
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .override(800, 800)
                .centerCrop()
                .into(binding.imageUpload)

            viewModel.predictMotor(it)
        }

        playAnimation()
        setupObservers()
        setupAction(imageUri)
    }

    private fun setupMotorNameDropdown() {
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item_dropdown,
            MOTOR_NAMES
        )
        (binding.motorNameInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                // Update year dropdown when motor name changes
                updateYearDropdown(MOTOR_NAMES[position])
            }
        }
    }

    private fun updateYearDropdown(motorName: String) {
        val yearRangeForMotor = MOTOR_YEAR_RANGES[motorName] ?: emptyList()
        val yearAdapter = ArrayAdapter(
            this,
            R.layout.list_item_dropdown,
            yearRangeForMotor.toList()
        )
        (binding.motorYearInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(yearAdapter)
            setText("") // Mengosongkan pilihan sebelumnya
        }
    }

    private fun setupMotorYearDropdown() {
        (binding.motorYearInputLayout.editText as? AutoCompleteTextView)?.apply {
            inputType = InputType.TYPE_NULL // Disable manual text input
            setOnClickListener { showDropDown() }
        }
    }

    private fun setupLocationDropdown() {
        val locationAdapter = ArrayAdapter(
            this,
            R.layout.list_item_dropdown,
            LOCATIONS
        )
        (binding.locationInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(locationAdapter)
            inputType = InputType.TYPE_NULL // Disable manual text input
            setOnClickListener { showDropDown() }
        }
    }

    private fun setupTaxStatusDropdown() {
        val taxStatusAdapter = ArrayAdapter(
            this,
            R.layout.list_item_dropdown,
            TAX_STATUS
        )
        (binding.taxInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(taxStatusAdapter)
            inputType = InputType.TYPE_NULL // Disable manual text input
            setOnClickListener { showDropDown() }
        }
    }

    private fun playAnimation() {
        val motorImage = ObjectAnimator.ofFloat(binding.cardImage, View.ALPHA, 1f).setDuration(100)
        val motorName = ObjectAnimator.ofFloat(binding.motorNameInputLayout, View.ALPHA, 1f).setDuration(100)
        val motorYear = ObjectAnimator.ofFloat(binding.motorYearInputLayout, View.ALPHA, 1f).setDuration(100)
        val mileage = ObjectAnimator.ofFloat(binding.mileageInputLayout, View.ALPHA, 1f).setDuration(100)
        val location = ObjectAnimator.ofFloat(binding.locationInputLayout, View.ALPHA, 1f).setDuration(100)
        val taxStatus = ObjectAnimator.ofFloat(binding.taxInputLayout, View.ALPHA, 1f).setDuration(100)
        val analyzeButton = ObjectAnimator.ofFloat(binding.analyzeButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                motorImage,
                motorName,
                motorYear,
                mileage,
                location,
                taxStatus,
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
                    val motorModel = result.data.model
                    Log.d("ScanDetailActivity", "Motor Model: $motorModel")

                    setMotorNameFromPrediction(motorModel)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    setMotorNameFromPrediction(null)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    Log.e("ScanDetailActivity", "Error: ${result.error}")
                }
                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun setMotorNameFromPrediction(motorModel: String?) {
        val motorNameEditText = binding.motorNameEditText

        val selectedMotorName = if (motorModel != null) {
            MOTOR_NAMES.find { it.equals(motorModel, ignoreCase = true) }
                ?: MOTOR_NAMES.find { it.contains(motorModel, ignoreCase = true) }
                ?: MOTOR_NAMES[0]
        } else {
            MOTOR_NAMES[0]
        }

        motorNameEditText.setText(selectedMotorName)
        updateYearDropdown(selectedMotorName)

        (binding.motorNameInputLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(
                this@ScanDetailActivity,
                R.layout.list_item_dropdown,
                MOTOR_NAMES
            ))
            setOnClickListener { showDropDown() }
        }
    }

    private fun setupAction(imageUri: Uri?) {
        binding.analyzeButton.setOnClickListener {
            val motorName = binding.motorNameEditText.text.toString()
            val motorYear = binding.motorYearEditText.text.toString()
            val mileage = binding.mileageEditText.text.toString()
            val location = binding.locationEditText.text.toString()
            val taxStatus = binding.taxEditText.text.toString()

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

                location.isEmpty() -> {
                    binding.locationEditText.requestFocus()
                    return@setOnClickListener
                }

                taxStatus.isEmpty() -> {
                    binding.taxEditText.requestFocus()
                    return@setOnClickListener
                }
            }

            imageUri?.let { uri ->
                try {
                    val file = uriToFile(uri, this)
                    val reducedFile = file.reduceFileImage()

                    Glide.with(this)
                        .load(reducedFile)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .override(800, 800)
                        .into(binding.imageUpload)

                    viewModel.predictMotor(uri)
                } catch (e: Exception) {
                    Log.e("ImageProcessing", "Error processing image", e)
                    Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                    setMotorNameFromPrediction(null)
                }
            } ?: run {
                Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show()
                setMotorNameFromPrediction(null)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.analyzeButton.isEnabled = !isLoading
    }
}