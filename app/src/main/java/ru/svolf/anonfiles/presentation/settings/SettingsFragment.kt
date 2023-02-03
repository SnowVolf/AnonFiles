package ru.svolf.anonfiles.presentation.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.svolf.anonfiles.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}