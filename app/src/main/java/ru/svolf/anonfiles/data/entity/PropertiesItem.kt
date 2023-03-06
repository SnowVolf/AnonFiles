package ru.svolf.anonfiles.data.entity

import androidx.annotation.StringRes
import ru.svolf.bullet.Item

/*
 * Created by SVolf on 01.03.2023, 16:28
 * This file is a part of "AnonFiles" project
 */
data class PropertiesItem(@StringRes val name: Int, val value: String): Item