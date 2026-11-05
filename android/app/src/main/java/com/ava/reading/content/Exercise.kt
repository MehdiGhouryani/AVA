package com.ava.reading.content

import java.nio.charset.StandardCharsets
import java.util.Base64

data class Exercise(
    val index: Int = 0,
    val selected: String? = null,
    val selectedTokens: List<Int> = emptyList(),
    val hintVisible: Boolean = false,
    val checked: Boolean = false,
    val correct: Boolean = false,
    val complete: Boolean = false,
    val wrongCount: Int = 0,
    val pending: Int? = null,
    val matched: Set<Int> = emptySet(),
    val mismatch: Pair<Int,Int>? = null
)
data class SessionSnapshot(
    val version: Int=2,
    val index: Int,
    val selected: String?,
    val selectedTokens: List<Int>,
    val hintVisible: Boolean,
    val checked: Boolean,
    val complete: Boolean,
    val wrongCount: Int=0,
    val matched: Set<Int> = emptySet(),
    val mismatch: Pair<Int,Int>? = null
) {
    fun encode(): String {
        val choice=selected?.let {Base64.getUrlEncoder().withoutPadding().encodeToString(it.toByteArray(StandardCharsets.UTF_8))}.orEmpty()
        val pair=mismatch?.let {"${it.first},${it.second}"}.orEmpty()
        return listOf(version,index,choice,selectedTokens.joinToString(","),hintVisible,checked,complete,wrongCount,matched.joinToString(","),pair).joinToString("|")
    }
    companion object {
        fun decode(value: String): SessionSnapshot?=runCatching {
            val parts=value.split('|')
            val version=parts[0].toInt()
            require((version==1 && parts.size==7) || (version==2 && parts.size==10))
            val selected=parts[2].takeIf {it.isNotEmpty()}?.let {String(Base64.getUrlDecoder().decode(it),StandardCharsets.UTF_8)}
            val tokens=parts[3].takeIf {it.isNotEmpty()}?.split(',')?.map {it.toInt()}?:emptyList()
            val base=SessionSnapshot(version=version,index=parts[1].toInt(),selected=selected,selectedTokens=tokens,hintVisible=parts[4].toBooleanStrict(),checked=parts[5].toBooleanStrict(),complete=parts[6].toBooleanStrict())
            if(version==1)return@runCatching base
            val matched=parts[8].takeIf {it.isNotEmpty()}?.split(',')?.map {it.toInt()}?.toSet()?:emptySet()
            val mismatch=parts[9].takeIf {it.isNotEmpty()}?.split(',')?.let {require(it.size==2);it[0].toInt() to it[1].toInt()}
            base.copy(wrongCount=parts[7].toInt(),matched=matched,mismatch=mismatch)
        }.getOrNull()
    }
}

fun Exercise.snapshot()=SessionSnapshot(index=index,selected=selected,selectedTokens=selectedTokens,hintVisible=hintVisible,checked=checked,complete=complete,wrongCount=wrongCount,matched=matched,mismatch=mismatch)

