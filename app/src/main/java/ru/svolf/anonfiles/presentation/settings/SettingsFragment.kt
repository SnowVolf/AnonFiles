package ru.svolf.anonfiles.presentation.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.presentation.about.AboutDialogFragment

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("about")?.setOnPreferenceClickListener {
            AboutDialogFragment().show(childFragmentManager)
            true
        }
    }
}