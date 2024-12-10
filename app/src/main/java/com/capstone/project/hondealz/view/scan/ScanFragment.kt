package com.capstone.project.hondealz.view.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.getImageUri
import com.capstone.project.hondealz.data.reduceFileImage
import com.capstone.project.hondealz.data.uriToFile
import com.capstone.project.hondealz.databinding.FragmentScanBinding
import com.capstone.project.hondealz.view.scan.detail.ScanDetailActivity

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }

        return binding.root
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            try {
                // Reduce file image terlebih dahulu
                val reducedImageFile = uriToFile(uri, requireContext()).reduceFileImage()

                // Buat URI baru dari file yang sudah diresize
                val reducedImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    reducedImageFile
                )

                // Kirim URI yang sudah diresize ke ScanDetailActivity
                val intent = Intent(requireContext(), ScanDetailActivity::class.java).apply {
                    putExtra(ScanDetailActivity.EXTRA_IMAGE_URI, reducedImageUri)
                }
                startActivity(intent)

            } catch (e: Exception) {
                Log.e("UploadImageError", "Error processing image", e)
                Toast.makeText(requireContext(), "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .override(800, 800)  // Batasi ukuran gambar
                .centerCrop()  // Potong gambar agar sesuai dengan ukuran target
                .into(binding.previewImageView)
        }
    }

}