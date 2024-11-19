package com.submission.storyapp.presentation.core.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.storyapp.databinding.FragmentHomeBinding
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StoryAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView()
        handleButton()
        handleRefresh()

        // Observe state
        viewModel.state.asLiveData().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleRefresh() { binding.srfLayout.setOnRefreshListener {
        viewModel.refresh()
    }}

    private fun handleButton() { binding.fabCreate.setOnClickListener {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateFragment()
        findNavController().navigate(action)
    }}

    private fun handleState(state: HomeState) {
        binding.lpiLoading.visibility = if (state.loading) View.VISIBLE else View.GONE

        binding.srfLayout.isRefreshing = state.refresh

        if (state.error != null) {
            showToast(state.error)
        }

        if (state.stories.isNotEmpty()) {
            adapter.submitList(state.stories)
        }
    }

    private fun recyclerView() { binding.apply {
        val layoutManager = LinearLayoutManager(requireContext())
        rvStory.layoutManager = layoutManager

        adapter = StoryAdapter { story, _ ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(story)
            findNavController().navigate(action)
        }

        rvStory.adapter = adapter
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeObserver()
        _binding = null
    }
}
