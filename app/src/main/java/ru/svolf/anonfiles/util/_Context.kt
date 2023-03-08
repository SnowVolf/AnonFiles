package ru.svolf.anonfiles.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/*
 * Created by SVolf on 08.03.2023, 13:29
 * This file is a part of "AnonFiles" project
 */
fun Context.copyToClipboard(text: CharSequence) {
	val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
	val clipData = ClipData.newPlainText("AnonFilesText", text)
	clipboard.setPrimaryClip(clipData)
}
