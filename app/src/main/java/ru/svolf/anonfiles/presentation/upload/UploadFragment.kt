package ru.svolf.anonfiles.presentation.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.svolf.anonfiles.R

class UploadFragment : Fragment() {

	companion object {
		fun newInstance() = UploadFragment()
	}

	private val viewModel by viewModels<UploadViewModel>()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_upload, container, false)
	}

}