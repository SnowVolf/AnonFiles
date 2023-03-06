package ru.svolf.anonfiles.adapter.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.svolf.anonfiles.R
import ru.svolf.bullet.BaseViewHolder
import ru.svolf.bullet.ItemVH
import ru.svolf.anonfiles.data.entity.ExplanationItem
import ru.svolf.bullet.Item
import ru.svolf.anonfiles.databinding.ItemExplanationBinding

/*
 * Created by SVolf on 01.03.2023, 17:37
 * This file is a part of "AnonFiles" project
 */
class ExplanationVH: ItemVH<ItemExplanationBinding, ExplanationItem> {

	override fun isMatchingItem(item: Item) = item is ExplanationItem

	override fun getLayoutId() = R.layout.item_explanation

	override fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<ItemExplanationBinding, ExplanationItem> {
		val binding = ItemExplanationBinding.inflate(inflater, parent, false)
		return ExplanationViewHolder(binding)
	}

	override fun getDifferCallback(): DiffUtil.ItemCallback<ExplanationItem> {
		return object: DiffUtil.ItemCallback<ExplanationItem>() {
			override fun areItemsTheSame(oldItem: ExplanationItem, newItem: ExplanationItem): Boolean {
				return true
			}

			override fun areContentsTheSame(oldItem: ExplanationItem, newItem: ExplanationItem): Boolean {
				return true
			}
		}
	}

	internal class ExplanationViewHolder(binding: ItemExplanationBinding) : BaseViewHolder<ItemExplanationBinding, ExplanationItem>(binding) {
		override fun onBind(item: ExplanationItem) {
			super.onBind(item)
			binding.textExplanation.setText(item.description)
		}
	}

}