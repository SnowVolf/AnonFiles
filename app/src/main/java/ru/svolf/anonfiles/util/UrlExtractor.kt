package ru.svolf.anonfiles.util

import timber.log.Timber

/*
 * Created by SVolf on 16.02.2023, 15:27
 * This file is a part of "AnonFiles" project
 */
object UrlExtractor {
	/**
	 * Transforms url into file ID
	 * For example: https://anonfiles.com/Eeg9s3scnf/Download_zip; https://anonfiles.com/Eeg9s3scnf/ -> Eeg9s3scnf
	 * @return file id or original link if cannot extract
	 */
	fun extractId(linkId: ContentString): ContentString {
		val regex = "^https?://anonfiles\\.\\w+/(\\w+).+$".toRegex()
		return regex.find(linkId)?.groups?.get(1)?.value ?: linkId
	}

	/**
	 * Transforms url into file name
	 * For example: https://anonfiles.com/ccocIfYey5/IMG_20230211_WA0003_jpg -> IMG_20230211_WA0003_jpg
	 * @return file name or original link if cannot extract
	 */
	fun extractName(linkId: ContentString): ContentString {
		val regex = "^https?://anonfiles\\.com/\\w+/(\\w+)$".toRegex()
		return regex.find(linkId)?.groups?.get(1)?.value ?: linkId
	}

	/**
	 * Собсна ищет ссылку по заданному паттерну. При первом же нахождении останавливает поиск и возвращает ссылку
	 * @param inputString контент html страницы как string
	 * @return ссылку на файл на сервере которую уже можно скачать или null
	 */
	fun findMatchedString(inputString: String): String? {
		val pattern = Regex("href=\"(https://cdn-\\d+\\.anonfiles.com/\\w+/\\w+-\\d+/\\S+)\"")
		var result: String? = null

		inputString.lineSequence().forEach { line ->
			val matchResult = pattern.find(line)?.groups?.get(1)
			if (matchResult != null) {
				result = matchResult.value
				return@forEach
			}
		}
		Timber.d("Finded string from html = $result")
		return result
	}
}