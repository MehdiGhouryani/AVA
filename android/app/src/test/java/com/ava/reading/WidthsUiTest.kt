package com.ava.reading

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.screens.AvaApp
import com.ava.reading.ui.theme.AvaTheme
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*

/**
 * Width coverage (deep-review P1 §4.13): the 360 dp compact boundary
 * itself was never exercised, nor the common 412 dp large-phone width.
 * Only w390dp (default) and w320dp existed before. Each test follows
 * the established NativeUiTest pattern: method-level @Config plus a
 * render box of the same size.
 */
@RunWith(RobolectricTestRunner::class)
// The class-level qualifiers were dead (both tests override them); only
// sdk=[35] belongs here — the widths live on the method-level @Configs.
@Config(sdk=[35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WidthsUiTest {
    @get:Rule val ui=createAndroidComposeRule<ComponentActivity>()
    private fun render(state: ()->AvaState,actions:AvaActions=AvaActions(),width:Int,height:Int) {
        ui.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl,LocalDensity provides Density(density.density,1f)) {
                AvaTheme {Box(Modifier.requiredSize(width.dp,height.dp)) {AvaApp(state(),actions)}}
            }
        }
    }
    private fun firstLessonRenders(width: Int,height: Int) {
        val first=Catalog.lessons.first().tasks.first()
        render({AvaState(route=Route.Lesson)},width=width,height=height)
        ui.onAllNodesWithText(first.target)[1].performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("بررسی کن").assertIsDisplayed()
    }

    @Config(qualifiers="w360dp-h640dp-mdpi")
    @Test fun homeLessonAndBubblesRenderAt360dp() {
        render({AvaState(route=Route.Home)},width=360,height=640)
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        firstLessonRenders(360,640)
        // The 360 dp boundary: three bubbles share 288 dp of content
        // width, so each is about 85 dp — still within 64–96 dp.
        val bubbleIndex=AllKindsFixture.tasks().indexOfFirst {it.kind==TaskKind.Bubble}
        render({AvaState(route=Route.Lesson,content=AllKindsFixture.snapshot(),selectedLesson="lesson_all_kinds",exercise=Exercise(index=bubbleIndex))},width=360,height=640)
        ui.onNodeWithText("پ").performScrollTo().assertIsDisplayed()
        val radio=SemanticsMatcher.expectValue(SemanticsProperties.Role,Role.RadioButton)
        ui.onAllNodes(radio).assertCountEquals(3)
        ui.onNodeWithText("بررسی کن").assertIsDisplayed()
    }

    @Config(qualifiers="w412dp-h892dp-mdpi")
    @Test fun homeAndLessonRenderAt412dp() {
        render({AvaState(route=Route.Home)},width=412,height=892)
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        render({AvaState(route=Route.Lesson)},width=412,height=892)
        val first=Catalog.lessons.first().tasks.first()
        ui.onAllNodesWithText(first.target)[1].performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("بررسی کن").assertIsDisplayed()
    }
}
