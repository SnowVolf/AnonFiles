package ru.svolf.anonfiles.adapter

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

/*
 * Created by SVolf on 16.02.2023, 16:44
 * This file is a part of "AnonFiles" project
 */
class ViewPagerAdapter(owner: FragmentActivity) : FragmentStateAdapter(owner) {
	private val fragmentList = LinkedList<Fragment>()
	private val iconList = LinkedList<Int>()

	fun addFragment(fragment: Fragment, @DrawableRes icon: Int){
		fragmentList.add(fragment)
		iconList.add(icon)
	}

	fun getIcon(position: Int) = iconList[position]

	override fun getItemCount(): Int = fragmentList.size

	override fun createFragment(position: Int): Fragment = fragmentList[position]
}