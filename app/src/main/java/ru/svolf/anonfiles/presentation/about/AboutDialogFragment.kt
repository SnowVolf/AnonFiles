package ru.svolf.anonfiles.presentation.about

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.svolf.anonfiles.BuildConfig
import ru.svolf.anonfiles.util._string

/*
 * Created by SVolf on 17.02.2023, 21:23
 * This file is a part of "AnonFiles" project
 */
class AboutDialogFragment : DialogFragment() {
	companion object {
		private const val TAG = "AboutDialogFragment"
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return AlertDialog.Builder(requireContext())
			.setTitle(_string.app_name)
			.setMessage("Unofficial Android client for https://anonfiles.com/\nVersion: ${BuildConfig.VERSION_NAME}")
			.setPositiveButton(android.R.string.ok, null)
			.show()
	}

	fun show(manager: FragmentManager){
		show(manager, TAG)
	}

}