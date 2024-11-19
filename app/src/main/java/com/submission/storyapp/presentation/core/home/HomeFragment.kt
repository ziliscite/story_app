package com.submission.storyapp.presentation.core.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.storyapp.R
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
        handleButton()
        handleRefresh()
        inflateActionBar()

        // Observe state
        viewModel.state.asLiveData().observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun inflateActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            alertDialog().show()
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
