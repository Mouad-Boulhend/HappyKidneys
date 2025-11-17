package com.example.happykidneys.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.User
import com.example.happykidneys.data.repository.UserRepository
import com.example.happykidneys.databinding.FragmentRegisterBinding
import com.example.happykidneys.ui.MainActivity
import com.example.happykidneys.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var preferenceManager: PreferenceManager
    private var selectedBirthday: Long = 0
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var debugTapCount = 0
    private var lastDebugTapTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(username, email, password)) {
                performRegistration(username, email, password)
            }
        }

        // Listen for taps on the main container
        binding.registerContainer.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDebugTapTime < 500) { // Taps must be within 500ms
                debugTapCount++
            } else {
                debugTapCount = 1 // Reset count if tap is too slow
            }
            lastDebugTapTime = currentTime

            if (debugTapCount == 3) {
                fillDebugData()
                debugTapCount = 0 // Reset
            }
        }
    }

    private fun fillDebugData() {
        binding.etUsername.setText("TestUser")
        binding.etEmail.setText("test@example.com")
        binding.etPassword.setText("password123")

        // Set birthday to 20 years ago to pass validation
        val debugCalendar = Calendar.getInstance()
        debugCalendar.add(Calendar.YEAR, -20)
        selectedBirthday = debugCalendar.timeInMillis
        binding.etBirthday.setText(dateFormat.format(debugCalendar.time))

        Toast.makeText(requireContext(), "Debug data filled!", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18) // Default to 18 years ago

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedBirthday = selectedCalendar.timeInMillis
                binding.etBirthday.setText(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateInputs(username: String, email: String, password: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedBirthday == 0L) {
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

        if (password.length < 6) {
            Toast.makeText(
                requireContext(),
                "Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if user is at least 18 years old
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)
        if (selectedBirthday > calendar.timeInMillis) {
            Toast.makeText(
                requireContext(),
                getString(R.string.age_requirement),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun performRegistration(username: String, email: String, password: String) {
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                // Check if email already exists
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.email_exists),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnRegister.isEnabled = true
                    return@launch
                }

                // Create new user
                val user = User(
                    username = username,
                    email = email,
                    password = password,
                    birthday = selectedBirthday,
                    dailyGoal = 2.0f
                )
                val userId = userRepository.insert(user)

                // Log in the user
                preferenceManager.login(userId, username, email, 2.0f)

                Toast.makeText(
                    requireContext(),
                    getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()

                navigateToMain()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_generic),
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnRegister.isEnabled = true
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