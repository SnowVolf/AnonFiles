package ru.svolf.anonfiles.presentation.upload

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.svolf.anonfiles.R

class UploadFragment : Fragment() {

    companion object {
        fun newInstance() = UploadFragment()
    }

    private lateinit var viewModel: UploadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
        // TODO: Use the ViewModel
    }

}