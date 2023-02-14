package ru.svolf.anonfiles.presentation.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.svolf.anonfiles.databinding.FragmentInfoBinding
@AndroidEntryPoint
class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private val infoModel by viewModels<InfoViewModel>()
    companion object {
        // Резолвится из DeepLink Navigation UI
        private const val ARG_FILE_ID = "file_id"
        fun newInstance(link: String) = InfoFragment().arguments?.putString(ARG_FILE_ID, link)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ARG_FILE_ID)?.let { infoModel.getData(it) }
        infoModel.loadingState.observe(viewLifecycleOwner) { loadState ->
            // Показываем заглушку
            if (loadState) {
                binding.layoutProgress.visibility = View.VISIBLE
                binding.layoutFileContent.visibility = View.GONE
            } else {
                // Контент загрузился
                binding.layoutFileContent.visibility = View.VISIBLE
                binding.layoutProgress.visibility = View.GONE
                infoModel.responseData.observe(viewLifecycleOwner) {response ->
                    if (response.status) {
                        val file = response.data?.file!!
                        binding.textContent.text = "File: ${file.metadata.name}\nSize: ${file.metadata.size.readable}"
                    }
                }
            }
        }
    }

}