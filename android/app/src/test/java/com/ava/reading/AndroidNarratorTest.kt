package com.ava.reading

import androidx.test.core.app.ApplicationProvider
import com.ava.reading.content.AudioPlaybackResult
import com.ava.reading.content.AudioPurpose
import com.ava.reading.content.AudioRequest
import com.ava.reading.platform.AndroidNarrator
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * AndroidNarrator must fail closed: with no installed offline Persian voice
 * (the case under Robolectric, and on a device without one) play reports
 * Missing so the caller can show the written fallback, and every call stays
 * safe after close.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class AndroidNarratorTest {
    private fun narrator()=AndroidNarrator(ApplicationProvider.getApplicationContext())
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
}
