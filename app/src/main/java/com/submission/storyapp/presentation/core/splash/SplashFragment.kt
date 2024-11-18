package com.submission.storyapp.presentation.core.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.submission.storyapp.databinding.FragmentSplashBinding
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    // Might need a view model, but it works for now
    @Inject lateinit var sessionUseCases: SessionUseCases

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            sessionUseCases.getSession().collect {
                val action = it?.let {
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                } ?: run {
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(action)
                }, 2000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
