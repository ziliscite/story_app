package com.submission.storyapp.presentation.core.create

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.submission.storyapp.databinding.FragmentCreateBinding
import com.submission.storyapp.utils.ResponseWrapper
import com.submission.storyapp.utils.getImageUri
import com.submission.storyapp.utils.reduceFileImage
import com.submission.storyapp.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationClient : FusedLocationProviderClient

    private val viewModel: CreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflateToolbar()
        animate()
        button()
        observeInput()

        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel.state.asLiveData().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun inflateToolbar() { binding.apply {
        // Set up the toolbar as the ActionBar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }}

    private fun animate() { binding.apply {
        val appbar = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 1f).setDuration(160)
        val image = ObjectAnimator.ofFloat(ivStory, View.ALPHA, 1f).setDuration(160)
        val camera = ObjectAnimator.ofFloat(btnCamera, View.ALPHA, 1f).setDuration(160)
        val gallery = ObjectAnimator.ofFloat(btnGallery, View.ALPHA, 1f).setDuration(160)
        val description = ObjectAnimator.ofFloat(etDescription, View.ALPHA, 1f).setDuration(160)
        val checkbox = ObjectAnimator.ofFloat(checkboxLayout, View.ALPHA, 1f).setDuration(160)
        val upload = ObjectAnimator.ofFloat(btnUpload, View.ALPHA, 1f).setDuration(160)

        AnimatorSet().apply {
            playSequentially(
                appbar, image,
                camera, gallery,
                description,
                checkbox, upload
            )
            startDelay = 100
        }.start()
    }}

    private fun button() { binding.apply {
        btnGallery.setOnClickListener {
            startGallery()
        }

        btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            viewModel.state.value.uri?.let {
                observeOnUpload(it)
            } ?: showToast("No media selected")
        }
    }}

    private fun observeInput() { binding.apply {
        etDescription.addTextChangedListener {
            viewModel.updateDescription(it.toString())
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) getLocation()
            else viewModel.updateCheck(false)
        }
    }}

    private fun handleState(state: CreateState) {
        binding.lpiLoading.visibility = if (state.loading) View.VISIBLE else View.GONE

        if (state.uri != null) {
            binding.ivStory.setImageURI(state.uri)
        }

        if (state.error != null) {
            showToast(state.error)
            viewModel.clearError()
        }

        if (state.message != null) {
            showToast(state.message)

            val action = CreateFragmentDirections.actionCreateFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        binding.cbAgree.isChecked = state.isChecked
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        if (permission[FINE_LOCATION] == true || permission[COARSE_LOCATION] == true) {
            getLocation()
        } else {
            showToast("Access to location not granted.")
            viewModel.updateCheck(false)
        }
    }

    private fun getLocation() {
        if (!checkPermission(FINE_LOCATION) && !checkPermission(COARSE_LOCATION)) {
            requestLocationPermissionLauncher.launch(arrayOf(FINE_LOCATION, COARSE_LOCATION))
            return
        }

        try {
            locationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateLocation(it.latitude, it.longitude)
                        viewModel.updateCheck(true)
                    } ?: run {
                        showToast("Location not found")
                        viewModel.updateCheck(false)
                    }
                }
                .addOnFailureListener {
                    showToast("Location retrieval failed")
                    viewModel.updateCheck(false)
                }
        } catch (e: Exception) {
            showToast("Permission error")
            viewModel.updateCheck(false)
        }
    }

    private fun observeOnUpload(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // File conversion in a background thread to avoid blocking the main thread
                val imageFile = uriToFile(uri, requireContext()).reduceFileImage()

                // Post to ViewModel on the main thread
                withContext(Dispatchers.Main) {
                    viewModel.postStory(imageFile).observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is ResponseWrapper.Success -> {
                                viewModel.onSuccess(response.data)
                            }
                            is ResponseWrapper.Error -> {
                                viewModel.onError(response.error)
                            }
                            is ResponseWrapper.Loading -> {
                                viewModel.onLoading()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Failed to process image: ${e.message}")
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setImageForTesting(uri: Uri) {
        viewModel.updateUri(uri)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateUri(uri)
            viewModel.updatePreviousUri()
        } else {
            showToast("No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (!isSuccess) {
            showToast("Failed to take picture")
            viewModel.revertUri() // Revert uri to previous image to avoid null
        } else {
            viewModel.updatePreviousUri()
        }
    }

    private fun startCamera() {
        viewModel.updateUri(getImageUri(requireContext()))
        launcherIntentCamera.launch(viewModel.state.value.uri!!)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    }
}
