package ru.svolf.anonfiles.adapter.items

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.svolf.anonfiles.R
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.databinding.ItemDownloadsBinding
import ru.svolf.anonfiles.util._drawable
import ru.svolf.bullet.BaseViewHolder
import ru.svolf.bullet.Item
import ru.svolf.bullet.ItemVH

/*
 * Created by SVolf on 18.02.2023, 18:14
 * This file is a part of "AnonFiles" project
 */
class HistoryVH(private val onClickDownloads: (DownloadsItem) -> Unit): ItemVH<ItemDownloadsBinding, DownloadsItem> {

	override fun isMatchingItem(item: Item) = item is DownloadsItem

	override fun getLayoutId() = R.layout.item_downloads

	override fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<ItemDownloadsBinding, DownloadsItem> {
		val binding = ItemDownloadsBinding.inflate(inflater, parent, false)
		return HistoryViewHolder(binding, onClickDownloads)
	}

	private val diffUtil = object : DiffUtil.ItemCallback<DownloadsItem>() {

		override fun areItemsTheSame(oldItem: DownloadsItem, newItem: DownloadsItem) = oldItem.id == newItem.id

		override fun areContentsTheSame(oldItem: DownloadsItem, newItem: DownloadsItem) = oldItem.link == newItem.link

	}

	override fun getDifferCallback(): DiffUtil.ItemCallback<DownloadsItem> {
		return diffUtil
	}

	internal class HistoryViewHolder(binding: ItemDownloadsBinding, private val onClickDownloads: (DownloadsItem) -> Unit):
		BaseViewHolder<ItemDownloadsBinding, DownloadsItem>(binding) {

		override fun onBind(item: DownloadsItem) {
			super.onBind(item)
			with(binding) {
				val icon = when(item.isUploaded) {
					true -> _drawable.ic_upload
					false -> _drawable.ic_download
				}
				downloadIcon.setImageResource(icon)
				titleDownload.text = item.fileName
				titleSize.text = item.sizeReadable
				titleDate.text = DateUtils.getRelativeTimeSpanString(binding.root.context, item.id)
				root.setOnClickListener { onClickDownloads.invoke(item) }
			}
		}
		}

}