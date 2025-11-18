package com.example.happykidneys.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate // <-- ADD THIS IMPORT
import androidx.core.os.LocaleListCompat // <-- ADD THIS IMPORT
import androidx.fragment.app.Fragment
import com.example.happykidneys.R
import com.example.happykidneys.databinding.FragmentProfileBinding
import com.example.happykidneys.ui.auth.AuthActivity
import com.example.happykidneys.utils.NotificationScheduler
import com.example.happykidneys.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        binding.tvUsername.text = preferenceManager.getUsername()
        binding.tvEmail.text = preferenceManager.getEmail()
        binding.tvDailyGoalValue.text = "${preferenceManager.getDailyGoal()} L"

        // Show member since date (you can store this when user registers)
        val memberSince = dateFormat.format(Date())
        binding.tvMemberSince.text = memberSince

        // Set notification switch
        binding.switchNotifications.isChecked = preferenceManager.isNotificationEnabled()

        // Set Water Rotation switch
        binding.switchWaterRotation.isChecked = preferenceManager.isWaterRotationEnabled()

        // Set language display
        val language = when (preferenceManager.getLanguage()) {
            "en" -> "English"
            "fr" -> "Français"
            "ar" -> "العربية"
            else -> "English"
        }
        binding.tvLanguageValue.text = language
    }

    private fun setupListeners() {
        // Notifications toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setNotificationEnabled(isChecked)
            if (isChecked) {
                val interval = preferenceManager.getReminderInterval()
                NotificationScheduler.scheduleReminders(requireContext(), interval)
            } else {
                NotificationScheduler.cancelReminders(requireContext())
            }
        }

        // Language selection
        binding.layoutLanguage.setOnClickListener {
            showLanguageDialog()
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Water Rotation
        binding.switchWaterRotation.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setWaterRotationEnabled(isChecked)
        }
    }

    // --- VV --- THIS FUNCTION IS NOW FIXED --- VV ---
    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Français", "العربية")
        val languageCodes = arrayOf("en", "fr", "ar")

        // Use the AppCompatDelegate to find the current language
        // This is more reliable than using your preference manager
        val currentLanguageTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val currentLanguage = if (currentLanguageTag.isEmpty()) "en" else currentLanguageTag

        val selectedIndex = languageCodes.indexOfFirst { it.startsWith(currentLanguage) }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.language))
            .setSingleChoiceItems(languages, selectedIndex) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]

                // 1. Update your PreferenceManager (good for consistency)
                preferenceManager.setLanguage(selectedLanguageCode)

                // 2. Update the text on the profile screen
                binding.tvLanguageValue.text = languages[which]

                // 3. This is the magic part:
                // Tell AppCompat to set the new language.
                // It will automatically save it and restart the Activity.
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedLanguageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)

                // 4. Just dismiss the dialog.
                // No need to show a "Please restart" message!
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    // --- ^^ --- THIS FUNCTION IS NOW FIXED --- ^^ ---


    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.confirm_logout))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun performLogout() {
        preferenceManager.logout()
        NotificationScheduler.cancelReminders(requireContext())

        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}