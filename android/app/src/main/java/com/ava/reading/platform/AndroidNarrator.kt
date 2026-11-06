package com.ava.reading.platform

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import com.ava.reading.content.AudioPlayback
import com.ava.reading.content.AudioPlaybackResult
import com.ava.reading.content.AudioRequest
import com.ava.reading.content.BundledAudio

/** Bundled human audio first, installed offline voices second. No recording, downloads or network. */
class AndroidNarrator(context: Context,private val localAudio: Map<String,Int> =BundledAudio.ids,private val playerFactory: (Context,Int)->MediaPlayer?=MediaPlayer::create): AudioPlayback {
 private var engine: TextToSpeech?=null
 private var ready=false
 private var closed=false
 private var tone: ToneGenerator?=null
 private var player: MediaPlayer?=null
 private val appContext=context.applicationContext
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
 /** Plays a bundled delivery asset from res/raw; null from the factory means the asset is unusable and the caller falls back. */
 private fun playLocal(res: Int): Boolean = try {
  if(closed)false else {
   stop()
   val p=playerFactory(appContext,res)
   player=p
   p?.setOnCompletionListener {if(player===it)player=null;it.release()}
   p?.start()
   p!=null
  }
 } catch(_: Exception) {false}
 override fun play(request: AudioRequest): AudioPlaybackResult {
  val res=request.audioRef?.id?.let {localAudio[it]}
  if(res!=null&&playLocal(res))return AudioPlaybackResult.PlayedLocal
  return if(speak(request.fallbackText))AudioPlaybackResult.PlayedFallback else AudioPlaybackResult.Missing
 }
 fun celebrate() {if(closed)return;try {if(tone==null)tone=ToneGenerator(AudioManager.STREAM_MUSIC,25);tone?.startTone(ToneGenerator.TONE_PROP_ACK,100)} catch(_: Exception) {}}
 override fun stop() {try {engine?.stop()} catch(_: Exception) {};try {player?.let {if(it.isPlaying)it.stop();it.release()}} catch(_: Exception) {};player=null}
 fun close() {closed=true;ready=false;stop();engine?.shutdown();engine=null;tone?.release();tone=null}
}
