package com.ava.reading.platform

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import com.ava.reading.content.AudioPlayback
import com.ava.reading.content.AudioPlaybackResult
import com.ava.reading.content.AudioRequest

/** Installed offline voices only. No recording or implicit voice downloads. */
class AndroidNarrator(context: Context): AudioPlayback {
    private var engine: TextToSpeech?=null
    private var ready=false
    private var closed=false
    private var tone: ToneGenerator?=null
    init {
        try {engine=TextToSpeech(context.applicationContext) {status->
            if(!closed && status==TextToSpeech.SUCCESS) {
                try {
                    val tts=engine
                    val voice=tts?.voices?.firstOrNull {it.locale.language=="fa" && !it.isNetworkConnectionRequired && TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED !in it.features.orEmpty()}
                    ready=voice!=null && tts?.setVoice(voice)==TextToSpeech.SUCCESS
                    if(ready)tts?.setSpeechRate(.85f)
                } catch(_: Exception) {ready=false}
            }
        }} catch(_: Exception) {ready=false}
    }
    fun speak(text: String): Boolean = try { !closed && ready && engine?.speak(text.take(2000),TextToSpeech.QUEUE_FLUSH,null,"ava-reading")==TextToSpeech.SUCCESS } catch(_: Exception) { false }
    override fun play(request: AudioRequest): AudioPlaybackResult = if(speak(request.fallbackText))AudioPlaybackResult.PlayedFallback else AudioPlaybackResult.Missing
    fun celebrate() {if(closed)return;try {if(tone==null)tone=ToneGenerator(AudioManager.STREAM_MUSIC,25);tone?.startTone(ToneGenerator.TONE_PROP_ACK,100)} catch(_: Exception) {}}
    override fun stop() {try {engine?.stop()} catch(_: Exception) {}}
    fun close() {closed=true;ready=false;stop();engine?.shutdown();engine=null;tone?.release();tone=null}
}
