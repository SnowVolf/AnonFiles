package ru.svolf.bullet

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/*
 * Created by SVolf on 18.02.2023, 14:40
 * This file is a part of "AnonFiles" project
 */
abstract class BaseViewHolder<out V: ViewBinding, I : Item> (val binding: V): RecyclerView.ViewHolder(binding.root) {

	lateinit var item: I

	open fun onBind(item: I){
		this.item = item
	}

}