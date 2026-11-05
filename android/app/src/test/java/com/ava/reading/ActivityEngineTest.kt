package com.ava.reading

import com.ava.reading.content.*
import org.junit.Assert.*
import org.junit.Test

class ActivityEngineTest {
    private fun choice(kind: TaskKind)=Task(
        id="choice_${kind.name}",kind=kind,target="پ",choices=listOf("ب","پ","ت"),
        skillIds=listOf("grapheme_recognition"),audioRef=AudioRef("task.choice.prompt"),
        audioRequiredForPublication=ActivityRegistry[kind].family==ActivityFamily.Listening,writtenFallback="پ"
    )
    private fun tokens(kind: TaskKind)=Task(
        id="tokens_${kind.name}",kind=kind,target="باب",tokens=listOf("ب","ا","ب"),skillIds=listOf("word_building")
    )

    @Test fun registryCoversEveryTaskKindExactlyOnce() {
        assertEquals(TaskKind.entries,ActivityRegistry.all.map {it.kind})
        assertEquals(TaskKind.entries.size,ActivityRegistry.all.map {it.kind}.distinct().size)
        assertEquals(setOf(ActivityFamily.Visual,ActivityFamily.Listening,ActivityFamily.WordConstruction),ActivityRegistry.all.map {it.family}.toSet())
    }

    @Test fun everyChoiceActivityUsesTheSharedChoiceReducer() {
        ActivityRegistry.all.filter {it.interaction==ActivityInteraction.Choice}.forEach {definition->
            val task=choice(definition.kind);val selected=ExerciseReducer.choose(Exercise(),task.target,listOf(task))
            assertEquals(definition.kind.name,task.target,selected.selected)
            assertTrue(definition.kind.name,ExerciseReducer.check(selected,listOf(task)).correct)
            assertEquals(selected,ExerciseReducer.choose(selected,"نامعتبر",listOf(task)))
        }
    }

    @Test fun everyTokenActivitySupportsRepeatedTokensUndoAndCorrection() {
        ActivityRegistry.all.filter {it.interaction==ActivityInteraction.TokenBuild}.forEach {definition->
            val task=tokens(definition.kind);val tasks=listOf(task)
            var state=listOf(0,1,2).fold(Exercise()) {current,index->ExerciseReducer.chooseToken(current,index,tasks)}
            assertEquals(definition.kind.name,"باب",ExerciseReducer.response(state,tasks))
            state=ExerciseReducer.removeToken(state,tasks);assertEquals(listOf(0,1),state.selectedTokens)
            state=ExerciseReducer.chooseToken(state,2,tasks);assertTrue(ExerciseReducer.check(state,tasks).correct)
        }
    }

    private fun match()=Task(
        id="match_ui",kind=TaskKind.Match,target="آب=آ|باد=ب|گل=گ",tokens=listOf("آب","آ","باد","ب","گل","گ"),skillIds=listOf("word_building")
    )

    @Test fun findBubbleAndMatchAreRegisteredWithTheirOwnBehaviour() {
        val find=ActivityRegistry[TaskKind.Find];val bubble=ActivityRegistry[TaskKind.Bubble];val pairs=ActivityRegistry[TaskKind.Match]
        assertEquals(ActivityInteraction.Choice,find.interaction);assertEquals(ActivityFamily.Visual,find.family);assertFalse(find.supportsHint);assertTrue(find.supportsAudio)
        assertEquals(ActivityInteraction.Choice,bubble.interaction);assertEquals(ActivityFamily.Listening,bubble.family);assertTrue(bubble.supportsHint);assertTrue(bubble.supportsAudio)
        assertEquals(ActivityInteraction.PairMatch,pairs.interaction);assertEquals(ActivityFamily.Visual,pairs.family);assertFalse(pairs.supportsHint);assertFalse(pairs.supportsAudio)
    }

    @Test fun pairMatchMatchesCompletesAndNeverCountsMistakes() {
        val tasks=listOf(match());var state=Exercise()
        state=ExerciseReducer.flip(state,0,tasks);assertEquals(0,state.pending)
        assertEquals(state,ExerciseReducer.flip(state,0,tasks))
        state=ExerciseReducer.flip(state,2,tasks)
        assertEquals(0 to 2,state.mismatch);assertNull(state.pending);assertTrue(state.matched.isEmpty());assertEquals(0,state.wrongCount)
        val afterNewFlip=ExerciseReducer.flip(state,1,tasks);assertNull(afterNewFlip.mismatch);assertEquals(1,afterNewFlip.pending)
        assertNull(ExerciseReducer.clearMismatch(state).mismatch)
        state=ExerciseReducer.flip(ExerciseReducer.flip(ExerciseReducer.clearMismatch(state),0,tasks),1,tasks)
        assertEquals(setOf(0),state.matched);assertNull(state.pending);assertFalse(state.checked)
        assertEquals(state,ExerciseReducer.flip(state,1,tasks))
        state=ExerciseReducer.flip(ExerciseReducer.flip(state,2,tasks),3,tasks)
        assertFalse(state.checked)
        state=ExerciseReducer.flip(ExerciseReducer.flip(state,4,tasks),5,tasks)
        assertTrue(state.checked);assertTrue(state.correct);assertEquals(setOf(0,1,2),state.matched);assertEquals(0,state.wrongCount)
        assertTrue(ExerciseReducer.advance(state,tasks).complete)
    }

