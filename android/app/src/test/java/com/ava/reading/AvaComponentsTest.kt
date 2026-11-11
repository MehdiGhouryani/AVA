package com.ava.reading

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.*
import androidx.compose.ui.test.SemanticsMatcher
import com.ava.reading.ui.components.*
import com.ava.reading.ui.theme.AvaTheme
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35],qualifiers="w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AvaComponentsTest {
    @get:Rule val ui=createAndroidComposeRule<ComponentActivity>()

    /** Components under test render in the same child environment as the app: RTL, AvaTheme, optional large text. */
    private fun render(font:Float=1f, content: @Composable ()->Unit) {
        ui.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl,LocalDensity provides Density(density.density,font)) {
                AvaTheme {content()}
            }
        }
    }

    @Test fun primaryButtonIsSeventyTwoDpAndFires() {
        var fired=false
        render { AvaButton("شروع تمرین",{fired=true}) }
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed().assertHeightIsAtLeast(72.dp).performClick()
        assertTrue(fired)
    }
    @Test fun secondaryAndTertiaryStayAtSixtyFourDp() {
        render {
            AvaButton("ادامه",{},style=AvaButtonStyle.Secondary)
            AvaButton("بیشتر",{},style=AvaButtonStyle.Tertiary)
        }
        ui.onNodeWithText("ادامه").assertHeightIsAtLeast(64.dp)
        ui.onNodeWithText("بیشتر").assertHeightIsAtLeast(64.dp)
    }
    @Test fun loadingButtonIsDisabledAndDoesNotFire() {
        var fired=false
        render { AvaButton("بررسی", {fired=true}, loading=true) }
        ui.onNodeWithText("بررسی").assertIsNotEnabled().performClick()
        assertFalse(fired)
    }
    @Test fun answerOptionsExposeRadioSelection() {
        var picked by mutableStateOf("")
        render {
            Column(Modifier.selectableGroup()) {
                AvaAnswerOption("آ",selected=picked=="آ",onClick={picked="آ"})
                AvaAnswerOption("ب",selected=picked=="ب",onClick={picked="ب"})
            }
        }
        val radio=SemanticsMatcher.expectValue(SemanticsProperties.Role,androidx.compose.ui.semantics.Role.RadioButton)
        ui.onAllNodes(radio).assertCountEquals(2)
        ui.onNodeWithText("ب").performClick()
        ui.onAllNodes(radio)[1].assertIsSelected()
        ui.onAllNodes(radio)[0].assertIsNotSelected()
    }
    @Test fun interactiveCardExposesRoleAndStateAndDisabledBlocksClicks() {
        var fired=false;var picked=false
        render {
            Column {
                AvaCard(Modifier.fillMaxWidth(),onClick={picked=true},selected=true,stateDescription="انتخاب‌شده") {Text("کارت انتخاب‌شده")}
                AvaCard(Modifier.fillMaxWidth(),onClick={fired=true},enabled=false) {Text("کارت غیرفعال")}
            }
        }
        val button=SemanticsMatcher.expectValue(SemanticsProperties.Role,androidx.compose.ui.semantics.Role.Button)
        ui.onNode(button and SemanticsMatcher.expectValue(SemanticsProperties.StateDescription,"انتخاب‌شده")).assertIsDisplayed().performClick()
        assertTrue(picked)
        ui.onAllNodes(button)[1].assertIsNotEnabled().performClick()
        assertFalse(fired)
    }
    @Test fun feedbackIsPoliteLiveRegion() {
        render { AvaFeedback("پیدایش کردی!",tone=AvaFeedbackTone.Success) }
        ui.onNode(hasText("پیدایش کردی!") and SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion,LiveRegionMode.Polite)).assertIsDisplayed()
    }
    @Test fun iconButtonExposesItsSpokenLabel() {
        var fired=false
        render { AvaIconButton(Icons.AutoMirrored.Outlined.ArrowBack,"بازگشت",{fired=true}) }
        ui.onNodeWithContentDescription("بازگشت").assertIsDisplayed().performClick()
        assertTrue(fired)
    }
    @Test fun progressShowsRangeAndSpokenLabel() {
        render { AvaProgress(0.5f,label="۲ از ۶ مرحله") }
        ui.onNodeWithText("۲ از ۶ مرحله").assertIsDisplayed()
        ui.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertExists()
    }
    @Test fun characterAndStateMessagesRenderWithPersianText() {
        var started=false
        render {
            Column {
                AvaCharacterMessage("char.ava.head_happy","سلام! امروز یک قدم تازه برمی‌داریم.")
                AvaStateMessage("هنوز چیزی برای مرور نداری",body="بعد از اولین درس اینجا تمرین می‌آید.",artId="char.ava.reading",actionLabel="شروع تمرین",onAction={started=true})
            }
        }
        ui.onNodeWithText("سلام! امروز یک قدم تازه برمی‌داریم.").assertIsDisplayed()
        ui.onNodeWithText("هنوز چیزی برای مرور نداری").assertIsDisplayed()
        ui.onNodeWithText("شروع تمرین").performClick()
        assertTrue(started)
    }
    @Test fun componentsSurviveLargeFontInRtl() {
        render(font=1.5f) {
            Column {
                AvaButton("یک دکمه با متن بلندتر برای آزمون فونت بزرگ",{})
                AvaAnswerOption("نشانهٔ شبیه نمونه را پیدا کن",selected=false,onClick={})
                AvaFeedback("بازخوردی که باید در فونت بزرگ هم کامل دیده شود",tone=AvaFeedbackTone.Info)
            }
        }
        ui.onNodeWithText("یک دکمه با متن بلندتر برای آزمون فونت بزرگ").assertIsDisplayed()
        ui.onNodeWithText("بازخوردی که باید در فونت بزرگ هم کامل دیده شود").assertIsDisplayed()
    }
    @Test fun componentGallerySnapshotRendersEveryState() {
        render { AvaComponentGallery() }
        ui.onNodeWithText("دکمه‌ها").assertIsDisplayed()
        ui.onNodeWithText("در حال بررسی…").assertIsNotEnabled()
        // Bottom-of-gallery items are composed but scrolled out of the window in mdpi-844dp.
        ui.onNodeWithText("۲ از ۶ مرحله").assertExists()
        ui.waitForIdle()
        ui.runOnIdle {
            val view=ui.activity.window.decorView
            assertTrue(view.width>0&&view.height>0)
            val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file=File("build/reports/native-ui/ava-components-gallery.png");requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use {assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,it))};bitmap.recycle()
        }
    }
}
