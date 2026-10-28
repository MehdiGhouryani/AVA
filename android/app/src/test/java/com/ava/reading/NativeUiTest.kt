package com.ava.reading

import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.ava.reading.storage.Learner
import com.ava.reading.ui.*
import com.ava.reading.ui.screens.AvaApp
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
class NativeUiTest {
    @get:Rule val ui=createAndroidComposeRule<ComponentActivity>()
    private fun render(state: ()->AvaState,actions:AvaActions=AvaActions(),width:Int=390,height:Int=844,font:Float=1f) {
        ui.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl,LocalDensity provides Density(density.density,font)) {
                AvaTheme {Box(Modifier.requiredSize(width.dp,height.dp)) {AvaApp(state(),actions)}}
            }
        }
    }
    private fun snapshot(name:String) {
        ui.waitForIdle()
        ui.runOnIdle {
            val view=ui.activity.window.decorView
            assertTrue(view.width>0&&view.height>0)
            val bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file=File("build/reports/native-ui/$name.png");requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use {assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,it))};bitmap.recycle()
        }
    }
    @Test fun freshHomeHasPrimaryActionAndLockedStages() {
        var continued=false
        render({AvaState(route=Route.Home,restoring=false)},AvaActions(continueLearning={continued=true}))
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed().performClick();assertTrue(continued)
        ui.onAllNodesWithText("دوستی با نشانه‌ها").assertCountEquals(2)
        ui.onNodeWithContentDescription("مرحلهٔ ۲، پس از مرحلهٔ قبل").assertIsNotEnabled()
        snapshot("home-fresh")
    }
    @Test fun tabsOnlyAppearOnRootDestinationsAndNavigate() {
        var state by mutableStateOf(AvaState(route=Route.Home));val a=AvaActions(navigate={state=state.copy(route=it)})
        render({state},a);ui.onNodeWithText("قصه‌ها").performClick();assertEquals(Route.Library,state.route)
        ui.onNodeWithText("باغ قصه‌ها").assertIsDisplayed();snapshot("library")
        ui.runOnIdle {state=state.copy(route=Route.StoryDetails)}
        ui.onNodeWithText("مرور").assertDoesNotExist()
    }
    @Test fun consentIsFullRowAndInvalidFormIsDisabled() {
        var state by mutableStateOf(AvaState(route=Route.Phone));val a=AvaActions(phone={state=state.copy(phone=normalizePhone(it))},consent={state=state.copy(consent=it)})
        render({state},a)
        val checkbox=SemanticsMatcher.expectValue(SemanticsProperties.Role,Role.Checkbox)
        ui.onAllNodes(checkbox).assertCountEquals(1)
        ui.onNode(hasText("حریم خصوصی و قوانین را می‌پذیرم") and checkbox).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription,"پذیرفته نشده"))
        ui.onNodeWithText("دریافت کد").assertIsNotEnabled()
        ui.onNode(hasSetTextAction()).performTextInput("۰۹۱۲۳۴۵۶۷۸۹")
        ui.onNodeWithText("حریم خصوصی و قوانین را می‌پذیرم").performClick()
        assertTrue(state.consent)
        ui.onNode(hasText("حریم خصوصی و قوانین را می‌پذیرم") and checkbox).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription,"پذیرفته شد"))
        ui.onNodeWithText("دریافت کد").performScrollTo().assertIsEnabled();snapshot("phone")
    }
    @Test fun authenticationFieldsExposeTheActualErrorToAccessibility() {
        var state by mutableStateOf(AvaState(route=Route.Phone,message="phone_error_invalid"))
        render({state})
        ui.onNode(hasSetTextAction()).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error,"شماره درست نیست"))
        ui.runOnIdle {state=AvaState(route=Route.Otp,phone="09123456789",message="otp_wrong")}
        ui.onNode(hasSetTextAction()).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error,"کد درست نیست، دوباره تلاش کنید"))
    }
    @Test fun oneOtpEditingNodeAcceptsPersianPaste() {
        var state by mutableStateOf(AvaState(route=Route.Otp,phone="09123456789",resendUntil=90_000));var entered=""
        render({state},AvaActions(code={entered=normalizeDigits(it);state=state.copy(code=entered)}))
        ui.onAllNodes(hasSetTextAction()).assertCountEquals(1)
        ui.onNode(hasSetTextAction()).performTextInput("۱۲۳۴۵۶")
        assertEquals("123456",entered);snapshot("otp")
    }
    @Test fun legalDialogContainsReadableCurrentText() {
        var state by mutableStateOf(AvaState(route=Route.Phone))
        render({state},AvaActions(legal={state=state.copy(legal=it)}))
        ui.onNodeWithText("حریم خصوصی").performClick()
        ui.onNodeWithText("نام انتخابی، تنظیمات و پیشرفت",substring=true).assertExists()
        ui.onNodeWithText("بستن").performClick();assertNull(state.legal)
    }
    @Test fun wrongAnswerCanBeChangedUsingActualControls() {
        var state by mutableStateOf(AvaState(route=Route.Lesson))
        val a=AvaActions(choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))})
        render({state},a);ui.onNodeWithText("ا").performScrollTo().performClick();ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
        ui.onNodeWithText("اشکالی ندارد؛ دوباره به نمونه نگاه کن.").assertExists();snapshot("lesson-retry")
        ui.onAllNodesWithText("آ")[1].performScrollTo().performClick();ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
        ui.onNodeWithText("پیدایش کردی!").assertExists();assertTrue(state.exercise.correct)
    }
    @Test fun completeLessonUsesActualButtonsAndShowsResult() {
        var state by mutableStateOf(AvaState(route=Route.Lesson))
        val a=AvaActions(choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))},advance={state=state.copy(exercise=ExerciseReducer.advance(state.exercise,state.tasks))})
        render({state},a)
        Catalog.lessons[0].tasks.forEachIndexed {index,t->
            val target=ui.onAllNodesWithText(t.target)[if(t.kind==TaskKind.Listen)0 else 1]
            target.performScrollTo().performClick();ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
            ui.onNodeWithText(if(index==Catalog.lessons[0].tasks.lastIndex)"پایان تمرین" else "ادامه").assertIsDisplayed().performClick()
        }
        ui.onNodeWithText("یک قدم تازه برداشتی!").assertExists();snapshot("lesson-complete")
    }
    @Test fun storyWrongThenCorrectQuestionCompletes() {
        var state by mutableStateOf(AvaState(route=Route.StoryQuestion))
        val a=AvaActions(storyAnswer={state=state.copy(storyAnswer=it,storyChecked=false)},checkStory={state=state.copy(storyChecked=true)},finishStory={if(state.storyAnswer==state.story.answer)state=state.copy(route=Route.StoryComplete)})
        render({state},a);ui.onNodeWithText("توپ").performClick();ui.onNodeWithText("بررسی کن").performClick();ui.onNodeWithText("یک بار دیگر فکر کن",substring=true).assertExists()
        ui.onNodeWithText("آب").performScrollTo().performClick();ui.onNodeWithText("بررسی کن").performClick();ui.onNodeWithText("پایان قصه").performClick()
        ui.onNodeWithText("یک قصهٔ دیگر خواندی!").assertExists();snapshot("story-complete-flow")
    }
    @Test fun libraryShowsCompletedAndReadingStates() {
        val learner=Learner(stories=setOf("little_seed"),positions=mapOf("little_seed" to 0,"friends_book" to 1))
        render({AvaState(route=Route.Library,learner=learner)})
        ui.onNodeWithText("خوانده‌شده").assertExists()
        ui.onNodeWithText("در حال خواندن").assertExists()
        ui.onNodeWithText("ادامه از صفحهٔ ۲ از ۴").assertExists()
        snapshot("library-reading-states")
    }
    @Test fun storyDetailsUsesResumeAndCompletedActions() {
        var state by mutableStateOf(AvaState(route=Route.StoryDetails,learner=Learner(positions=mapOf("little_seed" to 2))))
        render({state})
        ui.onNodeWithText("ادامه از صفحهٔ ۳ از ۵").assertExists()
        ui.onNodeWithText("ادامهٔ قصه").assertExists()
        ui.runOnIdle {state=state.copy(learner=state.learner.copy(stories=setOf("little_seed")))}
        ui.onNodeWithText("این قصه را خوانده‌ای",substring=true).assertExists()
        ui.onNodeWithText("دوباره خواندن").assertExists()
        snapshot("story-details-complete")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun lastStoryPageLeadsToQuestionAndExplainsAudioOff() {
        val story=Catalog.stories.first();var advanced=false
        val state=AvaState(route=Route.StoryReader,storyPage=story.pages.lastIndex,learner=Learner(narration=false))
        render({state},AvaActions(page={advanced=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("شنیدن").assertIsNotEnabled()
        ui.onNodeWithText("راهنمای صوتی در تنظیمات خاموش است",substring=true).performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("رفتن به پرسش").assertIsDisplayed().performClick();assertTrue(advanced)
        snapshot("story-last-page-audio-off")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun compactStoryQuestionUsesRadioChoicesAndFixedAction() {
        var state by mutableStateOf(AvaState(route=Route.StoryQuestion))
        render({state},AvaActions(storyAnswer={state=state.copy(storyAnswer=it,storyChecked=false)}),width=320,height=560,font=1.3f)
        val radio=SemanticsMatcher.expectValue(SemanticsProperties.Role,Role.RadioButton)
        ui.onAllNodes(radio).assertCountEquals(3)
        ui.onNodeWithText("بررسی کن").assertIsDisplayed().assertIsNotEnabled()
        ui.onNodeWithText("آب").performScrollTo().assertHeightIsAtLeast(48.dp).performClick().assertIsSelected()
        snapshot("story-question-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun storyCompleteSummarizesActualPageCount() {
        render({AvaState(route=Route.StoryComplete)},width=320,height=560,font=1.3f)
        ui.onNodeWithText("۵ صفحه خواندی و پرسش قصه را پاسخ دادی.").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("برگشت به قصه‌ها").performScrollTo().assertIsDisplayed()
        snapshot("story-complete-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun resetRequiresConfirmationAndCancelDoesNotReset() {
        var state by mutableStateOf(AvaState(route=Route.Settings));var reset=false
        render({state},AvaActions(confirm={state=state.copy(confirmation=it)},confirmAction={reset=true}),width=320,height=560)
        ui.onNodeWithText("شروع دوبارهٔ باغ").performScrollTo().performClick();ui.onNodeWithText("پیشرفت باغ پاک شود؟").assertExists()
        ui.onNodeWithText("فعلاً نه").performClick();assertFalse(reset);assertNull(state.confirmation);snapshot("settings-320")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun settingsUseSingleFullRowSwitchTargetsWithReadableState() {
        var state by mutableStateOf(AvaState(route=Route.Settings,learner=Learner(narration=true,sounds=false,reducedMotion=true)))
        val actions=AvaActions(setting={key,value->state=state.copy(learner=when(key) {
            "narration"->state.learner.copy(narration=value);"sounds"->state.learner.copy(sounds=value);else->state.learner.copy(reducedMotion=value)
        })})
        render({state},actions,width=320,height=560,font=1.3f)
        val switches=SemanticsMatcher.expectValue(SemanticsProperties.Role,Role.Switch)
        ui.onAllNodes(switches).assertCountEquals(3)
        ui.onNode(hasText("صدای راهنما") and switches).assertIsOn().performClick().assertIsOff()
        assertFalse(state.learner.narration)
        snapshot("settings-switches-320-large")
    }
    @Test fun expandedHelpIsReadableAndCanCollapse() {
        render({AvaState(route=Route.Help)})
        ui.onNodeWithText("از کجا شروع کنم؟").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription,"بسته")).performClick()
        ui.onNodeWithText("از کجا شروع کنم؟").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription,"باز"));ui.onNodeWithText("در باغ، دکمهٔ شروع تمرین",substring=true).assertExists()
        ui.onNodeWithText("از کجا شروع کنم؟").performClick();ui.onNodeWithText("در باغ، دکمهٔ شروع تمرین",substring=true).assertDoesNotExist()
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun largeTextOnboardingStillAllowsContinueAndSkip() {
        var route:Route?=null
        render({AvaState(route=Route.Onboarding2)},AvaActions(navigate={route=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("ادامه").performScrollTo().assertIsDisplayed().performClick();assertEquals(Route.Phone,route);snapshot("onboarding2-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun compactOtpRetainsInputAndContinue() {
        render({AvaState(route=Route.Otp,phone="09123456789",resendUntil=90_000)},width=320,height=560)
        ui.onAllNodes(hasSetTextAction()).assertCountEquals(1);ui.onNodeWithText("ادامه").performScrollTo().assertExists();snapshot("otp-320")
    }
    @Test fun pageGalleryRendersEveryDestination() {
        var state by mutableStateOf(AvaState())
        render({state})
        Route.entries.forEach {route->
            ui.runOnIdle {state=AvaState(route=route,restoring=false,phone="09123456789",learner=Learner(name="مینا",completed=setOf(Catalog.lessons[0].id),stories=setOf(Catalog.stories[0].id),reviews=2),profileName="مینا")}
            snapshot("page-${route.name.lowercase()}")
        }
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun compactHomeKeepsPrimaryActionVisibleWithLargeTextAndLongName() {
        render({AvaState(route=Route.Home,learner=Learner(name="نام بلند برای دوست آوا"))},width=320,height=560,font=1.3f)
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        ui.onNodeWithText("من").assertIsDisplayed()
        snapshot("home-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun homeIgnoresRemovedProgressAndDoesNotResumeACompletedLesson() {
        val first=Catalog.lessons.first().id
        render({AvaState(route=Route.Home,learner=Learner(completed=setOf(first,"removed"),activeLesson=first,activeIndex=2))},width=320,height=560,font=1.3f)
        ui.onNodeWithText("۱ از ۶ مرحله").assertExists()
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        ui.onNodeWithText("ادامهٔ تمرین").assertDoesNotExist()
        snapshot("home-stale-data-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun rootNavigationLabelsRemainAvailableAtLargerText() {
        var state by mutableStateOf(AvaState(route=Route.Home))
        render({state},AvaActions(navigate={state=state.copy(route=it)}),width=320,height=560,font=1.5f)
        listOf("باغ","قصه‌ها","مرور","من").forEach {ui.onNodeWithText(it).assertIsDisplayed()}
        ui.onNodeWithText("قصه‌ها").performClick();assertEquals(Route.Library,state.route)
        listOf("باغ","قصه‌ها","مرور","من").forEach {ui.onNodeWithText(it).assertIsDisplayed()}
        snapshot("root-tabs-320-font150")
    }
    @Test fun homeScrollPositionSurvivesRootTabRoundTrip() {
        var state by mutableStateOf(AvaState(route=Route.Home,learner=Learner(signedIn=true)))
        render({state},AvaActions(navigate={state=state.copy(route=it)}))
        ui.onNodeWithText("یک قصه هم بخوانیم؟").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("من").performClick()
        ui.onNodeWithText("باغ").performClick()
        ui.onNodeWithText("یک قصه هم بخوانیم؟").assertIsDisplayed()
        snapshot("home-restored-position")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun storyControlsStayVisibleWhileLargeTextContentScrolls() {
        var state by mutableStateOf(AvaState(route=Route.StoryReader))
        render({state},AvaActions(page={next->state=state.copy(storyPage=(state.storyPage+if(next)1 else -1).coerceIn(0,state.story.pages.lastIndex))}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("صفحهٔ بعد").assertIsDisplayed()
        ui.onNodeWithText(state.story.pages[0]).performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("صفحهٔ بعد").assertIsDisplayed().performClick()
        assertEquals(1,state.storyPage)
        ui.onNodeWithText("صفحهٔ قبل").assertIsDisplayed().assertIsEnabled()
        snapshot("story-reader-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun profileLargeTextRetainsReadableNameAndSettingsAccess() {
        var state by mutableStateOf(AvaState(route=Route.Profile,learner=Learner(name="دوست مهربان کتاب‌های آوا")))
        render({state},AvaActions(navigate={state=state.copy(route=it)}),width=320,height=560,font=1.3f)
        ui.onNodeWithText(state.learner.name).performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("نام و دوست من").performScrollTo().assertIsDisplayed()
        snapshot("profile-320-large")
        ui.onNodeWithText("تنظیمات").performScrollTo().performClick()
        assertEquals(Route.Settings,state.route)
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun avatarAndSaveRemainUsableWithLargeText() {
        var state by mutableStateOf(AvaState(route=Route.EditProfile,profileName="مینا"))
        var saved=false
        render({state},AvaActions(avatar={state=state.copy(profileAvatar=it)},saveProfile={saved=true}),width=320,height=560,font=1.3f)
        val radio=SemanticsMatcher.expectValue(SemanticsProperties.Role,Role.RadioButton)
        ui.onAllNodes(radio).assertCountEquals(4)
        ui.onNode(hasText("روباه") and radio).performScrollTo().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick().assertIsSelected()
        ui.onNodeWithText("ذخیره").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(saved);snapshot("edit-profile-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun profileAndProgressIgnoreUnknownCatalogIds() {
        val learner=Learner(name="مینا",completed=setOf(Catalog.lessons[0].id,"removed"),stories=setOf(Catalog.stories[0].id,"removed"),reviews=2)
        var state by mutableStateOf(AvaState(route=Route.Profile,learner=learner))
        render({state},AvaActions(navigate={state=state.copy(route=it)}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("۱ از ۶ مرحلهٔ باغ کامل شده").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("پیشرفت من").performScrollTo().performClick()
        ui.onNodeWithText("۱ از ۶").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("۱ از ۲").performScrollTo().assertIsDisplayed()
        snapshot("progress-validated-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun completeProgressOffersStoriesAndRealBadgeCount() {
        var destination:Route?=null
        val learner=Learner(completed=Catalog.lessons.map {it.id}.toSet(),stories=setOf(Catalog.stories.first().id),reviews=1)
        render({AvaState(route=Route.Progress,learner=learner)},AvaActions(navigate={destination=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("همهٔ مرحله‌های این باغ کامل شده‌اند؛ قصه‌ها منتظرند.").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("۶ از ۶ یادگاری به دست آمده").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("قصه‌ها").performScrollTo().performClick();assertEquals(Route.Library,destination)
        snapshot("progress-complete-320-large")
    }
    @Test fun privacyAndTermsExposeDevelopmentLimits() {
        var state by mutableStateOf(AvaState(route=Route.Privacy))
        render({state})
        ui.onNodeWithText("میکروفن استفاده نمی‌شود",substring=true).performScrollTo().assertExists()
        ui.runOnIdle {state=state.copy(route=Route.Terms)}
        ui.onNodeWithText("جای برنامهٔ درسی تأییدشده",substring=true).performScrollTo().assertExists()
        ui.onNodeWithText("بازبینی حقوقی نیاز دارد",substring=true).assertExists()
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun libraryCardsReflowAndOpenCorrectStoryWithLargeText() {
        var opened=""
        render({AvaState(route=Route.Library)},AvaActions(openStory={opened=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("کتابِ دوستان").performScrollTo().assertIsDisplayed().performClick()
        assertEquals("friends_book",opened);snapshot("library-320-large")
    }

    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun lessonFooterSupportsRetryAndNewTaskStartsAtModel() {
        var state by mutableStateOf(AvaState(route=Route.Lesson,learner=Learner(narration=false)))
        val a=AvaActions(choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},revealHint={state=state.copy(exercise=ExerciseReducer.revealHint(state.exercise,state.tasks))},check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))},advance={state=state.copy(exercise=ExerciseReducer.advance(state.exercise,state.tasks))})
        render({state},a,width=320,height=560,font=1.3f)
        ui.onNodeWithText("بررسی کن").assertIsDisplayed().assertIsNotEnabled()
        ui.onNodeWithText("شنیدن").assertIsNotEnabled()
        ui.onNodeWithText("ا").performScrollTo().performClick().assertIsSelected()
        ui.onNodeWithText("بررسی کن").assertIsDisplayed().performClick()
        ui.onNodeWithText("اشکالی ندارد؛ دوباره به نمونه نگاه کن.").assertIsDisplayed()
        snapshot("lesson-320-large-retry")
        ui.onAllNodesWithText("آ")[1].performScrollTo().performClick().assertIsSelected()
        ui.onNodeWithText("بررسی کن").performClick()
        ui.onNodeWithText("ادامه").assertIsDisplayed().performClick()
        assertEquals(1,state.exercise.index)
        ui.onNodeWithText("گوش کن و نشانه را پیدا کن.").assertIsDisplayed()
        ui.onNodeWithText("اگر صدا آماده نیست، راهنمای نوشتاری را ببین.").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("نمایش راهنمای نوشتاری").performScrollTo().performClick()
        ui.onNodeWithText("راهنمای نوشتاری").assertIsDisplayed()
        ui.onNodeWithText("بررسی کن").assertIsNotEnabled()
        snapshot("lesson-320-large-next")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun wordBuilderUsesLargeTapTargetsAndSupportsCorrection() {
        var state by mutableStateOf(AvaState(route=Route.Lesson,selectedLesson=Catalog.lessons[2].id))
        val a=AvaActions(chooseToken={state=state.copy(exercise=ExerciseReducer.chooseToken(state.exercise,it,state.tasks))},removeToken={state=state.copy(exercise=ExerciseReducer.removeToken(state.exercise,state.tasks))},check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))})
        render({state},a,width=320,height=560,font=1.3f)
        ui.onNodeWithText("بررسی کن").assertIsNotEnabled()
        ui.onNodeWithText("ب").performScrollTo().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        ui.onNodeWithText("آ").performScrollTo().assertHeightIsAtLeast(48.dp).performClick()
        ui.onNodeWithText("بررسی کن").assertIsEnabled().performClick()
        ui.onNodeWithText("اشکالی ندارد؛ یک بار دیگر نشانه‌ها را بچین.").assertIsDisplayed()
        repeat(2) {ui.onNodeWithText("آخرین نشانه را بردار").performClick()}
        ui.onNodeWithText("آ").performClick();ui.onNodeWithText("ب").performClick()
        ui.onNodeWithText("بررسی کن").performClick()
        ui.onNodeWithText("پیدایش کردی!").assertIsDisplayed()
        snapshot("word-lesson-320-large")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun lessonIntroductionExplainsResumeAndPreviewsContent() {
        val first=Catalog.lessons[0].id;var started=false
        render({AvaState(route=Route.LessonDetails,selectedLesson=first,learner=Learner(activeLesson=first,activeIndex=1))},AvaActions(startLesson={started=true}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("ادامه از بازی ۲ از ۳").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("ادامهٔ تمرین").performScrollTo().performClick();assertTrue(started)
        snapshot("lesson-intro-resume-320")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun completedLessonWithStaleActiveIndexOffersReplayFromStart() {
        val first=Catalog.lessons.first().id
        render({AvaState(route=Route.LessonDetails,selectedLesson=first,learner=Learner(completed=setOf(first),activeLesson=first,activeIndex=2))},width=320,height=560,font=1.3f)
        ui.onNodeWithText("دوباره تمرین کن").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("ادامه از بازی ۳ از ۳").assertDoesNotExist()
        snapshot("lesson-details-stale-replay-320")
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun allLessonsCompleteOffersStoriesAndRealActivityCount() {
        var destination:Route?=null
        render({AvaState(route=Route.Lesson,selectedLesson=Catalog.lessons.last().id,exercise=Exercise(index=2,complete=true),learner=Learner(completed=Catalog.lessons.map {it.id}.toSet()))},AvaActions(navigate={destination=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("۳ بازی را تا پایان انجام دادی.").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("قصه‌ها").performScrollTo().performClick();assertEquals(Route.Library,destination)
        snapshot("lesson-all-complete-320")
    }
    @Test fun obsoleteReviewKeysDoNotCreateDeadStartButtons() {
        render({AvaState(route=Route.Practice,learner=Learner(completed=setOf("removed"),mistakes=setOf("removed:0")))})
        ui.onNodeWithText("بعد از اولین تمرین، اینجا هم بازی خواهیم داشت.").assertExists()
        ui.onNodeWithText("شروع تمرین").assertIsDisplayed()
        ui.onNodeWithText("تمرین دوباره").assertDoesNotExist()
    }
    @Config(qualifiers="w320dp-h560dp-mdpi")
    @Test fun practiceShowsShortMixedSessionAndDistinctReviewActions() {
        var mode:ReviewMode?=null
        render({AvaState(route=Route.Practice,learner=Learner(completed=setOf(Catalog.lessons[0].id),mistakes=setOf(taskKey(Catalog.lessons[0].id,0))))},AvaActions(review={mode=it}),width=320,height=560,font=1.3f)
        ui.onNodeWithText("این نوبت ۳ بازی دارد.").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("۲ دیدن").assertExists();ui.onNodeWithText("۱ شنیدن").assertExists()
        ui.onNodeWithText("شروع مرور کوتاه").performScrollTo().performClick();assertEquals(ReviewMode.Mix,mode)
        ui.onNodeWithText("مرور درس‌ها").performScrollTo().performClick();assertEquals(ReviewMode.Learned,mode)
        ui.onNodeWithText("تمرین دوباره").performScrollTo().performClick();assertEquals(ReviewMode.Mistakes,mode)
        snapshot("practice-320-large")
    }
    @Test fun lastCorrectAnswerRequiresExplicitFinishAction() {
        var state by mutableStateOf(AvaState(route=Route.Lesson,exercise=Exercise(index=2)))
        val a=AvaActions(choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))},advance={state=state.copy(exercise=ExerciseReducer.advance(state.exercise,state.tasks))})
        render({state},a)
        ui.onAllNodesWithText(state.tasks[2].target)[1].performScrollTo().performClick()
        ui.onNodeWithText("بررسی کن").performClick()
        assertFalse(state.exercise.complete)
        ui.onNodeWithText("پایان تمرین").assertIsDisplayed();snapshot("lesson-last-step")
        ui.onNodeWithText("پایان تمرین").performClick();assertTrue(state.exercise.complete)
        ui.onNodeWithText("یک قدم تازه برداشتی!").assertExists()
    }

    @Test fun newActivityKindsReuseStableChoiceAndTokenRenderers() {
        fun content(task: Task): ContentSnapshot {
            val first=Catalog.levels.first();val firstUnit=first.units.first();val lesson=firstUnit.lessons.first().copy(tasks=listOf(task))
            return Catalog.snapshot.copy(levels=listOf(first.copy(units=listOf(firstUnit.copy(lessons=listOf(lesson))))))
        }
        val visual=Task("dot_ui",TaskKind.DotFamily,"پ",choices=listOf("ب","پ","ت"),audioRef=AudioRef("task.dot_ui.prompt"))
        var state by mutableStateOf(AvaState(route=Route.Lesson,content=content(visual),contentReady=true,selectedLesson=Catalog.lessons.first().id))
        val actions=AvaActions(
            choose={state=state.copy(exercise=ExerciseReducer.choose(state.exercise,it,state.tasks))},
            chooseToken={state=state.copy(exercise=ExerciseReducer.chooseToken(state.exercise,it,state.tasks))},
            check={state=state.copy(exercise=ExerciseReducer.check(state.exercise,state.tasks))}
        )
        render({state},actions)
        ui.onNodeWithText("بررسی کن").assertIsNotEnabled()
        ui.onAllNodesWithText("پ")[1].performScrollTo().performClick();ui.onNodeWithText("بررسی کن").performClick()
        ui.onNodeWithText("پیدایش کردی!").assertIsDisplayed()

        val segment=Task("segment_ui",TaskKind.Segment,"باد",tokens=listOf("د","ب","ا"))
        ui.runOnIdle {state=state.copy(content=content(segment),exercise=Exercise())}
        ui.onNodeWithText("ب").performScrollTo().performClick();ui.onNodeWithText("ا").performClick();ui.onNodeWithText("د").performClick()
        ui.onNodeWithText("بررسی کن").assertIsEnabled().performClick()
        ui.onNodeWithText("پیدایش کردی!").assertIsDisplayed();snapshot("activity-engine-new-kinds")
    }

    @Test fun initialSoundUsesTheWordAsAudioFallbackAndWrittenHint() {
        val lesson=Catalog.lessons.first {it.id=="edge_sounds_03"};var played:AudioRequest?=null
        var state by mutableStateOf(AvaState(route=Route.Lesson,selectedLesson=lesson.id,learner=Learner(narration=true)))
        val actions=AvaActions(
            listen={played=it},
            revealHint={state=state.copy(exercise=ExerciseReducer.revealHint(state.exercise,state.tasks))}
        )
        render({state},actions)
        ui.onNodeWithText("شنیدن").performScrollTo().performClick()
        assertEquals("بابا",played?.fallbackText);assertEquals(AudioRef("task.initial_baba.prompt"),played?.audioRef)
        ui.onNodeWithText("نمایش راهنمای نوشتاری").performScrollTo().performClick()
        ui.onNodeWithText("بابا").assertIsDisplayed();snapshot("initial-sound-word-fallback")
    }

}