    @Test fun pairMatchAndOtherInteractionsDoNotLeakIntoEachOther() {
        val pairs=match();val choice=choice(TaskKind.Find)
        assertEquals(Exercise(),ExerciseReducer.flip(Exercise(),0,listOf(choice)))
        assertEquals(Exercise(),ExerciseReducer.flip(Exercise(),9,listOf(pairs)))
        assertEquals(Exercise(),ExerciseReducer.choose(Exercise(),"آب",listOf(pairs)))
        assertEquals(Exercise(),ExerciseReducer.chooseToken(Exercise(),0,listOf(pairs)))
        assertFalse(ExerciseReducer.canCheck(Exercise(),listOf(pairs)))
        assertEquals(Exercise(),ExerciseReducer.restore(Exercise().snapshot().encode(),listOf(pairs)))
    }

    @Test fun hintsBelongOnlyToListeningFamily() {
        TaskKind.entries.forEach {kind->
            val definition=ActivityRegistry[kind]
            val task=if(definition.interaction==ActivityInteraction.Choice)choice(kind) else tokens(kind)
            val hinted=ExerciseReducer.revealHint(Exercise(),listOf(task))
            assertEquals(kind.name,definition.family==ActivityFamily.Listening,hinted.hintVisible)
        }
    }

    @Test fun snapshotRoundTripsPersianChoiceAndRepeatedTokenIdentity() {
        val listening=choice(TaskKind.InitialSound)
        val chosen=Exercise(index=0,selected="پ",hintVisible=true,checked=true)
        assertEquals(chosen.copy(correct=true),ExerciseReducer.restore(chosen.snapshot().encode(),listOf(listening)))

        val build=tokens(TaskKind.Blend)
        val built=Exercise(selectedTokens=listOf(0,1,2),checked=true)
        assertEquals(built.copy(correct=true),ExerciseReducer.restore(built.snapshot().encode(),listOf(build)))
    }

    @Test fun snapshotRoundTripsMatchProgressAndWrongCount() {
        val pairs=match();val tasks=listOf(pairs)
        val mismatched=ExerciseReducer.flip(ExerciseReducer.flip(Exercise(),0,tasks),2,tasks)
        assertEquals(0 to 2,mismatched.mismatch)
        assertEquals(mismatched,ExerciseReducer.restore(mismatched.snapshot().encode(),tasks))
        val matched=ExerciseReducer.flip(ExerciseReducer.flip(ExerciseReducer.clearMismatch(mismatched),0,tasks),1,tasks)
        assertEquals(setOf(0),matched.matched);assertFalse(matched.checked)
        assertEquals(matched,ExerciseReducer.restore(matched.snapshot().encode(),tasks))
        var done=matched
        done=ExerciseReducer.flip(ExerciseReducer.flip(done,2,tasks),3,tasks)
        done=ExerciseReducer.flip(ExerciseReducer.flip(done,4,tasks),5,tasks)
        assertTrue(done.checked);assertTrue(done.correct);assertEquals(setOf(0,1,2),done.matched)
        assertEquals(done,ExerciseReducer.restore(done.snapshot().encode(),tasks))
        val listening=choice(TaskKind.InitialSound)
        val wrong=ExerciseReducer.check(Exercise(selected="ب"),listOf(listening))
        assertEquals(1,wrong.wrongCount);assertFalse(wrong.correct)
        assertEquals(wrong,ExerciseReducer.restore(wrong.snapshot().encode(),listOf(listening)))
        val legacy=SessionSnapshot(1,0,"پ",listOf(0,1),false,true,false)
        val restored=ExerciseReducer.restore(legacy.encode(),listOf(listening))
        assertEquals(0,restored.wrongCount);assertTrue(restored.matched.isEmpty());assertNull(restored.mismatch)
    }

    @Test fun emptyMatchTaskCanAlwaysBeSkippedAndOddTokensStillComplete() {
        val empty=match().copy(id="match_empty",tokens=emptyList())
        val odd=match().copy(id="match_odd",tokens=listOf("آب","آ","باد"))
        val tasks=listOf(empty,odd)
        assertTrue(ExerciseReducer.canSkip(Exercise(),tasks))
        assertEquals(Exercise(index=1),ExerciseReducer.skip(Exercise(),tasks))
        val done=ExerciseReducer.flip(ExerciseReducer.flip(Exercise(index=1),0,tasks),1,tasks)
        assertTrue(done.checked);assertTrue(done.correct)
    }

    @Test fun corruptOrStaleSnapshotFallsBackWithoutInvalidSelections() {
        val task=choice(TaskKind.DotFamily);val tasks=listOf(task)
        assertEquals(Exercise(),ExerciseReducer.restore("broken",tasks))
        val stale=SessionSnapshot(index=99,selected="خ",selectedTokens=listOf(-1,8),hintVisible=true,checked=true,complete=true)
        assertEquals(Exercise(complete=true),ExerciseReducer.restore(stale.encode(),tasks))
    }

    @Test fun injectableAudioPlaybackReceivesStableRefFallbackAndPurpose() {
        val audio=FakeAudioPlayback(AudioPlaybackResult.PlayedFallback)
        val request=AudioRequest(AudioRef("task.sound_be.prompt"),"ب",AudioPurpose.Prompt)
        assertEquals(AudioPlaybackResult.PlayedFallback,audio.play(request));audio.stop()
        assertEquals(listOf(request),audio.requests);assertEquals(1,audio.stops)
    }
}
