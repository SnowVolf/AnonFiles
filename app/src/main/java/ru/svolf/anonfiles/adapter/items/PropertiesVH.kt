package ru.svolf.anonfiles.adapter.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.svolf.anonfiles.R
import ru.svolf.bullet.BaseViewHolder
import ru.svolf.bullet.ItemVH
import ru.svolf.anonfiles.data.entity.PropertiesItem
import ru.svolf.bullet.Item
import ru.svolf.anonfiles.databinding.ItemPropertiesBinding

/*
 * Created by SVolf on 01.03.2023, 16:27
 * This file is a part of "AnonFiles" project
 */
class PropertiesVH(private val listener: (PropertiesItem) -> Unit?): ItemVH<ItemPropertiesBinding, PropertiesItem> {
	override fun isMatchingItem(item: Item) = item is PropertiesItem

	override fun getLayoutId() = R.layout.item_properties

	override fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<ItemPropertiesBinding, PropertiesItem> {
		val binding = ItemPropertiesBinding.inflate(inflater, parent, false)
		return PropertiesViewHolder(binding)
	}

	override fun getDifferCallback(): DiffUtil.ItemCallback<PropertiesItem> {
		return object : DiffUtil.ItemCallback<PropertiesItem>(){
			override fun areItemsTheSame(oldItem: PropertiesItem, newItem: PropertiesItem): Boolean {
				// items is static and don't changed anymore
				return true
			}

			override fun areContentsTheSame(oldItem: PropertiesItem, newItem: PropertiesItem): Boolean {
				// items is static and don't changed anymore
				return true
			}
		}
	}

	inner class PropertiesViewHolder(binding: ItemPropertiesBinding): BaseViewHolder<ItemPropertiesBinding, PropertiesItem>(binding) {
		override fun onBind(item: PropertiesItem) {
			super.onBind(item)
			binding.propertyTitle.setText(item.name)
			binding.propertyValue.text = item.value
			binding.root.setOnClickListener {
				listener.invoke(item)
			}
		}
	}
}