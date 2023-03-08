package ru.svolf.anonfiles.presentation.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.work.WorkInfo
import com.afollestad.assent.Permission
import com.afollestad.assent.coroutines.awaitPermissionsGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.adapter.decorators.PrettyPaddingItemDecoration
import ru.svolf.anonfiles.adapter.items.HistoryVH
import ru.svolf.anonfiles.adapter.items.TitleVH
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.entity.TitleItem
import ru.svolf.anonfiles.databinding.FragmentInfoBinding
import ru.svolf.anonfiles.presentation.error.ErrorDialogFragment
import ru.svolf.anonfiles.util._string
import ru.svolf.anonfiles.util.dp
import ru.svolf.bullet.BulletAdapter
import ru.svolf.bullet.Item
import timber.log.Timber

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var bulletAdapter: BulletAdapter

    private val infoModel by viewModels<InfoViewModel>()
    private val historyViewModel by viewModels<HistoryViewModel>()

    private val pickLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            infoModel.uploadWithWorkManager(uri)
            binding.fabProgressCircle.show()
        } else {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "MainAnonFilesFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bulletAdapter = BulletAdapter(listOf(TitleVH(), HistoryVH(::onHistoryItemClick)))

        with(binding.listHistory){
            addItemDecoration(PrettyPaddingItemDecoration(12.dp))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = bulletAdapter
        }

        binding.fieldSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    infoModel.getData(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.fab.setOnClickListener {
            pickLauncher.launch(arrayOf("*/*"))
        }

        with(infoModel) {
            lifecycleScope.launchWhenStarted {
                historyViewModel.getItems().map { items ->
                    items.toMutableList<Item>().apply {
                        add(0, TitleItem(getString(_string.title_history)))
                    }
                }.collect { populatedItems ->
                    bulletAdapter.mergeItems(populatedItems)
                }
                loadingState.collectLatest(::switch)

                responseState.collectLatest { response ->
                    when(response) {
                        is AnonResult.Success -> onFileFetched(response.data)
                        is AnonResult.Error -> onErrorThrown(response.error)
                        is AnonResult.Empty -> Timber.d("Empty result")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun switch(showLoading: Boolean){
        with(binding) {
            if (showLoading){
                fabProgressCircle.show()
            } else {
                fabProgressCircle.hide()
            }
        }
    }

    private fun onHistoryItemClick(item: DownloadsItem){
        PropertiesDialogFragment.newInstance(item, childFragmentManager)
    }

    private suspend fun onFileFetched(apiFile: ApiFile){
        binding.title.text = apiFile.fileName
        binding.subtitle.text = apiFile.sizeReadable
        binding.fab.setOnClickListener {
            lifecycleScope.launch {
                downloadFile(apiFile, apiFile.fullUrl, apiFile.sizeReadable)
            }
        }
    }

    private fun onErrorThrown(error: ApiError){
        binding.title.setText(_string.title_error)
        binding.fabProgressCircle.hide()
        ErrorDialogFragment.newInstance(error, childFragmentManager)
    }

    private suspend fun downloadFile(apiFile: ApiFile, link: String, fileSize: String){
        awaitPermissionsGranted(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.POST_NOTIFICATIONS)

        Toast.makeText(context, _string.msg_download_start, Toast.LENGTH_SHORT).show()
        infoModel.downloadWithWorkManager(apiFile).observe(viewLifecycleOwner) {
            when (it.state) {
                WorkInfo.State.SUCCEEDED -> {
                    binding.subtitle.setText(_string.states_success)
                    binding.fabProgressCircle.beginFinalAnimation()
                    lifecycleScope.launch {
                        historyViewModel.put(link, fileSize)
                    }
                }
                WorkInfo.State.RUNNING -> {
                    binding.subtitle.setText(_string.state_running)
                    binding.fabProgressCircle.show()
                }
                WorkInfo.State.FAILED -> {
                    binding.subtitle.setText(_string.state_error)
                    binding.fabProgressCircle.hide()
                }
                WorkInfo.State.CANCELLED -> {
                    binding.subtitle.setText(_string.state_cancelled)
                    binding.fabProgressCircle.hide()
                }
                else -> {
                    binding.subtitle.setText(_string.state_unknown)
                    binding.fabProgressCircle.hide()
                }
            }
        }
    }

}