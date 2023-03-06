package ru.svolf.bullet

import androidx.recyclerview.widget.DiffUtil

/*
 * Created by SVolf on 18.02.2023, 15:20
 * This file is a part of "AnonFiles" project
 */
class BulletDiffer(
	private val downloads: List<ItemVH<*, *>>,
	private val oldList: List<Item>,
	private val newList: List<Item>
) : DiffUtil.Callback() {
	override fun getOldListSize() = oldList.size

	override fun getNewListSize() = newList.size

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val olds = oldList[oldItemPosition]
		val news = newList[newItemPosition]
		if (olds::class != news::class) return false

		return getItemCallback(olds).areItemsTheSame(olds, news)
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val olds = oldList[oldItemPosition]
		val news = newList[newItemPosition]
		if (olds::class != news::class) return false

		return getItemCallback(olds).areContentsTheSame(olds, news)
	}

	private fun getItemCallback(item: Item): DiffUtil.ItemCallback<Item> = downloads.find { it.isMatchingItem(item) }
		?.getDifferCallback()
		?.let { it as DiffUtil.ItemCallback<Item> }
		?: throw IllegalStateException("Cannot find differ for $item")
}