object ExerciseReducer {
    private fun task(s: Exercise,tasks: List<Task>)=tasks.getOrNull(s.index)
    private fun interaction(task: Task?)=task?.let {ActivityRegistry[it.kind].interaction}
    fun choose(s: Exercise, value: String, tasks: List<Task>): Exercise =
        if(s.complete || (s.checked && s.correct) || interaction(task(s,tasks))!=ActivityInteraction.Choice || value !in task(s,tasks)?.choices.orEmpty()) s
        else s.copy(selected=value, checked=false, correct=false)
    fun chooseToken(s: Exercise, token: Int, tasks: List<Task>): Exercise {
        val task=task(s,tasks)?:return s
        return if(s.complete || (s.checked&&s.correct) || interaction(task)!=ActivityInteraction.TokenBuild || token !in task.tokens.indices || token in s.selectedTokens)s
        else s.copy(selectedTokens=s.selectedTokens+token,checked=false,correct=false)
    }
    fun removeToken(s: Exercise,tasks: List<Task>): Exercise {
        val task=task(s,tasks)?:return s
        return if(s.complete || (s.checked&&s.correct) || interaction(task)!=ActivityInteraction.TokenBuild || s.selectedTokens.isEmpty())s
        else s.copy(selectedTokens=s.selectedTokens.dropLast(1),checked=false,correct=false)
    }
    /** Pair matching: cards are token indices; tokens[2i] pairs with tokens[2i+1]. Mismatches are never counted. */
    fun flip(s: Exercise, card: Int, tasks: List<Task>): Exercise {
        val task=task(s,tasks)?:return s
        if(s.complete || (s.checked&&s.correct) || interaction(task)!=ActivityInteraction.PairMatch || card !in task.tokens.indices || card/2 in s.matched)return s
        val cleared=s.copy(mismatch=null);val first=cleared.pending
        return when {
            first==null -> cleared.copy(pending=card)
            first==card -> cleared
            first/2==card/2 -> cleared.copy(pending=null,matched=cleared.matched+(card/2)).let {
                if(it.matched.size==task.tokens.size/2)it.copy(checked=true,correct=true) else it
            }
            else -> cleared.copy(pending=null,mismatch=first to card)
        }
    }
    fun clearMismatch(s: Exercise): Exercise = if(s.mismatch==null)s else s.copy(mismatch=null)
    fun revealHint(s: Exercise,tasks: List<Task>): Exercise =
        if(s.complete || task(s,tasks)?.let {ActivityRegistry[it.kind].supportsHint}!=true)s else s.copy(hintVisible=true)
    fun response(s: Exercise,tasks: List<Task>): String? {
        val task=task(s,tasks)?:return null
        return if(interaction(task)==ActivityInteraction.TokenBuild && s.selectedTokens.size==task.tokens.size)
            s.selectedTokens.joinToString("") {task.tokens[it]} else s.selected
    }
    fun canCheck(s: Exercise,tasks: List<Task>)=!s.complete && response(s,tasks)!=null
    fun check(s: Exercise, tasks: List<Task>): Exercise {
        if(!canCheck(s,tasks) || s.checked)return s
        val correct=response(s,tasks)==tasks[s.index].target
        return s.copy(checked=true,correct=correct,wrongCount=s.wrongCount+if(correct)0 else 1)
    }
    /** A PairMatch task with fewer than two tokens can never be played: no card exists to flip, so the learner must always be able to skip it. */
    private fun unplayable(task: Task?)=interaction(task)==ActivityInteraction.PairMatch && (task?.tokens?.size?:0)<2
    fun canSkip(s: Exercise, tasks: List<Task>)=!s.complete && task(s,tasks)!=null && (s.wrongCount>=2 || unplayable(task(s,tasks))) && !(s.checked&&s.correct)
    fun skip(s: Exercise, tasks: List<Task>): Exercise = when {
        !canSkip(s,tasks) -> s
        s.index==tasks.lastIndex -> s.copy(complete=true)
        else -> Exercise(index=s.index+1)
    }
    fun advance(s: Exercise, tasks: List<Task>): Exercise = when {
        s.complete || !s.checked || !s.correct -> s
        s.index==tasks.lastIndex -> s.copy(complete=true)
        else -> Exercise(index=s.index+1)
    }
    fun restore(encoded: String?,tasks: List<Task>): Exercise {
        val snapshot=encoded?.let(SessionSnapshot::decode)?:return Exercise()
        if(tasks.isEmpty())return Exercise()
        val index=snapshot.index.coerceIn(0,tasks.lastIndex);val task=tasks[index];val mode=interaction(task)
        val selected=snapshot.selected?.takeIf {mode==ActivityInteraction.Choice && it in task.choices}
        val tokens=snapshot.selectedTokens.distinct().filter {mode==ActivityInteraction.TokenBuild && it in task.tokens.indices}
        val matched=if(mode==ActivityInteraction.PairMatch)snapshot.matched.filter {it in 0 until task.tokens.size/2}.toSet() else emptySet()
        val mismatch=snapshot.mismatch?.takeIf {mode==ActivityInteraction.PairMatch && it.first in task.tokens.indices && it.second in task.tokens.indices && it.first!=it.second && it.first/2 !in matched && it.second/2 !in matched}
        val base=Exercise(index=index,selected=selected,selectedTokens=tokens,hintVisible=snapshot.hintVisible&&ActivityRegistry[task.kind].supportsHint,wrongCount=snapshot.wrongCount,matched=matched,mismatch=mismatch,complete=snapshot.complete&&index==tasks.lastIndex)
        if(mode==ActivityInteraction.PairMatch) {
            return if(task.tokens.size>=2 && matched.size==task.tokens.size/2)base.copy(checked=true,correct=true) else base
        }
        if(!snapshot.checked)return base
        val response=response(base,tasks)?:return base
        return base.copy(checked=true,correct=response==task.target)
    }
}
