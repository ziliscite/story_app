package com.submission.storyapp.presentation.core.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.storyapp.R
import com.submission.storyapp.databinding.FragmentHomeBinding
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.presentation.core.maps.MapsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StoryAdapter
    private lateinit var loadingAdapter: LoadingAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleButton()
        handleRefresh()
        inflateActionBar()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stories.collect { stories ->
                    adapter.submitData(stories)
                }
            }
        }
    }

    private fun inflateActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            alertDialog().show()
        }

        binding.toolbar.findViewById<ImageButton>(R.id.mapsButton).setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun alertDialog(): AlertDialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.custom_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

        positiveButton.setOnClickListener {
            viewModel.logout()
            requireActivity().finish() // Close the app
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    private fun handleRefresh() { binding.srfLayout.setOnRefreshListener {
        adapter.refresh()
    }}

    private fun handleButton() { binding.fabCreate.setOnClickListener {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateFragment()
        findNavController().navigate(action)
        viewModel.setScrollToTop(true)
    }}

    private fun recyclerView() { binding.apply {
        val layoutManager = LinearLayoutManager(requireContext())
        rvStory.layoutManager = layoutManager

        adapter = StoryAdapter { story, _ ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(story)
            findNavController().navigate(action)
        }

        loadingAdapter = LoadingAdapter{
            adapter.retry()
        }

        rvStory.adapter = adapter.withLoadStateFooter(
            footer = loadingAdapter
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }.collect { loadState ->
                    when (val refreshState = loadState.refresh) {
                        is LoadState.Loading -> {
                            lpiLoading.visibility = View.VISIBLE
                        }
                        is LoadState.Error -> {
                            lpiLoading.visibility = View.GONE
                            srfLayout.isRefreshing = false
                            val errorTextView = when(val error = refreshState.error) {
                                is IOException -> "Network Error: Check your connection"
                                is HttpException -> "Server Error: ${error.code()}"
                                else -> "Unknown Error: ${error.message}"
                            }
                            showToast(errorTextView)
                        }
                        is LoadState.NotLoading -> {
                            lpiLoading.visibility = View.GONE
                            srfLayout.isRefreshing = false

                            if (viewModel.scroll.value) {
                                delay(100)
                                binding.rvStory.post {
                                    binding.rvStory.scrollToPosition(0)
                                    (binding.rvStory.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
                                }
                                viewModel.setScrollToTop(false)
                            }
                        }
                    }

                    when (val appendState = loadState.append) {
                        is LoadState.Loading -> {
                            pbLoading.visibility = View.VISIBLE
                        }
                        is LoadState.Error -> {
                            pbLoading.visibility = View.GONE
                            val errorMessage = when(val error = appendState.error) {
                                is IOException -> "Network Error: Check your connection"
                                is HttpException -> "Server Error: ${error.code()}"
                                else -> "Unknown Error: ${error.message}"
                            }
                            showToast(errorMessage)
                        }
                        is LoadState.NotLoading -> {
                            pbLoading.visibility = View.GONE
                            if (appendState.endOfPaginationReached) {
                                showToast("No more stories to load")
                            }
                        }
                    }
                }
            }
        }
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
