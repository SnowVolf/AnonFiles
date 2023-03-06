package ru.svolf.anonfiles.presentation.info

import android.app.Notification
import android.app.NotificationManager
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.work.*
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.R
import ru.svolf.bullet.BulletAdapter
import ru.svolf.anonfiles.adapter.decorators.PrettyPaddingItemDecoration
import ru.svolf.anonfiles.adapter.items.HistoryVH
import ru.svolf.anonfiles.adapter.items.TitleVH
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.entity.TitleItem
import ru.svolf.bullet.Item
import ru.svolf.anonfiles.databinding.FragmentInfoBinding
import ru.svolf.anonfiles.presentation.error.ErrorDialogFragment
import ru.svolf.anonfiles.util.dp
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private val infoModel by viewModels<InfoViewModel>()
    private val historyViewModel by viewModels<HistoryViewModel>()
    @Inject lateinit var api: AnonApi

    private lateinit var bulletAdapter: BulletAdapter

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
                    infoModel.getData(query, api)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        with(infoModel) {
            lifecycleScope.launchWhenStarted {
                historyViewModel.getItems().collect { items ->
                    val populatedItems = mutableListOf<Item>()
                    populatedItems.add(TitleItem(getString(R.string.title_history)))
                    populatedItems.addAll(items)
                    bulletAdapter.mergeItems(populatedItems)
                }
            }
            lifecycleScope.launchWhenStarted {
                loadingState.collectLatest(::switch)
            }
            lifecycleScope.launchWhenStarted {
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
                layoutProgress.visibility = View.VISIBLE
                layoutFileContent.visibility = View.GONE
            } else {
                layoutFileContent.visibility = View.VISIBLE
                layoutProgress.visibility = View.GONE
            }
        }
    }

    private fun onHistoryItemClick(item: DownloadsItem){
        PropertiesDialogFragment.newInstance(item, childFragmentManager)
    }

    private fun onFileFetched(apiFile: ApiFile){
        val fileName = apiFile.file.metadata.name
        val fileSize = apiFile.file.metadata.size.readable
        val link = apiFile.file.url.full

        binding.included.root.visibility = View.VISIBLE

        binding.included.title.text = fileName
        binding.included.subtitle.text = fileSize
        binding.included.buttonDownload.setOnClickListener {
            downloadFile(apiFile, link, fileName)
        }
    }

    private fun onErrorThrown(error: ApiError){
        if (binding.included.root.visibility == View.VISIBLE){
            binding.included.root.visibility = View.GONE
        }
        ErrorDialogFragment.newInstance(error, childFragmentManager)
    }

    private fun downloadFile(apiFile: ApiFile, link: String, fileSize: String){
        runWithPermissions(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.POST_NOTIFICATIONS){
            Toast.makeText(context, R.string.msg_download_start, Toast.LENGTH_SHORT).show()
            infoModel.downloadWithWorkManager(apiFile).observe(viewLifecycleOwner) {
                binding.included.subtitle.text = when (it.state) {
                    WorkInfo.State.SUCCEEDED -> getString(R.string.states_success)
                    WorkInfo.State.RUNNING -> getString(R.string.state_running)
                    WorkInfo.State.FAILED -> getString(R.string.state_error)
                    WorkInfo.State.CANCELLED -> getString(R.string.state_cancelled)
                    else -> getString(R.string.state_unknown)
                }
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    lifecycleScope.launchWhenStarted {
                        historyViewModel.put(link, fileSize)
                    }
                }
            }
        }
    }

}