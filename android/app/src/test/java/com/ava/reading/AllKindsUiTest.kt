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
 *
 * Node lookup differs per kind, and the test says why instead of
 * assuming one node per target:
 * - Recognize, DotFamily, PositionalForm, Find: the target is rendered
 *   twice (TargetCard + choice tile), so the choice is node [1].
 * - Listen, InitialSound, FinalSound, Bubble: ListenCard hides the
 *   target word until the hint is revealed, so the only node is the
 *   choice tile / bubble.
 * - BuildWord, Segment, Blend: TargetCard shows the word once; the
 *   tokens are single letters, so the whole word is unique.
 * - Match: the target is a "a=b|c=d" spec, never rendered; the first
 *   token is the visible card.
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
    private val duplicateTargetKinds=setOf(TaskKind.Recognize,TaskKind.DotFamily,TaskKind.PositionalForm,TaskKind.Find)
    private fun visibleNode(task: Task): SemanticsNodeInteraction {
        val visible=if(task.kind==TaskKind.Match)task.tokens.first() else task.target
        return if(task.kind in duplicateTargetKinds)ui.onAllNodesWithText(visible)[1]
        else ui.onNodeWithText(visible)
    }
    @Test fun fixtureCoversEveryRegisteredKind() {
        assertEquals(TaskKind.entries,AllKindsFixture.tasks().map {it.kind})
    }
    @Test fun everyKindRendersItsTask() {
        AllKindsFixture.tasks().forEachIndexed {index,task->
            var state by mutableStateOf(AvaState(route=Route.Lesson,content=AllKindsFixture.snapshot(),selectedLesson="lesson_all_kinds",exercise=Exercise(index=index)))
            render({state})
            visibleNode(task).performScrollTo().assertIsDisplayed()
            if(task.kind==TaskKind.Match)ui.onNodeWithText("بررسی کن").assertDoesNotExist()
            else ui.onNodeWithText("بررسی کن").assertIsDisplayed()
        }
    }
    /**
     * Same twelve tasks, but answered for real through the reducer-wired
     * actions: choice kinds tap the correct tile, TokenBuild kinds tap
     * the tokens that spell the target, Match flips the three pairs.
     * Every kind must reach the "پیدایش کردی!" feedback.
     */
    @Test fun everyKindAnswersCorrectly() {
        AllKindsFixture.tasks().forEachIndexed {index,task->
            var state by mutableStateOf(AvaState(route=Route.Lesson,content=AllKindsFixture.snapshot(),selectedLesson="lesson_all_kinds",exercise=Exercise(index=index)))
            val actions=AvaActions(
                choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},
                chooseToken={state=state.copy(exercise=ExerciseReducer.chooseToken(state.exercise,it,state.tasks))},
                check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))},
                flip={state=state.copy(exercise=ExerciseReducer.flip(state.exercise,it,state.tasks))},
                clearMismatch={state=state.copy(exercise=ExerciseReducer.clearMismatch(state.exercise))}
            )
            render({state},actions)
            when(task.kind) {
                TaskKind.Match -> {
                    // Pairs are آب=آ, باد=ب, گل=گ; each label is a unique card.
                    listOf("آب" to "آ","باد" to "ب","گل" to "گ").forEach { (first,second) ->
                        ui.onNodeWithText(first).performScrollTo().performClick()
                        ui.onNodeWithText(second).performScrollTo().performClick()
                    }
                }
                TaskKind.BuildWord -> {
                    // Target آب, tokens [ب, آ]: tap آ then ب.
                    ui.onNodeWithText("آ").performScrollTo().performClick()
                    ui.onNodeWithText("ب").performScrollTo().performClick()
                    ui.onNodeWithText("بررسی کن").performScrollTo().performClick()
                }
                TaskKind.Segment -> {
                    // Target باد, tokens [د, ب, ا]: tap ب, ا, د in word order.
                    ui.onNodeWithText("ب").performScrollTo().performClick()
                    ui.onNodeWithText("ا").performScrollTo().performClick()
                    ui.onNodeWithText("د").performScrollTo().performClick()
                    ui.onNodeWithText("بررسی کن").performScrollTo().performClick()
                }
                TaskKind.Blend -> {
                    // Target باب, tokens [ب, ا, ب]: the two ب buttons stay
                    // composed after use, so the second one is node [1].
                    ui.onAllNodesWithText("ب")[0].performScrollTo().performClick()
                    ui.onNodeWithText("ا").performScrollTo().performClick()
                    ui.onAllNodesWithText("ب")[1].performScrollTo().performClick()
                    ui.onNodeWithText("بررسی کن").performScrollTo().performClick()
                }
                else -> {
                    visibleNode(task).performScrollTo().performClick()
                    ui.onNodeWithText("بررسی کن").performScrollTo().performClick()
                }
            }
            ui.onNodeWithText("پیدایش کردی!").assertExists()
            assertTrue("kind ${task.kind} should be correct",state.exercise.correct)
        }
    }
}
