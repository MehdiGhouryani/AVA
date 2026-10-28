package com.ava.reading

import com.ava.reading.content.*
import org.junit.Assert.*
import org.junit.Test

class ModelTest {
    private fun answer(state: Exercise,tasks: List<Task>): Exercise {
        val task=tasks[state.index]
        return if(ActivityRegistry[task.kind].interaction==ActivityInteraction.TokenBuild) task.target.fold(state) {current,letter->
            val index=task.tokens.indices.first {it !in current.selectedTokens && task.tokens[it]==letter.toString()}
            ExerciseReducer.chooseToken(current,index,tasks)
        } else ExerciseReducer.choose(state,task.target,tasks)
    }
    @Test fun normalizesPersianArabicAndInternationalNumbers() {
        listOf("۰۹۱۲۳۴۵۶۷۸۹","٠٩١٢٣٤٥٦٧٨٩","9123456789","+98 912-345-6789","00989123456789").forEach {assertEquals("09123456789",normalizePhone(it))}
        assertFalse(validPhone("08123456789"));assertFalse(validPhone("0912"))
    }
    @Test fun catalogHasUniqueIdsAndValidTargets() {
        assertEquals(6,Catalog.lessons.size);assertEquals(2,Catalog.stories.size)
        assertEquals(17,Catalog.lessons.sumOf {it.tasks.size})
        val tasks=Catalog.lessons.flatMap {it.tasks}
        assertEquals(tasks.size,tasks.map {it.id}.distinct().size)
        assertEquals(3,tasks.count {it.kind==TaskKind.Recognize});assertEquals(4,tasks.count {it.kind==TaskKind.Listen});assertEquals(3,tasks.count {it.kind==TaskKind.BuildWord})
        assertEquals(2,tasks.count {it.kind==TaskKind.DotFamily});assertEquals(2,tasks.count {it.kind==TaskKind.InitialSound})
        assertEquals(1,tasks.count {it.kind==TaskKind.FinalSound});assertEquals(2,tasks.count {it.kind==TaskKind.Blend})
        tasks.forEach {
            if(ActivityRegistry[it.kind].interaction==ActivityInteraction.TokenBuild) {assertTrue(it.choices.isEmpty());assertEquals(it.target.toList().sorted(),it.tokens.joinToString("").toList().sorted())}
            else {assertEquals(3,it.choices.distinct().size);assertTrue(it.target in it.choices);assertTrue(it.tokens.isEmpty())}
        }
        assertEquals(9,Catalog.stories.sumOf {it.pages.size})
    }
    @Test fun contentV3HasStableHierarchyRefsAndCompatibilityContract() {
        assertEquals(3,Catalog.release.schemaVersion);assertEquals("0.6.1-dev.1",Catalog.release.contentVersion)
        assertEquals(1,Catalog.levels.size);assertEquals(3,Catalog.levels.single().units.size);assertEquals(5,Catalog.skills.size)
        val tasks=Catalog.lessons.flatMap {it.tasks}
        tasks.filter {ActivityRegistry[it.kind].family==ActivityFamily.Listening}.forEach {
            assertNotNull(it.audioRef);assertTrue(it.audioRequiredForPublication);assertFalse(it.writtenFallback.isNullOrBlank())
        }
        tasks.filter {ActivityRegistry[it.kind].interaction==ActivityInteraction.TokenBuild}.forEach {
            assertNull(it.audioRef);assertFalse(it.audioRequiredForPublication);assertNull(it.writtenFallback)
        }
        val pages=Catalog.stories.flatMap {it.pageItems }
        assertEquals(pages.size,pages.map {it.id}.distinct().size);assertTrue(pages.all {it.audioRef.id.startsWith("story.")})
        assertTrue(Catalog.release.supports("0.6.1"));assertTrue(Catalog.release.supports("1.0.0"));assertFalse(Catalog.release.supports("0.6.0"))
        assertFalse(Catalog.release.publishable())
    }
    @Test fun repeatedBuildTokensRemainDistinctAndCorrectable() {
        val task=Task("build_bab",TaskKind.BuildWord,"باب",tokens=listOf("ب","ا","ب"),skillIds=listOf("word_building"))
        val tasks=listOf(task)
        val state=listOf(0,1,2).fold(Exercise()) {current,index->ExerciseReducer.chooseToken(current,index,tasks)}
        assertEquals("باب",ExerciseReducer.response(state,tasks));assertTrue(ExerciseReducer.check(state,tasks).correct)
    }
    @Test fun freshPathCannotSkip() {assertTrue(unlocked(Catalog.lessons[0].id,emptySet()));assertFalse(unlocked(Catalog.lessons[1].id,emptySet()));assertFalse(unlocked("missing",emptySet()))}
    @Test fun completedPathUnlocksNextAndAllowsReplay() {val done=setOf(Catalog.lessons[0].id);assertTrue(unlocked(Catalog.lessons[1].id,done));assertTrue(unlocked(Catalog.lessons[0].id,done));assertEquals(Catalog.lessons[1],nextLesson(done))}
    @Test fun corruptedOutOfOrderProgressCannotUnlockLaterLessons() {
        val third=Catalog.lessons[2].id
        assertTrue(unlocked(third,setOf(third)))
        assertFalse(unlocked(Catalog.lessons[3].id,setOf(third)))
        assertFalse(unlocked(Catalog.lessons[2].id,setOf(Catalog.lessons[0].id)))
    }
    @Test fun catalogDerivedProgressIgnoresRemovedIdsAndOrdersReviewTasks() {
        assertEquals(1,knownLessonCount(setOf(Catalog.lessons[0].id,"removed")))
        assertEquals(1,knownStoryCount(setOf(Catalog.stories[0].id,"removed")))
        assertNull(resumableLesson(Catalog.lessons[0].id,setOf(Catalog.lessons[0].id)))
        val later=taskKey(Catalog.lessons[2].id,1);val earlierLegacy="${Catalog.lessons[0].id}:2"
        assertEquals(listOf(earlierLegacy,later),orderedTaskKeys(setOf(later,"removed:0",earlierLegacy)))
    }
    @Test fun stableAndLegacyTaskKeysResolveSafely() {
        val lesson=Catalog.lessons[0];val stable=taskKey(lesson.id,1);val legacy="${lesson.id}:1"
        assertTrue(stable.endsWith(lesson.tasks[1].id));assertEquals(lesson.tasks[1],taskForKey(stable));assertEquals(lesson.tasks[1],taskForKey(legacy))
        assertEquals(setOf(stable,legacy),taskAliases(stable));assertNull(taskForKey("missing:0"));assertNull(taskForKey("visual_shapes_01:-1"))
    }
    @Test fun shortReviewPlanPrioritizesMistakesAddsVarietyAndRotatesLearnedWork() {
        val completed=Catalog.lessons.map {it.id}.toSet()
        val recognize=taskKey(Catalog.lessons[0].id,0)
        val listen=taskKey(Catalog.lessons[0].id,1)
        val mix=plannedReviewKeys(ReviewMode.Mix,completed,setOf(listen,recognize,"removed:0"),0)
        assertEquals(listOf(recognize,listen),mix.take(2));assertEquals(5,mix.size)
        assertEquals(ActivityFamily.entries.toSet(),mix.mapNotNull(::taskForKey).map {ActivityRegistry[it.kind].family}.toSet())
        val first=plannedReviewKeys(ReviewMode.Learned,completed,emptySet(),0)
        val second=plannedReviewKeys(ReviewMode.Learned,completed,emptySet(),1)
        assertEquals(first.drop(1),second.take(4));assertNotEquals(first.first(),second.first())
        assertTrue(plannedReviewKeys(ReviewMode.Mix,completed,emptySet(),0,0).isEmpty())
    }
    @Test fun noSelectionOrWrongAnswerCannotAdvance() {val tasks=Catalog.lessons[0].tasks;val initial=Exercise();assertEquals(initial,ExerciseReducer.check(initial,tasks));assertEquals(initial,ExerciseReducer.advance(initial,tasks));assertEquals(initial,ExerciseReducer.choose(initial,"bad",tasks))}
    @Test fun wrongAnswerCanBeCorrected() {
        val tasks=Catalog.lessons[0].tasks
        val wrong=ExerciseReducer.check(ExerciseReducer.choose(Exercise(),tasks[0].choices.first {it!=tasks[0].target},tasks),tasks)
        assertTrue(wrong.checked);assertFalse(wrong.correct);assertEquals(wrong,ExerciseReducer.advance(wrong,tasks))
        val right=ExerciseReducer.check(ExerciseReducer.choose(wrong,tasks[0].target,tasks),tasks)
        assertTrue(right.correct);assertEquals(1,ExerciseReducer.advance(right,tasks).index)
    }
    @Test fun completionAndCorrectSelectionAreStable() {
        val tasks=Catalog.lessons[0].tasks;var state=Exercise()
        tasks.forEach {state=ExerciseReducer.check(answer(state,tasks),tasks);assertEquals(state,ExerciseReducer.choose(state,"bad",tasks));state=ExerciseReducer.advance(state,tasks)}
        assertTrue(state.complete);assertEquals(state,ExerciseReducer.advance(state,tasks))
    }
    @Test fun listeningHintIsOptionalAndAnswerRemainsStable() {
        val tasks=Catalog.lessons[0].tasks;val listen=Exercise(index=1)
        val hinted=ExerciseReducer.revealHint(listen,tasks);assertTrue(hinted.hintVisible);assertFalse(ExerciseReducer.canCheck(hinted,tasks))
        val chosen=ExerciseReducer.choose(hinted,tasks[1].target,tasks);assertTrue(chosen.hintVisible);assertTrue(ExerciseReducer.check(chosen,tasks).correct)
        val recognize=Exercise();assertEquals(recognize,ExerciseReducer.revealHint(recognize,tasks))
    }
    @Test fun wordBuilderRequiresEveryTokenAndSupportsUndoAndCorrection() {
        val tasks=Catalog.lessons[2].tasks;var state=Exercise()
        state=ExerciseReducer.chooseToken(state,0,tasks);assertFalse(ExerciseReducer.canCheck(state,tasks));assertEquals(state,ExerciseReducer.chooseToken(state,0,tasks))
        state=ExerciseReducer.chooseToken(state,1,tasks);assertTrue(ExerciseReducer.canCheck(state,tasks));state=ExerciseReducer.check(state,tasks);assertFalse(state.correct)
        state=ExerciseReducer.removeToken(state,tasks);state=ExerciseReducer.removeToken(state,tasks);assertTrue(state.selectedTokens.isEmpty())
        state=answer(state,tasks);assertEquals("آب",ExerciseReducer.response(state,tasks));assertTrue(ExerciseReducer.check(state,tasks).correct)
    }
}
