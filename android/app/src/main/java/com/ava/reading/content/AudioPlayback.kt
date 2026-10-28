package com.ava.reading.content

enum class AudioPurpose { Prompt, Story, Feedback }
data class AudioRequest(val audioRef: AudioRef?,val fallbackText: String,val purpose: AudioPurpose)
sealed interface AudioPlaybackResult {
    data object PlayedLocal: AudioPlaybackResult
    data object PlayedFallback: AudioPlaybackResult
    data object Missing: AudioPlaybackResult
}
interface AudioPlayback {
    fun play(request: AudioRequest): AudioPlaybackResult
    fun stop()
}
class FakeAudioPlayback(private val result: AudioPlaybackResult=AudioPlaybackResult.PlayedFallback): AudioPlayback {
    val requests=mutableListOf<AudioRequest>()
    var stops=0
    override fun play(request: AudioRequest): AudioPlaybackResult {requests+=request;return result}
    override fun stop() {stops++}
}
