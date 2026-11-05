package com.ava.reading

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * MainActivity wiring smoke test: the real onCreate path — ViewModel with the
 * DataStore repository, setContent with AvaApp, narrator lifecycle — must
 * compose a first screen without crashing. The exact first route depends on
 * the async content load, so the test asserts the composed root, not a route.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35],qualifiers="w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainActivityTest {
    @get:Rule val ui=createAndroidComposeRule<MainActivity>()
    @Test fun activityLaunchesAndComposesItsFirstScreen() {
        ui.waitForIdle()
        ui.onRoot().assertIsDisplayed()
        assertFalse(ui.activity.isFinishing)
    }
}
