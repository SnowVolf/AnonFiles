package ru.svolf.anonfiles.presentation.error

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.svolf.anonfiles.adapter.items.ExplanationVH
import ru.svolf.anonfiles.adapter.items.PropertiesVH
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.api.ErrorCodes
import ru.svolf.anonfiles.data.entity.PropertiesItem
import ru.svolf.anonfiles.databinding.DialogPropertiesBinding
import ru.svolf.anonfiles.util._string
import ru.svolf.bullet.BulletAdapter

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
					putSerializable(ARG_DB_ITEM, item)
				}
			}.show(manager, TAG)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			interestedItem =
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
					it.getSerializable(ARG_DB_ITEM, ApiError::class.java)
				else
					it.getSerializable(ARG_DB_ITEM) as ApiError
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = DialogPropertiesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.title.setText(_string.title_error)
		val adapter = BulletAdapter(listOf(PropertiesVH(::println), ExplanationVH()))
		binding.listProperties.adapter = adapter
		interestedItem?.let {
			adapter.mergeItems(processItem(it))
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun processItem(item: ApiError) = listOf(
		PropertiesItem(_string.properties_error_message, getLocalizedMessage(item.code)),
		PropertiesItem(_string.properties_error_type, item.type),
		PropertiesItem(_string.properties_error_code, item.code.toString()),
	)

	private fun getLocalizedMessage(code: Int): String {
		return when(code) {
			ErrorCodes.ERROR_FILE_INVALID -> getString(_string.err_file_invalid)
			ErrorCodes.ERROR_FILE_BANNED -> getString(_string.err_file_banned)
			ErrorCodes.ERROR_FILE_EMPTY -> getString(_string.err_file_empty)
			ErrorCodes.ERROR_FILE_NOT_PROVIDED -> getString(_string.err_file_provided)
			ErrorCodes.ERROR_FILE_DISALLOWED_TYPE -> getString(_string.err_file_disallowed)
			ErrorCodes.ERROR_FILE_SIZE_EXCEEDED -> getString(_string.err_size_exceeded)
			ErrorCodes.ERROR_USER_MAX_BYTES_PER_DAY_REACHED -> getString(_string.err_bytes_day)
			ErrorCodes.ERROR_USER_MAX_BYTES_PER_HOUR_REACHED -> getString(_string.err_bytes_hour)
			ErrorCodes.ERROR_USER_MAX_FILES_PER_DAY_REACHED -> getString(_string.err_files_day)
			ErrorCodes.ERROR_USER_MAX_FILES_PER_HOUR_REACHED -> getString(_string.err_files_hour)
			ErrorCodes.NOT_FOUND -> getString(_string.err_404)
			ErrorCodes.STATUS_ERROR_SYSTEM_FAILURE -> getString(_string.err_sys_failture)
			else -> getString(_string.err_wtf)
		}
	}

}