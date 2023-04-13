package com.example.picpostapi.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.picpostapi.data.APIService
import com.example.picpostapi.viewmodel.CaptureViewModel
import com.example.picpostapi.viewmodel.CaptureViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private val apiService: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(APIService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

    private val viewModel: CaptureViewModel by viewModels { CaptureViewModelFactory(apiService) }

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imageView.setImageURI(it)
                viewModel.url = it
            }
        }

        takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    binding.imageView.setImageURI(viewModel.imageUri)
                }
            }

        binding.takePhotoButton.setOnClickListener { takePhoto() }
        binding.selectPhotoButton.setOnClickListener { selectPhoto() }
        binding.sendPhotoButton.setOnClickListener { sendPhoto() }
    }

    private fun takePhoto() {
        if (hasCameraPermission()) {
            viewModel.()?.let {
                viewModel.imageUri = it
                takePicture.launch(it)
            }
        } else {
            requestCameraPermission()
        }
    }

    private fun selectPhoto() {
        if (hasReadStoragePermission()) {
            getContent.launch("image/*")
        } else {
            requestReadStoragePermission()
        }
    }

    private fun sendPhoto() {
        viewModel.imageUri?.let {
            val imagePath = viewModel.getPathFromUri(requireContext(), it)
            imagePath?.let { path ->
                val imageFile = File(path)

                viewModel.uploadImage(imageFile, { imageUrl ->
                    Toast.makeText(requireContext(), "Image URL: $imageUrl", Toast.LENGTH_LONG)
                        .show()
                }, {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    private fun hasReadStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestReadStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_STORAGE_PERMISSION
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val REQUEST_READ_STORAGE_PERMISSION = 2
    }
}
