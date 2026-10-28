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
