package com.example.pantrychef.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pantrychef.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { recognizeFromUri(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun recognizeFromUri(uri: Uri) {
        binding.btnCapture.isEnabled = false
        binding.btnGallery.isEnabled = false
        binding.tvHint.text = "Analyzing..."

        try {
            val image = InputImage.fromFilePath(requireContext(), uri)
            IngredientRecognizer.recognizeWithSuggestions(image) { categories ->
                activity?.runOnUiThread {
                    binding.btnCapture.isEnabled = true
                    binding.btnGallery.isEnabled = true
                    binding.tvHint.text = "Point at your fridge or pantry"

                    if (categories.isEmpty()) {
                        Toast.makeText(requireContext(), "No food items detected. Try a clearer photo.", Toast.LENGTH_SHORT).show()
                    } else {
                        val sharedViewModel: SharedRecognitionViewModel by activityViewModels()
                        sharedViewModel.setCategories(categories)
                        
                        val action = CameraFragmentDirections
                            .actionCameraFragmentToIngredientConfirmFragment(
                                emptyArray()
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
            binding.btnCapture.isEnabled = true
            binding.btnGallery.isEnabled = true
            binding.tvHint.text = "Point at your fridge or pantry"
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Failed to start camera", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        binding.btnCapture.isEnabled = false
        binding.tvHint.text = "Analyzing..."

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    IngredientRecognizer.recognizeWithSuggestionsFromProxy(imageProxy) { categories ->
                        imageProxy.close()
                        
                        if (categories.isEmpty()) {
                            Toast.makeText(requireContext(), "No food items detected. Try a clearer photo.", Toast.LENGTH_SHORT).show()
                            binding.btnCapture.isEnabled = true
                            binding.tvHint.text = "Point at your fridge or pantry"
                        } else {
                            val sharedViewModel: SharedRecognitionViewModel by activityViewModels()
                            sharedViewModel.setCategories(categories)
                            
                            val action = CameraFragmentDirections
                                .actionCameraFragmentToIngredientConfirmFragment(
                                    emptyArray()
                                )
                            findNavController().navigate(action)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                    binding.btnCapture.isEnabled = true
                    binding.tvHint.text = "Point at your fridge or pantry"
                }
            }
        )
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}

