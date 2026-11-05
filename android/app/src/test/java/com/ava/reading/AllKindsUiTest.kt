package com.ava.reading

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.screens.AvaApp
import com.ava.reading.ui.theme.AvaTheme
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*

/**
 * I8: every activity kind renders through the real Lesson screen.
 * The fixture lesson is synthetic (never published content), so this is
 * the only place Find, Bubble and Match renderers are exercised — the
 * published catalog has no tasks of those kinds yet.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35],qualifiers="w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AllKindsUiTest {
    @get:Rule val ui=createAndroidComposeRule<ComponentActivity>()
    private fun render(state: ()->AvaState,actions:AvaActions=AvaActions(),width:Int=390,height:Int=844,font:Float=1f) {
        ui.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl,LocalDensity provides Density(density.density,font)) {
                AvaTheme {Box(Modifier.requiredSize(width.dp,height.dp)) {AvaApp(state(),actions)}}
            }
        }
    }
    @Test fun fixtureCoversEveryRegisteredKind() {
        assertEquals(TaskKind.entries,AllKindsFixture.tasks().map {it.kind})
    }
    @Test fun everyKindRendersItsTask() {
        AllKindsFixture.tasks().forEachIndexed {index,task->
            var state by mutableStateOf(AvaState(route=Route.Lesson,content=AllKindsFixture.snapshot(),selectedLesson="lesson_all_kinds",exercise=Exercise(index=index)))
            render({state})
            val visible=if(task.kind==TaskKind.Match)task.tokens.first() else task.target
            ui.onNodeWithText(visible).performScrollTo().assertIsDisplayed()
            if(task.kind==TaskKind.Match)ui.onNodeWithText("بررسی کن").assertDoesNotExist()
            else ui.onNodeWithText("بررسی کن").assertIsDisplayed()
        }
    }
}
