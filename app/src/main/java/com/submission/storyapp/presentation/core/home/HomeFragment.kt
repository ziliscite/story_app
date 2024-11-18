package com.submission.storyapp.presentation.core.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.storyapp.databinding.FragmentHomeBinding
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

        // Observe state
        viewModel.state.asLiveData().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: HomeState) {
        binding.lpiLoading.visibility = if (state.loading) View.VISIBLE else View.GONE

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

            // Shared element transition -- failed, but tried anyway
//            val extras = FragmentNavigator.Extras.Builder()
//                .addSharedElements(
//                    mapOf(
//                        views.ivStory to views.ivStory.transitionName,
//                        views.tvTitle to views.tvTitle.transitionName,
//                        views.tvDate to views.tvDate.transitionName,
//                        views.tvDescription to views.tvDescription.transitionName
//                    )
//                ).build()

            findNavController().navigate(action)
        }

        rvStory.adapter = adapter
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
