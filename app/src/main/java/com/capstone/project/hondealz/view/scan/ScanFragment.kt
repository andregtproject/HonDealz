package com.capstone.project.hondealz.view.scan

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSuccessToast(getString(R.string.permission_request_granted))
            } else {
                showErrorToast(getString(R.string.permission_request_denied))
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

        playAnimation()

        return binding.root
    }

    private fun playAnimation() {
        val motorImage = ObjectAnimator.ofFloat(binding.cardImage, View.ALPHA, 1f).setDuration(100)
        val cameraGalleryLayout =
            ObjectAnimator.ofFloat(binding.cameraGalleryLayout, View.ALPHA, 1f).setDuration(100)
        val uploadButton =
            ObjectAnimator.ofFloat(binding.uploadButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                motorImage,
                cameraGalleryLayout,
                uploadButton
            )
            startDelay = 100
        }.start()
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            try {
                val reducedImageFile = uriToFile(uri, requireContext()).reduceFileImage()

                val reducedImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    reducedImageFile
                )

                val intent = Intent(requireContext(), ScanDetailActivity::class.java).apply {
                    putExtra(ScanDetailActivity.EXTRA_IMAGE_URI, reducedImageUri)
                }
                startActivity(intent)

            } catch (e: Exception) {
                Log.e("UploadImageError", "Error processing image", e)
                showErrorToast(getString(R.string.failed_process_image))
            }
        } ?: run {
            showErrorToast(getString(R.string.choose_image_first))
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
        Log.d("Camera", "Camera URI: $currentImageUri")
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            Log.d("Camera", "Photo capture successful")
            Log.d("Camera", "Current Image URI: $currentImageUri")
            showImage()
        } else {
            Log.d("Camera", "Photo capture failed")
            showErrorToast(getString(R.string.failed_to_capture_photo))
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .override(800, 800)
                .centerCrop()
                .into(binding.previewImageView)
        }
    }

    private fun showSuccessToast(message: String) {
        MotionToast.createColorToast(
            requireActivity(),
            getString(R.string.success),
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    private fun showErrorToast(message: String) {
        MotionToast.createColorToast(
            requireActivity(),
            getString(R.string.fail),
            message,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }
}