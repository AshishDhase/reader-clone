package com.learning.readingadventure

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/** A recorded-voice backend can replace this contract without changing gameplay. */
interface VoiceEngine {
    val speaking: StateFlow<Boolean>
    val status: StateFlow<String>
    var volume: Float
    fun speak(text: String, onComplete: () -> Unit = {})
    fun stop()
    fun close()
}
class AndroidVoiceEngine(context: Context) : VoiceEngine {
    override val speaking = MutableStateFlow(false)
    override val status = MutableStateFlow("Preparing offline voice…")
    override var volume = 0.8f
    private val handler = Handler(Looper.getMainLooper())
    private val tokens = AudioTokens()
    private val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focus = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
        .setOnAudioFocusChangeListener { change -> if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) stop() }.build()
    private var ready = false
    private var closed = false
    private var pending: Pair<String, () -> Unit>? = null
    private var completion: (() -> Unit)? = null
    private var timeout: Runnable? = null
    private var tts: TextToSpeech? = null
    init {
        tts = TextToSpeech(context.applicationContext) { result -> handler.post {
            if (closed) return@post
            val engine = tts
            val voice = engine?.voices?.filter { it.locale.language == Locale.ENGLISH.language && !it.isNetworkConnectionRequired && !it.features.orEmpty().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED) }?.sortedBy { if (it.locale == Locale.US) 0 else 1 }?.firstOrNull()
            ready = result == TextToSpeech.SUCCESS && voice != null && engine?.setVoice(voice) == TextToSpeech.SUCCESS
            if (ready) {
                engine?.setSpeechRate(0.78f)
                engine?.setPitch(1.08f)
                status.value = "Offline English voice ready"
            } else status.value = "No offline English voice. Add one in your phone's TTS settings."
            pending?.let { pending = null; speak(it.first, it.second) }
        } }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) { handler.post { if (id?.toIntOrNull() == tokens.generation && tokens.speaking) speaking.value = true } }
            override fun onDone(id: String?) { handler.post { finish(id?.toIntOrNull()) } }
            @Deprecated("Platform callback") override fun onError(id: String?) { handler.post { status.value = "Voice could not play. You can keep playing."; finish(id?.toIntOrNull()) } }
        })
    }
    private fun finish(token: Int?) {
        if (token == null || !tokens.complete(token)) return
        timeout?.let(handler::removeCallbacks); timeout = null
        speaking.value = false
        manager.abandonAudioFocusRequest(focus)
        val callback = completion; completion = null; callback?.invoke()
    }
    override fun speak(text: String, onComplete: () -> Unit) {
        stop()
        if (closed) return
        if (status.value.startsWith("Preparing")) { pending = text to onComplete; return }
        if (!ready || volume <= 0f) { onComplete(); return }
        if (manager.requestAudioFocus(focus) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) { onComplete(); return }
        val token = tokens.begin(); completion = onComplete
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.coerceIn(0f,1f)) }
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, token.toString())
        if (result == TextToSpeech.ERROR) { finish(token); return }
        timeout = Runnable { if (token == tokens.generation) { tts?.stop(); finish(token) } }.also { handler.postDelayed(it, 18000) }
    }
    override fun stop() {
        pending = null; completion = null; tokens.cancel(); speaking.value = false
        timeout?.let(handler::removeCallbacks); timeout = null
        tts?.stop(); manager.abandonAudioFocusRequest(focus)
    }
    override fun close() { stop(); closed = true; tts?.shutdown(); tts = null }
}

/** Original short synthesized cues; no recordings or external assets. */
class FeedbackAudio {
    private var tone: ToneGenerator? = null
    fun play(success: Boolean, volume: Float) {
        tone?.release(); tone = null
        if (volume <= 0f) return
        try { tone = ToneGenerator(AudioManager.STREAM_MUSIC, (volume * 55).toInt()).also { it.startTone(if (success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP, if (success) 130 else 65) } } catch (_: RuntimeException) { /* Audio is optional; gameplay continues. */ }
    }
    fun close() { tone?.release(); tone = null }
}

/** TTS approximations, not a substitute for validated recorded phonemes. */
object LetterSounds {
    private val sounds = mapOf('a' to "ah", 'b' to "buh", 'c' to "kuh", 'd' to "duh", 'e' to "eh", 'f' to "fff", 'g' to "guh", 'h' to "huh", 'i' to "ih", 'j' to "juh", 'k' to "kuh", 'l' to "lll", 'm' to "mmm", 'n' to "nnn", 'o' to "oh", 'p' to "puh", 'q' to "kwuh", 'r' to "rrr", 's' to "sss", 't' to "tuh", 'u' to "uh", 'v' to "vvv", 'w' to "wuh", 'x' to "ks", 'y' to "yuh", 'z' to "zzz")
    fun forLetter(letter: Char, phonetic: Boolean) = if (phonetic) sounds[letter] ?: letter.toString() else letter.toString().uppercase()
}
