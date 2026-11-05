package com.ava.reading

import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.*
import androidx.lifecycle.SavedStateHandle
import com.ava.reading.content.*
import com.ava.reading.data.MockAuthRepository
import com.ava.reading.storage.Learner
import com.ava.reading.storage.MemoryLearnerRepository
import com.ava.reading.ui.*
import com.ava.reading.ui.screens.AvaApp
import com.ava.reading.ui.theme.AvaTheme
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.*
import java.util.concurrent.TimeUnit

/**
 * Journey tests (deep-review P1 §4.13): the real AvaViewModel wired into
 * the real AvaApp, driven through the actual buttons — login with the
 * mock code, a full lesson, a full story, a review session, and reset.
 *
 * The ViewModel waits ~1800 ms on Splash and ~600 ms after a correct
 * OTP, so each leg advances the main looper explicitly instead of
 * relying on waitForIdle alone. Text input follows the established
 * NativeUiTest pattern: hasSetTextAction() with Persian digits.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35],qualifiers="w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class JourneyUiTest {
    @get:Rule val ui=createAndroidComposeRule<ComponentActivity>()
    private fun model(signedIn: Boolean=false): AvaViewModel {
        val repo=MemoryLearnerRepository(Learner(signedIn=signedIn,onboarded=signedIn))
        return AvaViewModel(SavedStateHandle(),MockAuthRepository(latency=0),repo,progressClock={1234L})
    }
    private fun render(v: AvaViewModel) {
        ui.setContent {
            val s by v.state.collectAsState()
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AvaTheme {Box(Modifier.requiredSize(390.dp,844.dp)) {AvaApp(s,v.actions())}}
            }
        }
    }
    /** Advance the paused main looper past the VM's Splash/OTP delays. */
    private fun idle(seconds: Long=3) {
        shadowOf(Looper.getMainLooper()).idleFor(seconds,TimeUnit.SECONDS)
        ui.waitForIdle()
    }
    /** Answer every task of the current lesson (or review) correctly. */
    private fun answerAllTasks(v: AvaViewModel) {
        val tasks=v.state.value.tasks
        tasks.forEachIndexed {index,task->
            val node=ui.onAllNodesWithText(task.target)[if(task.kind==TaskKind.Listen)0 else 1]
            node.performScrollTo().performClick()
            ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
            ui.waitForIdle()
            if(index==tasks.lastIndex)ui.onNodeWithText("پایان تمرین").assertIsDisplayed().performClick()
            else ui.onNodeWithText("ادامه").assertIsDisplayed().performClick()
            ui.waitForIdle()
        }
    }

    @Test fun newUserLogsInWithMockCode() {
        val v=model()
        render(v);idle()
        ui.onNodeWithText("رد کردن").assertIsDisplayed().performClick()
        ui.waitForIdle()
        ui.onNode(hasSetTextAction()).performTextInput("۰۹۱۲۳۴۵۶۷۸۹")
        ui.onNodeWithText("حریم خصوصی و قوانین را می‌پذیرم").performClick()
        ui.onNodeWithText("دریافت کد").performScrollTo().assertIsEnabled().performClick()
        ui.waitForIdle()
        ui.onNode(hasSetTextAction()).performTextInput("۱۲۳۴۵۶")
        idle(2)
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        assertTrue(v.state.value.learner.signedIn)
    }

    @Test fun returningUserCompletesFirstLesson() {
        val v=model(signedIn=true)
        render(v);idle()
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed().performClick()
        ui.waitForIdle()
        answerAllTasks(v)
        ui.onNodeWithText("یک قدم تازه برداشتی!").assertExists()
        assertTrue(v.state.value.learner.completed.contains(Catalog.lessons.first().id))
    }

    @Test fun returningUserReadsFirstStory() {
        val v=model(signedIn=true)
        render(v);idle()
        ui.onNodeWithText("قصه‌ها").performClick()
        ui.waitForIdle()
        val story=Catalog.stories.first()
        ui.onNodeWithText(story.title).performScrollTo().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("باز کردن کتاب").assertIsDisplayed().performClick()
        ui.waitForIdle()
        repeat(story.pages.size-1) {
            ui.onNodeWithText("صفحهٔ بعد").assertIsDisplayed().performClick()
            ui.waitForIdle()
        }
        ui.onNodeWithText("رفتن به پرسش").assertIsDisplayed().performClick()
        ui.waitForIdle()
        ui.onNodeWithText(story.answer).performScrollTo().performClick()
        ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("آفرین، یادت مانده!").assertExists()
        ui.onNodeWithText("پایان قصه").assertIsDisplayed().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("یک قصهٔ دیگر خواندی!").assertExists()
        assertTrue(v.state.value.learner.stories.contains(story.id))
    }

    @Test fun lessonThenPracticeThenReset() {
        val v=model(signedIn=true)
        render(v);idle()
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed().performClick()
        ui.waitForIdle()
        answerAllTasks(v)
        ui.onNodeWithText("یک قدم تازه برداشتی!").assertExists()
        // Back to Home, then the Practice tab: the completed lesson
        // gives the Mix review something to review.
        ui.onNodeWithText("بازگشت به باغ").performScrollTo().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("مرور").performClick()
        ui.waitForIdle()
        ui.onNodeWithText("شروع مرور کوتاه").performScrollTo().performClick()
        ui.waitForIdle()
        assertTrue(v.state.value.reviewing)
        answerAllTasks(v)
        assertEquals(1,v.state.value.learner.reviews)
        // Review result leads back to Practice; then Profile → Settings → reset.
        ui.onNodeWithText("مرور").performScrollTo().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("من").performClick()
        ui.waitForIdle()
        ui.onNodeWithText("تنظیمات").performScrollTo().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("شروع دوبارهٔ باغ").performScrollTo().performClick()
        ui.waitForIdle()
        ui.onNodeWithText("پیشرفت باغ پاک شود؟").assertExists()
        ui.onNodeWithText("بله، ادامه بده").performClick()
        ui.waitForIdle()
        assertTrue(v.state.value.learner.completed.isEmpty())
        assertTrue(v.state.value.learner.stories.isEmpty())
        assertEquals(0,v.state.value.learner.reviews)
    }
}
