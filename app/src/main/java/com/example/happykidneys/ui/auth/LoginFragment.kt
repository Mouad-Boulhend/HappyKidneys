package com.example.happykidneys.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.happykidneys.R
import com.example.happykidneys.data.repository.UserRepository
import com.example.happykidneys.databinding.FragmentLoginBinding
import com.example.happykidneys.ui.MainActivity
import com.example.happykidneys.utils.PreferenceManager
import com.example.happykidneys.data.database.AppDatabase
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var preferenceManager: PreferenceManager

    // Variables to track debug taps
    private var debugTapCount = 0
    private var lastDebugTapTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        userRepository = UserRepository(database.userDao())
        preferenceManager = PreferenceManager(requireContext())

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }

        // Triple-tap listener on the background container
        binding.loginContainer.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            // Check if the tap is within 500ms of the last one
            if (currentTime - lastDebugTapTime < 500) {
                debugTapCount++
            } else {
                debugTapCount = 1 // Reset count if too slow
            }
            lastDebugTapTime = currentTime

            if (debugTapCount == 3) {
                // Auto-fill credentials
                binding.etEmail.setText("test@example.com")
                binding.etPassword.setText("password123")
                Toast.makeText(requireContext(), "Debug info filled!", Toast.LENGTH_SHORT).show()
                debugTapCount = 0 // Reset counter
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.fill_all_fields),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_email),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    preferenceManager.login(user.id, user.username, user.email, user.dailyGoal)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMain()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_generic),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}