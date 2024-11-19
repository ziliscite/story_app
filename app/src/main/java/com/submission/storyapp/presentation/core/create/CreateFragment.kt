package com.submission.storyapp.presentation.core.create

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.submission.storyapp.databinding.FragmentCreateBinding
import com.submission.storyapp.utils.ResponseWrapper
import com.submission.storyapp.utils.getImageUri
import com.submission.storyapp.utils.reduceFileImage
import com.submission.storyapp.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

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

        button()
        observeInput()

        viewModel.state.asLiveData().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun button() { binding.apply {
        btnGallery.setOnClickListener {
            startGallery()
        }

        btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            observeOnUpload()
        }
    }}

    private fun observeInput() { binding.apply {
        etDescription.addTextChangedListener {
            viewModel.updateDescription(it.toString())
        }
    }}

    private fun handleState(state: CreateState) {
        binding.lpiLoading.visibility = if (state.loading) View.VISIBLE else View.GONE

        state.uri?.let {
            binding.ivStory.setImageURI(it)
        }

        state.error?.let {
            showToast(it)
        }

        state.message?.let {
            showToast(it)
            findNavController().popBackStack()
        }
    }

    private fun observeOnUpload() {
        val uri = viewModel.state.value.uri

        if (uri == null) {
            showToast("No media selected")
            return
        }

        val imageFile = uriToFile(uri, requireContext()).reduceFileImage()

        viewModel.postStory(imageFile).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseWrapper.Success -> viewModel.onSuccess(response.data)
                is ResponseWrapper.Error -> viewModel.onError(response.error)
                is ResponseWrapper.Loading -> viewModel.onLoading()
            }
        }
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
            viewModel.revertUri()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
