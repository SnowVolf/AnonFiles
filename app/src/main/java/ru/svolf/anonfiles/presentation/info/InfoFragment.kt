package ru.svolf.anonfiles.presentation.info

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.adapter.decorators.PrettyPaddingItemDecoration
import ru.svolf.anonfiles.adapter.items.HistoryVH
import ru.svolf.anonfiles.adapter.items.TitleVH
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.api.ModeStrategy
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.entity.TitleItem
import ru.svolf.anonfiles.databinding.FragmentInfoBinding
import ru.svolf.anonfiles.util.ErrorResolver
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
        } else {
            Toast.makeText(context, _string.no_file_selected, Toast.LENGTH_SHORT).show()
        }
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

        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_settings -> {
                        findNavController().navigate(R.id.action_toSettings)
                        return true
                    }
                }
                return false
            }

        }, viewLifecycleOwner)

        binding.fieldSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    infoModel.fetchInfoForFile(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    switchModes(ModeStrategy.UPLOAD, null)
                }
                return false
            }
        })

        with(infoModel) {
            lifecycleScope.launchWhenStarted {
                historyViewModel.getItems()
                    .map { items ->
                        items.toMutableList<Item>().apply {
                            add(0, TitleItem(getString(_string.title_history), resources.getQuantityString(R.plurals.items_counters, items.size, items.size)))
                        }
                    }
                    .collect { populatedItems ->
                        bulletAdapter.mergeItems(populatedItems)
                    }
            }
            lifecycleScope.launchWhenStarted {
                loadingState.collect(::switch)
            }
            lifecycleScope.launchWhenResumed {
                statusSate.collectLatest {
                    binding.subtitle.setText(it)
                }
            }
            lifecycleScope.launchWhenStarted {
                responseState.collect { response ->
                    when(response) {
                        is AnonResult.Success -> onFileFetched(response.data, response.strategy)
                        is AnonResult.Error -> onErrorThrown(response.error, response.strategy)
                        is AnonResult.Empty -> Timber.d("Empty result")
                    }
                }
            }
        }
        switchModes(ModeStrategy.UPLOAD, null)
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

    private fun onFileFetched(apiFile: ApiFile, strategy: ModeStrategy){
        switchModes(strategy, apiFile)
        binding.title.text = apiFile.fileName
        binding.subtitle.text = apiFile.sizeReadable
    }

    private fun onErrorThrown(error: ApiError, strategy: ModeStrategy){
        val msg = ErrorResolver.localizedMessage[error.code]?.let { getString(it) } ?: "${error.message} (${error.code})"
        switchModes(strategy, null)
        binding.title.setText(_string.title_error)
        binding.subtitle.text = msg
        binding.fabProgressCircle.hide()
    }

    private fun switchModes(strategy: ModeStrategy, file: ApiFile?){
        if (strategy == ModeStrategy.DOWNLOAD) {
            binding.fab.setImageResource(R.drawable.ic_download)
            binding.fab.setOnClickListener {
                file?.let {
                    infoModel.downloadWithWorkManager(it)
                }
            }
        } else {
            binding.fab.setImageResource(R.drawable.ic_upload)
            binding.fab.setOnClickListener {
                pickLauncher.launch(arrayOf("*/*"))
            }
        }
    }

}