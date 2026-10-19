package com.learning.readingadventure

import kotlin.math.*

data class AnimationConfig(val bounce: Float = 1f, val celebration: String = "leaves")
data class LearningWord(val id: String, val text: String, val difficulty: Int, val pronunciation: String, val sentence: String, val animation: AnimationConfig = AnimationConfig(), val character: String = "pip")
enum class Phase { LETTERS, SENTENCE, CELEBRATE }
/** Tracks physical tiles separately from occupied slots, including repeated letters. */
data class LetterBoard(val text: String, val assignments: Map<Int, Int> = emptyMap()) {
    val complete get() = assignments.size == text.length
    fun accepts(tile: Int, slot: Int) = tile in text.indices && slot in text.indices && tile !in assignments && slot !in assignments.values && text[tile] == text[slot]
    fun drop(tile: Int, slot: Int) = if (accepts(tile, slot)) copy(assignments = assignments + (tile to slot)) else this
}
object Progression {
    fun unlockedLevel(completed: Set<String>, words: List<LearningWord>): Int = when {
        words.count { it.difficulty == 2 && it.id in completed } >= 12 && words.count { it.difficulty == 1 && it.id in completed } >= 12 -> 3
        words.count { it.difficulty == 1 && it.id in completed } >= 12 -> 2
        else -> 1
    }
}
object Motion {
    fun snap(progress: Float): Float { val t = progress.coerceIn(0f, 1f); return 1f - (1f-t).pow(3) }
    fun bounce(seconds: Float, amplitude: Float) = sin(seconds * 3f) * amplitude
    fun celebration(progress: Float) = (1f-progress.coerceIn(0f,1f)).pow(2)
}
/** Generation tokens prevent a stopped utterance from finishing a newer animation. */
class AudioTokens {
    var generation = 0; private set
    var speaking = false; private set
    fun begin(): Int { generation++; speaking = true; return generation }
    fun complete(token: Int): Boolean { if (token != generation || !speaking) return false; speaking = false; return true }
    fun cancel() { generation++; speaking = false }
}
