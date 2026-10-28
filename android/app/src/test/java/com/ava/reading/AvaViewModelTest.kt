package com.ava.reading

import androidx.lifecycle.*
import com.ava.reading.content.*
import com.ava.reading.data.*
import com.ava.reading.storage.*
import com.ava.reading.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainRule : TestWatcher() {
    val dispatcher=StandardTestDispatcher()
    override fun starting(description: Description) {Dispatchers.setMain(dispatcher)}
    override fun finished(description: Description) {Dispatchers.resetMain()}
}
@OptIn(ExperimentalCoroutinesApi::class)
class AvaViewModelTest {
    @get:Rule val main=MainRule()
    private val store=ViewModelStore()
    @After fun close() {store.clear()}
    private fun vm(repo: LearnerRepository=MemoryLearnerRepository(),progressClock:()->Long={1234L},contentRepository:ContentRepository=LocalContentRepository()): AvaViewModel = AvaViewModel(auth=MockAuthRepository(latency=0),repository=repo,progressClock=progressClock,contentRepository=contentRepository).also {store.put("vm",it)}
    private fun answer(v: AvaViewModel) {
        val s=v.state.value;val task=s.tasks[s.exercise.index]
        if(ActivityRegistry[task.kind].interaction==ActivityInteraction.TokenBuild) task.target.forEach {letter->
            val used=v.state.value.exercise.selectedTokens
            v.chooseToken(task.tokens.indices.first {index->index !in used && task.tokens[index]==letter.toString()})
        } else v.choose(task.target)
    }
    private fun complete(v: AvaViewModel) {while(!v.state.value.exercise.complete) {answer(v);v.check();v.advance()}}
    @Test fun startupRespectsReturningSession()=runTest {val v=vm(MemoryLearnerRepository(Learner(signedIn=true)));advanceTimeBy(1801);runCurrent();assertEquals(Route.Home,v.state.value.route)}
    @Test fun startupReadsTheActiveLocalSnapshotInsteadOfStaticUiCatalog()=runTest {
        val first=Catalog.levels.first();val unit=first.units.first();val lesson=unit.lessons.first().copy(title="عنوان نسخهٔ محلی")
        val levels=listOf(first.copy(units=listOf(unit.copy(lessons=listOf(lesson)))+first.units.drop(1)))
        val snapshot=Catalog.snapshot.copy(release=Catalog.release.copy(releaseId="local-ui-0.5.1",status=ContentReleaseStatus.Staged),levels=levels)
        val content=LocalContentRepository();assertTrue(content.activate(snapshot) is ContentActivationResult.Activated)
        val v=vm(contentRepository=content);runCurrent()
        assertTrue(v.state.value.contentReady);assertEquals("local-ui-0.5.1",v.state.value.content.release.releaseId)
        assertEquals("عنوان نسخهٔ محلی",v.state.value.lessons.first().title)
    }
    @Test fun newUserStartsOnboardingAndSkipPersists()=runTest {val repo=MemoryLearnerRepository();val v=vm(repo);advanceTimeBy(1801);runCurrent();assertEquals(Route.Onboarding1,v.state.value.route);v.navigate(Route.Phone);runCurrent();assertTrue(repo.data.value.onboarded)}
    @Test fun phoneConsentAndSingleAutoVerificationFlow()=runTest {
        val v=vm();runCurrent();v.navigate(Route.Phone);v.phone("۰۹۱۲۳۴۵۶۷۸۹");v.send();assertEquals("phone_consent_hint",v.state.value.message)
        v.consent(true);v.send();runCurrent();assertEquals(Route.Otp,v.state.value.route);v.code("۱۲۳۴۵۶");runCurrent();advanceTimeBy(601);runCurrent();assertEquals(Route.Home,v.state.value.route);assertTrue(v.state.value.learner.signedIn)
    }
    @Test fun leavingDuringRequestCancelsNavigation()=runTest {val v=vm();runCurrent();v.navigate(Route.Phone);v.phone("09123456789");v.consent(true);v.send();v.navigate(Route.Onboarding1);runCurrent();assertEquals(Route.Onboarding1,v.state.value.route)}
    @Test fun lockedLessonCannotOpen()=runTest {val v=vm();runCurrent();v.navigate(Route.Home);v.openLesson(Catalog.lessons[1].id);assertEquals(Route.Home,v.state.value.route)}
    @Test fun completionUnlocksNextAndIsIdempotent()=runTest {val v=vm();runCurrent();v.continueLearning();complete(v);v.advance();runCurrent();assertEquals(setOf(Catalog.lessons[0].id),v.state.value.learner.completed);v.continueLearning();assertEquals(Catalog.lessons[1].id,v.state.value.selectedLesson)}
    @Test fun staleActiveLessonCannotOverrideTheNextPathStepOrReplayPosition()=runTest {
        val first=Catalog.lessons[0].id
        val v=vm(MemoryLearnerRepository(Learner(completed=setOf(first),activeLesson=first,activeIndex=2)));runCurrent()
        v.continueLearning()
        assertEquals(Catalog.lessons[1].id,v.state.value.selectedLesson);assertEquals(0,v.state.value.exercise.index)
        v.navigate(Route.Home);v.openLesson(first);v.startLesson()
        assertEquals(first,v.state.value.selectedLesson);assertEquals(0,v.state.value.exercise.index)
    }
    @Test fun continueAfterTheWholePathMovesToStories()=runTest {
        val v=vm(MemoryLearnerRepository(Learner(completed=Catalog.lessons.map {it.id}.toSet(),activeLesson=Catalog.lessons.last().id,activeIndex=2)));runCurrent()
        v.continueLearning();assertEquals(Route.Library,v.state.value.route)
    }
    @Test fun mistakeReviewUsesCurriculumOrderAndDropsRemovedKeys()=runTest {
        val earlier=taskKey(Catalog.lessons[0].id,2);val later=taskKey(Catalog.lessons[2].id,1)
        val v=vm(MemoryLearnerRepository(Learner(mistakes=setOf(later,"removed:0",earlier))));runCurrent()
        v.review(ReviewMode.Mistakes);assertEquals(listOf(earlier,later),v.state.value.reviewKeys)
    }
    @Test fun lessonExitConfirmsAndResumesCompletedStep()=runTest {
        val repo=MemoryLearnerRepository();val v=vm(repo);runCurrent();v.continueLearning();v.choose(v.state.value.tasks[0].target);v.check();v.advance();assertTrue(v.back());assertEquals(Confirmation.ExitLesson,v.state.value.confirmation)
        v.confirmAction();runCurrent();assertEquals(Route.Home,v.state.value.route);v.continueLearning();assertEquals(1,v.state.value.exercise.index);assertEquals(1,repo.data.value.activeIndex)
    }
    @Test fun inProgressAnswerAndHintSurviveViewModelRecreation()=runTest {
        val saved=SavedStateHandle();val repo=MemoryLearnerRepository()
        val first=AvaViewModel(saved,MockAuthRepository(latency=0),repo,contentRepository=LocalContentRepository())
        store.put("session-first",first);runCurrent();first.continueLearning()
        first.choose(first.state.value.tasks[0].target);first.check();first.advance()
        first.revealHint();first.choose(first.state.value.tasks[1].target)
        val expected=first.state.value.exercise

        val recreated=AvaViewModel(saved,MockAuthRepository(latency=0),repo,contentRepository=LocalContentRepository())
        store.put("session-recreated",recreated);runCurrent();recreated.continueLearning()
        assertEquals(expected,recreated.state.value.exercise)
        assertEquals(expected.index,recreated.state.value.learner.activeIndex)
    }
    @Test fun mistakeReviewClearsCorrectedTaskAndCountsOnce()=runTest {
        val v=vm();runCurrent();v.continueLearning();val t=v.state.value.tasks[0];v.choose(t.choices.first {it!=t.target});v.check();assertEquals(1,v.state.value.learner.mistakes.size)
        v.navigate(Route.Practice);v.review(ReviewMode.Mistakes);complete(v);v.advance();runCurrent();assertTrue(v.state.value.learner.mistakes.isEmpty());assertEquals(1,v.state.value.learner.reviews)
    }
    @Test fun eachCheckRecordsVersionedTaskProgressWithInjectedTime()=runTest {
        val v=vm(progressClock={9876L});runCurrent();v.continueLearning()
        val task=v.state.value.tasks.first();val key=taskKey(v.state.value.selectedLesson,0)
        v.choose(task.choices.first {it!=task.target});v.check();runCurrent()
        assertEquals(TaskProgress(1,0,0,9876),v.state.value.learner.taskProgress[key])
        v.choose(task.target);v.check();runCurrent()
        assertEquals(TaskProgress(2,1,0,9876),v.state.value.learner.taskProgress[key])
    }
    @Test fun wordBuilderAndListeningHintFlowThroughViewModel()=runTest {
        val v=vm();runCurrent();v.navigate(Route.Home);v.openLesson(Catalog.lessons[0].id);v.startLesson()
        answer(v);v.check();v.advance();assertEquals(TaskKind.Listen,v.state.value.tasks[v.state.value.exercise.index].kind)
        v.revealHint();assertTrue(v.state.value.exercise.hintVisible);answer(v);v.check();v.advance()
        v.navigate(Route.Home);v.openLesson(Catalog.lessons[0].id);v.startLesson();complete(v);runCurrent()
        val buildLesson=Catalog.lessons.first {lesson->lesson.tasks.first().kind==TaskKind.BuildWord}
        Catalog.lessons.drop(1).takeWhile {it.id!=buildLesson.id}.forEach {lesson->
            v.openLesson(lesson.id);v.startLesson();complete(v);runCurrent()
        }
        v.openLesson(buildLesson.id);v.startLesson();assertEquals(TaskKind.BuildWord,v.state.value.tasks.first().kind)
        v.chooseToken(0);assertFalse(ExerciseReducer.canCheck(v.state.value.exercise,v.state.value.tasks));v.removeToken();assertTrue(v.state.value.exercise.selectedTokens.isEmpty())
        answer(v);v.check();assertTrue(v.state.value.exercise.correct)
    }
    @Test fun mixedReviewIsShortPrioritizesMistakesAndRotatesNextSession()=runTest {
        val completed=Catalog.lessons.map {it.id}.toSet();val mistake=taskKey(Catalog.lessons[0].id,1)
        val v=vm(MemoryLearnerRepository(Learner(completed=completed,mistakes=setOf(mistake))));runCurrent()
        v.navigate(Route.Practice);v.review(ReviewMode.Mix)
        assertEquals(plannedReviewKeys(ReviewMode.Mix,completed,setOf(mistake),0),v.state.value.reviewKeys)
        assertEquals(5,v.state.value.tasks.size);assertEquals(mistake,v.state.value.reviewKeys.first())
        complete(v);runCurrent();assertEquals(1,v.state.value.learner.reviews);assertTrue(v.state.value.learner.mistakes.isEmpty())
        v.navigate(Route.Practice);v.review(ReviewMode.Learned)
        assertEquals(plannedReviewKeys(ReviewMode.Learned,completed,emptySet(),1),v.state.value.reviewKeys)
    }
    @Test fun storyPositionAndQuestionAreRecoverableAndUnique()=runTest {
        val v=vm();runCurrent();v.openStory(Catalog.stories[0].id);v.startStory();v.page(true);v.navigate(Route.Library);v.openStory(Catalog.stories[0].id);v.startStory();assertEquals(1,v.state.value.storyPage)
        repeat(v.state.value.story.pages.size-1) {v.page(true)};assertEquals(Route.StoryQuestion,v.state.value.route)
        v.storyAnswer("توپ");v.checkStory();v.finishStory();assertEquals(Route.StoryQuestion,v.state.value.route)
        v.storyAnswer(v.state.value.story.answer);v.checkStory();v.checkStory();assertTrue(v.state.value.learner.stories.isEmpty())
        v.finishStory();runCurrent();assertEquals(Route.StoryComplete,v.state.value.route);assertEquals(1,v.state.value.learner.stories.size)
    }
    @Test fun resetPreservesIdentityPreferencesAndLogoutPreservesStudy()=runTest {
        val v=vm(MemoryLearnerRepository(Learner(signedIn=true,name="مینا",avatar=2,completed=setOf(Catalog.lessons[0].id),sounds=false)));runCurrent()
        v.confirm(Confirmation.Logout);v.confirmAction();runCurrent();assertFalse(v.state.value.learner.signedIn);assertTrue(v.state.value.learner.completed.isNotEmpty())
        v.confirm(Confirmation.ResetProgress);v.confirmAction();runCurrent();assertTrue(v.state.value.learner.completed.isEmpty());assertEquals("مینا",v.state.value.learner.name);assertEquals(2,v.state.value.learner.avatar);assertFalse(v.state.value.learner.sounds)
    }
    @Test fun profileSanitizesAndClampsInputs()=runTest {val v=vm();runCurrent();v.navigate(Route.EditProfile);v.editName("مینا\n"+"آ".repeat(30));v.avatar(99);v.saveProfile();runCurrent();assertEquals(24,v.state.value.learner.name.length);assertFalse(v.state.value.learner.name.contains('\n'));assertEquals(3,v.state.value.learner.avatar)}
    @Test fun failedReadStaysVisibleUntilSuccessfulRetry()=runTest {
        var broken=true
        val repo=object: LearnerRepository {override val data: Flow<Learner> get()=flow {if(broken)error("disk");emit(Learner(signedIn=true))};override suspend fun update(transform:(Learner)->Learner) {}}
        val v=vm(repo);advanceTimeBy(1801);runCurrent();assertEquals(Route.Splash,v.state.value.route);assertTrue(v.state.value.storageError)
        broken=false;v.retryStorage();runCurrent();assertEquals(Route.Home,v.state.value.route);assertFalse(v.state.value.storageError)
    }
    @Test fun failedWriteCanBeRetriedWithoutLosingOptimisticState()=runTest {
        val base=MemoryLearnerRepository();var broken=true
        val repo=object:LearnerRepository {override val data=base.data;override suspend fun update(transform:(Learner)->Learner) {if(broken)error("disk");base.update(transform)}}
        val v=vm(repo);runCurrent();v.setting("motion",true);runCurrent();assertTrue(v.state.value.storageError);assertTrue(v.state.value.learner.reducedMotion)
        broken=false;v.retryStorage();runCurrent();assertFalse(v.state.value.storageError);assertTrue(base.data.value.reducedMotion)
    }
    @Test fun questionBackReturnsToLastReadPageWithoutStaleAnswer()=runTest {
        val v=vm();runCurrent();v.openStory(Catalog.stories[0].id);v.startStory()
        repeat(v.state.value.story.pages.size) {v.page(true)}
        v.storyAnswer("توپ");v.checkStory();assertTrue(v.state.value.storyChecked)
        assertTrue(v.back());assertEquals(Route.StoryReader,v.state.value.route)
        assertEquals(v.state.value.story.pages.lastIndex,v.state.value.storyPage)
        assertNull(v.state.value.storyAnswer);assertFalse(v.state.value.storyChecked)
        v.page(true);assertEquals(Route.StoryQuestion,v.state.value.route)
        assertNull(v.state.value.storyAnswer)
    }

