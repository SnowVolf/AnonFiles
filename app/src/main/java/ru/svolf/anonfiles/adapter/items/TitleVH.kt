package ru.svolf.anonfiles.adapter.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.svolf.anonfiles.R
import ru.svolf.bullet.BaseViewHolder
import ru.svolf.bullet.ItemVH
import ru.svolf.anonfiles.data.entity.TitleItem
import ru.svolf.bullet.Item
import ru.svolf.anonfiles.databinding.ItemTitleBinding

/*
 * Created by SVolf on 18.02.2023, 17:43
 * This file is a part of "AnonFiles" project
 */
class TitleVH: ItemVH<ItemTitleBinding, TitleItem> {

	override fun isMatchingItem(item: Item) = item is TitleItem

	override fun getLayoutId() = R.layout.item_title

	override fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<ItemTitleBinding, TitleItem> {
		val binding = ItemTitleBinding.inflate(inflater, parent, false)
		return TitleViewHolder(binding)
	}

	override fun getDifferCallback(): DiffUtil.ItemCallback<TitleItem> {
		return diffUtil
	}

	private val diffUtil = object : DiffUtil.ItemCallback<TitleItem>() {
		override fun areItemsTheSame(oldItem: TitleItem, newItem: TitleItem): Boolean {
			// always true
			return oldItem.title == oldItem.title
		}

		override fun areContentsTheSame(oldItem: TitleItem, newItem: TitleItem): Boolean {
			// always true
			return oldItem == oldItem
		}

	}

	inner class TitleViewHolder(binding: ItemTitleBinding): BaseViewHolder<ItemTitleBinding, TitleItem>(binding){
		override fun onBind(item: TitleItem) {
			super.onBind(item)
			binding.itemTitle.text = item.title
		}
	}
}