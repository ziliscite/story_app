package com.submission.storyapp.presentation.core.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.submission.storyapp.databinding.FragmentDetailBinding
import com.submission.storyapp.domain.models.Story

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Retrieve the story data passed from the previous fragment
        val story = DetailFragmentArgs.fromBundle(requireArguments()).story
        bindElements(story)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflateToolbar()
        animate()
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
        val title = ObjectAnimator.ofFloat(tvTitle, View.ALPHA, 1f).setDuration(160)
        val date = ObjectAnimator.ofFloat(tvDate, View.ALPHA, 1f).setDuration(160)
        val description = ObjectAnimator.ofFloat(tvDescription, View.ALPHA, 1f).setDuration(160)

        AnimatorSet().apply {
            playSequentially(
                appbar, image, title,
                date, description,
            )
            startDelay = 100
        }.start()
    }}

    private fun bindElements(story: Story) {
        Glide.with(this).load(story.photoUrl).into(binding.ivStory)
        binding.tvTitle.text = story.name
        binding.tvDate.text = story.createdAt
        binding.tvDescription.text = story.description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
