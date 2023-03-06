package ru.svolf.bullet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

/*
 * Created by SVolf on 18.02.2023, 13:09
 * This file is a part of "AnonFiles" project
 */
interface ItemVH<V : ViewBinding, I: Item> {

	fun isMatchingItem(item: Item): Boolean

	@LayoutRes
	fun getLayoutId(): Int

	fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<V, I>

	fun getDifferCallback(): DiffUtil.ItemCallback<I>

}