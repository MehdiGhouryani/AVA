package com.ava.reading

import androidx.test.core.app.ApplicationProvider
import com.ava.reading.content.AudioPlaybackResult
import com.ava.reading.content.AudioPurpose
import com.ava.reading.content.AudioRef
import com.ava.reading.content.AudioRequest
import com.ava.reading.platform.AndroidNarrator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaPlayer

/**
 * AndroidNarrator must fail closed: with no installed offline Persian voice
 * (the case under Robolectric, and on a device without one) play reports
 * Missing so the caller can show the written fallback, and every call stays
 * safe after close. When the requested audioRef resolves to a bundled
 * delivery asset, play must open a player and report PlayedLocal instead of
 * speaking TTS. The player is injected because unit tests carry no bundled
 * raw resources; opening a real delivery file is exercised on device.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class AndroidNarratorTest {
    private fun narrator()=AndroidNarrator(ApplicationProvider.getApplicationContext())
    /** A shadow-backed player with a registered datasource, prepared, so start() follows the real state machine. */
    private fun preparedPlayer(): android.media.MediaPlayer {
        val ds=org.robolectric.shadows.util.DataSource.toDataSource("test://tone")
        ShadowMediaPlayer.addMediaInfo(ds, ShadowMediaPlayer.MediaInfo(300,0))
        val p=android.media.MediaPlayer()
        val sh=shadowOf(p)
        sh.doSetDataSource(ds)
        sh.setState(ShadowMediaPlayer.State.PREPARED)
        return p
    }
    @Test fun playReturnsMissingWhenNoPersianVoiceIsInstalled() {
        val n=narrator()
        assertEquals(AudioPlaybackResult.Missing,n.play(AudioRequest(null,"سلام",AudioPurpose.Prompt)))
        n.close()
    }
    @Test fun speakCelebrateAndStopAreSafeAfterClose() {
        val n=narrator();n.close()
        assertFalse(n.speak("سلام"))
        assertEquals(AudioPlaybackResult.Missing,n.play(AudioRequest(null,"سلام",AudioPurpose.Feedback)))
        n.celebrate();n.stop();n.close()
    }
    @Test fun playOpensBundledLocalAudioAndReportsPlayedLocal() {
        val opened=mutableListOf<Int>()
        val n=AndroidNarrator(ApplicationProvider.getApplicationContext(),mapOf("task.test.prompt" to 42)) {_,res->opened+=res;preparedPlayer()}
        assertEquals(AudioPlaybackResult.PlayedLocal,n.play(AudioRequest(AudioRef("task.test.prompt"),"سلام",AudioPurpose.Prompt)))
        assertEquals(listOf(42),opened)
        n.stop();n.close()
        // After close the local path must also fail closed.
        assertEquals(AudioPlaybackResult.Missing,n.play(AudioRequest(AudioRef("task.test.prompt"),"سلام",AudioPurpose.Prompt)))
        assertEquals(listOf(42),opened)
    }
    @Test fun playFallsBackWhenRefIsNotBundled() {
        val n=AndroidNarrator(ApplicationProvider.getApplicationContext(),mapOf("task.other.prompt" to 42)) {_,_->preparedPlayer()}
        val r=n.play(AudioRequest(AudioRef("task.missing.prompt"),"سلام",AudioPurpose.Prompt))
        assertTrue("expected fallback or missing, got $r",r==AudioPlaybackResult.PlayedFallback||r==AudioPlaybackResult.Missing)
        n.close()
    }
    @Test fun playFallsBackWhenTheBundledPlayerCannotBeOpened() {
        val n=AndroidNarrator(ApplicationProvider.getApplicationContext(),mapOf("task.test.prompt" to 42)) {_,_->null}
        val r=n.play(AudioRequest(AudioRef("task.test.prompt"),"سلام",AudioPurpose.Prompt))
        assertTrue("expected fallback or missing, got $r",r==AudioPlaybackResult.PlayedFallback||r==AudioPlaybackResult.Missing)
        n.close()
    }
}
