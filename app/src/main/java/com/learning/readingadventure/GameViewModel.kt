package com.learning.readingadventure

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("garden", 0)
    val voice = AndroidVoiceEngine(application)
    val feedback = FeedbackAudio()
    var words by mutableStateOf<List<LearningWord>>(emptyList()); private set
    var error by mutableStateOf<String?>(null); private set
    var completed by mutableStateOf(prefs.getStringSet("completed", emptySet()).orEmpty().toSet()); private set
    var volume by mutableFloatStateOf(prefs.getFloat("volume", .8f)); private set
    var phonetic by mutableStateOf(prefs.getBoolean("phonetic", false)); private set
    var active by mutableStateOf<LearningWord?>(null); private set
    var board by mutableStateOf(LetterBoard("")); private set
    var phase by mutableStateOf(Phase.LETTERS); private set
    var message by mutableStateOf("Find a letter's home")
    var selected by mutableIntStateOf(-1)
    var hintSlot by mutableIntStateOf(-1)
    val unlocked get() = Progression.unlockedLevel(completed, words)
    init {
        voice.volume = volume
        viewModelScope.launch {
            try { words = withContext(Dispatchers.IO) { Content.load(application) } }
            catch (_: Exception) { error = "The word garden could not load. Please reopen the app." }
        }
    }
    fun changeVolume(value: Float) { volume = value.coerceIn(0f,1f); voice.volume = volume; prefs.edit().putFloat("volume",volume).apply() }
    fun changePhonetic(value: Boolean) { phonetic = value; prefs.edit().putBoolean("phonetic",value).apply() }
    fun start(word: LearningWord) {
        if (word.difficulty > unlocked) return
        voice.stop(); active = word; board = LetterBoard(word.text); phase = Phase.LETTERS; selected = -1; hintSlot = -1
        message = "Find a letter's home"
        voice.speak(word.pronunciation)
    }
    fun select(tile: Int) {
        val word = active ?: return
        if (phase != Phase.LETTERS || tile !in word.text.indices || tile in board.assignments) return
        selected = tile; hintSlot = -1
        voice.speak(LetterSounds.forLetter(word.text[tile], phonetic))
    }
    fun drop(tile: Int, slot: Int) {
        if (phase != Phase.LETTERS) return
        if (board.accepts(tile,slot)) {
            board = board.drop(tile,slot); selected = -1; hintSlot = -1
            feedback.play(true,volume); message = "Lovely! Keep growing."
            if (board.complete) {
                val completedWord = active
                viewModelScope.launch {
                    delay(400) // Let the final tile settle before replacing the playfield.
                    if (active === completedWord && board.complete && phase == Phase.LETTERS) {
                        phase = Phase.SENTENCE; message = "Find the word for the story"
                        completedWord?.let { voice.speak(it.pronunciation) }
                    }
                }
            }
        } else {
            selected = -1; hintSlot = active?.text?.indices?.firstOrNull { it !in board.assignments.values && active?.text?.get(it) == active?.text?.getOrNull(tile) } ?: -1
            message = "Try the glowing home"; feedback.play(false,volume)
        }
    }
    fun sentenceChoice(text: String) {
        val word = active ?: return
        if (phase != Phase.SENTENCE) return
        if (text != word.text) { message = "Listen and try another word"; voice.speak(word.pronunciation); return }
        phase = Phase.CELEBRATE; message = "You grew a new word!"
        completed = completed + word.id
        prefs.edit().putStringSet("completed", completed).apply()
        feedback.play(true, volume)
        voice.speak(word.sentence)
    }
    fun repeat() { active?.let { voice.speak(if (phase == Phase.CELEBRATE) it.sentence else it.pronunciation) } }
    fun home() { voice.stop(); active = null; selected = -1 }
    fun next() {
        val available = words.filter { it.difficulty <= unlocked }
        val next = available.firstOrNull { it.id !in completed } ?: available.getOrNull((available.indexOf(active)+1) % available.size.coerceAtLeast(1))
        next?.let(::start)
    }
    fun resetProgress() { completed = emptySet(); prefs.edit().remove("completed").apply(); home() }
    override fun onCleared() { voice.close(); feedback.close() }
}
