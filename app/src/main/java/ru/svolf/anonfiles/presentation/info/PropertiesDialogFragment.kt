package ru.svolf.anonfiles.presentation.info

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.svolf.anonfiles.R
import ru.svolf.bullet.BulletAdapter
import ru.svolf.anonfiles.adapter.items.ExplanationVH
import ru.svolf.anonfiles.adapter.items.PropertiesVH
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.entity.ExplanationItem
import ru.svolf.anonfiles.data.entity.PropertiesItem
import ru.svolf.anonfiles.databinding.DialogPropertiesBinding

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
					putParcelable(ARG_DB_ITEM, item)
				}
			}.show(manager, TAG)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			interestedItem = it.getParcelable(ARG_DB_ITEM)!!
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = DialogPropertiesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.title.setText(R.string.title_properties)
		val datapter = BulletAdapter(listOf(PropertiesVH(::clickOnItem), ExplanationVH()))
		binding.listProperties.adapter = datapter
		interestedItem?.let {
			datapter.mergeItems(processItem(it))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun processItem(item: DownloadsItem) = listOf(
		PropertiesItem(R.string.properties_filename, item.fileName),
		PropertiesItem(R.string.properties_size, item.sizeReadable),
		PropertiesItem(R.string.properties_link, item.link),
		PropertiesItem(R.string.properties_uploaded, DateUtils.getRelativeTimeSpanString(
			item.id,
			System.currentTimeMillis(),
			DateUtils.MINUTE_IN_MILLIS)
			.toString()),
		ExplanationItem(R.string.msg_item_copyable)
	)

	private fun clickOnItem(item: PropertiesItem) {
		val clipboard: ClipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		val clipData = ClipData.newPlainText(TAG, item.value)
		clipboard.setPrimaryClip(clipData)
		Toast.makeText(context, R.string.msg_copied_clipboard, Toast.LENGTH_SHORT).show()
	}

}