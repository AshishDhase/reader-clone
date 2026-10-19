package com.learning.readingadventure

import android.content.Context
import org.json.JSONObject

object Content {
    fun parse(json: String): List<LearningWord> {
        val pack = JSONObject(json)
        require(pack.getInt("schemaVersion") == 1)
        val items = pack.getJSONArray("words")
        val words = (0 until items.length()).map { index ->
            val item = items.getJSONObject(index)
            val animation = item.getJSONObject("animation")
            LearningWord(item.getString("id"), item.getString("text"), item.getInt("difficulty"), item.getString("pronunciation"), item.getString("sentence"), AnimationConfig(animation.getDouble("bounce").toFloat(), animation.getString("celebration")), item.optString("character", "pip"))
        }
        require(words.map { it.id }.distinct().size == words.size)
        require(words.all { it.id.isNotBlank() && it.text.matches(Regex("[a-z]{1,8}")) && it.difficulty in 1..3 && it.pronunciation.isNotBlank() && Regex("\\b${it.text}\\b", RegexOption.IGNORE_CASE).containsMatchIn(it.sentence) })
        return words
    }
    fun load(context: Context): List<LearningWord> = context.assets.list("content").orEmpty().filter { it.endsWith(".json") }.sorted().flatMap { name -> context.assets.open("content/$name").bufferedReader().use { parse(it.readText()) } }.also { words -> require(words.map { it.id }.distinct().size == words.size) }
}