    @Test fun secondaryDestinationsHavePredictableBackTargets()=runTest {
        val v=vm();runCurrent()
        val cases=linkedMapOf(
            Route.Onboarding2 to Route.Onboarding1,Route.Otp to Route.Phone,
            Route.LessonDetails to Route.Home,Route.Library to Route.Home,Route.Practice to Route.Home,Route.Profile to Route.Home,
            Route.StoryDetails to Route.Library,Route.StoryReader to Route.StoryDetails,Route.StoryComplete to Route.Library,
            Route.EditProfile to Route.Profile,Route.Progress to Route.Profile,Route.Settings to Route.Profile,Route.Help to Route.Profile,Route.About to Route.Profile,
            Route.Privacy to Route.Settings,Route.Terms to Route.Settings
        )
        cases.forEach {(from,to)->v.navigate(from);assertTrue("$from should handle back",v.back());assertEquals("$from parent",to,v.state.value.route)}
        v.navigate(Route.StoryQuestion);v.storyAnswer(v.state.value.story.choices.first());v.checkStory()
        assertTrue(v.back());assertEquals(Route.StoryReader,v.state.value.route);assertNull(v.state.value.storyAnswer);assertFalse(v.state.value.storyChecked)
    }

    @Test fun startingFreshStoryPersistsOpenedStateAtFirstPage()=runTest {
        val repo=MemoryLearnerRepository();val v=vm(repo);runCurrent()
        v.openStory(Catalog.stories.first().id);v.startStory();runCurrent()
        assertEquals(0,v.state.value.learner.positions[Catalog.stories.first().id])
        assertEquals(0,repo.data.value.positions[Catalog.stories.first().id])
    }

