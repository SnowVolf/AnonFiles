package ru.svolf.anonfiles.util

import android.content.res.Resources

/*
 * Created by SVolf on 18.02.2023, 13:51
 * This file is a part of "AnonFiles" project
 */
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()