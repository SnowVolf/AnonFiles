package ru.svolf.anonfiles.presentation.info

import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.svolf.anonfiles.adapter.decorators.PrettyPaddingItemDecoration
import ru.svolf.anonfiles.adapter.items.ExplanationVH
import ru.svolf.anonfiles.adapter.items.PropertiesVH
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.entity.ExplanationItem
import ru.svolf.anonfiles.data.entity.PropertiesItem
import ru.svolf.anonfiles.databinding.DialogPropertiesBinding
import ru.svolf.anonfiles.util._string
import ru.svolf.anonfiles.util.copyToClipboard
import ru.svolf.anonfiles.util.dp
import ru.svolf.bullet.BulletAdapter

/*
 * Created by SVolf on 01.03.2023, 15:40
 * This file is a part of "AnonFiles" project
 */
/**
 * Вообще был вариант передавать чисто ID айтема и потом через репозиторий брать из БД айтем и парсить его содержимое.
 * Но по сути это не даст профита как такового, только лишь усложнив простой код
 */
class PropertiesDialogFragment : BottomSheetDialogFragment() {
	private var _binding: DialogPropertiesBinding? = null
	private val binding get() = _binding!!
	private var interestedItem: DownloadsItem? = null

	companion object {
		private const val TAG = "PropertiesDialogFragment"
		private const val ARG_DB_ITEM = "DB_Item"

		@JvmStatic
		fun newInstance(item: DownloadsItem, manager: FragmentManager) =
			PropertiesDialogFragment().also {
				it.arguments = Bundle().apply {
					putSerializable(ARG_DB_ITEM, item)
				}
			}.show(manager, TAG)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			interestedItem =
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
					it.getSerializable(ARG_DB_ITEM, DownloadsItem::class.java)
				else
					it.getSerializable(ARG_DB_ITEM) as DownloadsItem
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = DialogPropertiesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.title.setText(_string.title_properties)

		val adapterr = BulletAdapter(listOf(PropertiesVH(::clickOnItem), ExplanationVH()))
		with(binding.listProperties) {
			addItemDecoration(PrettyPaddingItemDecoration(12.dp))
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			adapter = adapterr
		}
		interestedItem?.let {
			adapterr.mergeItems(processItem(it))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun processItem(item: DownloadsItem) = listOf(
		PropertiesItem(_string.properties_filename, item.fileName),
		PropertiesItem(_string.properties_size, item.sizeReadable),
		PropertiesItem(_string.properties_link, item.link),
		PropertiesItem(_string.properties_uploaded, DateUtils.getRelativeTimeSpanString(
			item.id,
			System.currentTimeMillis(),
			DateUtils.MINUTE_IN_MILLIS)
			.toString()),
		ExplanationItem(_string.msg_item_copyable)
	)

	private fun clickOnItem(item: PropertiesItem) {
		requireContext().copyToClipboard(item.value)
		Toast.makeText(context, _string.msg_copied_clipboard, Toast.LENGTH_SHORT).show()
		dismiss()
	}

}