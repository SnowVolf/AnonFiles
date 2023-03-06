package ru.svolf.bullet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/*
 * Created by SVolf on 18.02.2023, 13:10
 * This file is a part of "AnonFiles" project
 */
class BulletAdapter(private val adapterItems: List<ItemVH<*, *>>)
	: RecyclerView.Adapter<BaseViewHolder<ViewBinding, Item>>() {

	private val items = mutableListOf<Item>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding, Item> {
		val inflater = LayoutInflater.from(parent.context)
		return adapterItems.find { it.getLayoutId() == viewType }
			?.getViewHolder(inflater, parent)
			?.let { it as BaseViewHolder<ViewBinding, Item> }
			?: throw IllegalArgumentException("Cannot create ViewHolder for ViewType = $viewType")
	}

	override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding, Item>, position: Int) {
		holder.onBind(items[position])
	}

	override fun getItemViewType(position: Int): Int {
		val item = items[position]
		return adapterItems.find { it.isMatchingItem(item) }
			?.getLayoutId()
			?: throw IllegalArgumentException("Cannot find viewType for $item")
	}

	override fun getItemCount(): Int = items.size

	fun mergeItems(newItems: List<Item>){
		val itms = newItems.toList()
		val differ = BulletDiffer(adapterItems, items, itms)
		val result = DiffUtil.calculateDiff(differ)
		items.clear()
		items.addAll(newItems)
		result.dispatchUpdatesTo(this)
	}
}