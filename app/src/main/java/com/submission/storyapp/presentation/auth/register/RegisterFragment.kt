package com.submission.storyapp.presentation.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.submission.storyapp.databinding.FragmentRegisterBinding
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animate()
        observeInput()
        navigation()
    }

    private fun animate() { binding.apply {
        ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(messageTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(signInTextView, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(signUpButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title, message,
                nameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                login, signup
            )
            startDelay = 100
        }.start()
    }}

    private fun observeInput() { binding.apply {
        nameEditText.addTextChangedListener {
            viewModel.updateName(it.toString())
        }

        emailEditText.addTextChangedListener {
            viewModel.updateEmail(it.toString())
        }

        passwordEditText.addTextChangedListener {
            viewModel.updatePassword(it.toString())
        }
    }}

    private fun navigation() { binding.apply {
        signInTextView.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        signUpButton.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            observeOnSignUp()
        }
    }}

    private fun observeOnSignUp() { viewModel.signUp().observe(viewLifecycleOwner) { response ->
        when (response) {
            is ResponseWrapper.Success -> {
                binding.lpiLoading.visibility = View.GONE

                showToast("Signed up successful")

                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            is ResponseWrapper.Error -> {
                binding.lpiLoading.visibility = View.GONE

                showToast(response.error)
            }
            is ResponseWrapper.Loading -> {
                binding.lpiLoading.visibility = View.VISIBLE
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