    @Test fun correctStoryAnswerCannotBeChangedBeforeCompletion()=runTest {
        val v=vm();runCurrent();v.openStory(Catalog.stories.first().id);v.startStory()
        repeat(v.state.value.story.pages.size) {v.page(true)}
        v.storyAnswer(v.state.value.story.answer);v.checkStory();val correct=v.state.value.storyAnswer
        v.storyAnswer(v.state.value.story.choices.first {it!=correct})
        assertEquals(correct,v.state.value.storyAnswer);assertTrue(v.state.value.storyChecked)
        v.finishStory();assertEquals(Route.StoryComplete,v.state.value.route)
    }

    @Test fun rereadingCompletedStoryStartsFromFirstPage()=runTest {
        val id=Catalog.stories.first().id
        val v=vm(MemoryLearnerRepository(Learner(stories=setOf(id),positions=mapOf(id to 3))));runCurrent()
        v.openStory(id);v.startStory()
        assertEquals(0,v.state.value.storyPage)
    }

    @Test fun leavingCorrectButUnfinishedReviewDoesNotCountOrRemoveMistake()=runTest {
        val key=taskKey(Catalog.lessons[0].id,0)
        val v=vm(MemoryLearnerRepository(Learner(mistakes=setOf(key))));runCurrent()
        v.review(ReviewMode.Mistakes);v.choose(v.state.value.tasks[0].target);v.check()
        assertTrue(v.state.value.learner.mistakes.contains(key))
        v.back();v.confirmAction();runCurrent()
        assertEquals(Route.Practice,v.state.value.route);assertEquals(0,v.state.value.learner.reviews)
        assertTrue(v.state.value.learner.mistakes.contains(key))
        v.review(ReviewMode.Mistakes);v.choose(v.state.value.tasks[0].target);v.check();v.advance();v.advance();runCurrent()
        assertTrue(v.state.value.learner.mistakes.isEmpty());assertEquals(1,v.state.value.learner.reviews)
    }

}
