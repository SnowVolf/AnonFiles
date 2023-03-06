package ru.svolf.anonfiles.presentation.error

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
import ru.svolf.anonfiles.api.ApiError
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
class ErrorDialogFragment : BottomSheetDialogFragment() {
	private var _binding: DialogPropertiesBinding? = null
	private val binding get() = _binding!!
	private var interestedItem: ApiError? = null

	companion object {
		private const val TAG = "ErrorDialogFragment"
		private const val ARG_DB_ITEM = "ERR_Item"

		@JvmStatic
		fun newInstance(item: ApiError, manager: FragmentManager) =
			ErrorDialogFragment().also {
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
		binding.title.setText(R.string.title_error)
		val datapter = BulletAdapter(listOf(PropertiesVH(::println), ExplanationVH()))
		binding.listProperties.adapter = datapter
		interestedItem?.let {
			datapter.mergeItems(processItem(it))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun processItem(item: ApiError) = listOf(
		PropertiesItem(R.string.properties_error_message, item.message),
		PropertiesItem(R.string.properties_error_type, item.type),
		PropertiesItem(R.string.properties_error_code, item.code.toString()),
	)

}