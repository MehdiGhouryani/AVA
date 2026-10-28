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
    val complete: Boolean = false
)
data class SessionSnapshot(
    val version: Int=1,
    val index: Int,
    val selected: String?,
    val selectedTokens: List<Int>,
    val hintVisible: Boolean,
    val checked: Boolean,
    val complete: Boolean
) {
    fun encode(): String {
        val choice=selected?.let {Base64.getUrlEncoder().withoutPadding().encodeToString(it.toByteArray(StandardCharsets.UTF_8))}.orEmpty()
        return listOf(version,index,choice,selectedTokens.joinToString(","),hintVisible,checked,complete).joinToString("|")
    }
    companion object {
        fun decode(value: String): SessionSnapshot?=runCatching {
            val parts=value.split('|');require(parts.size==7 && parts[0].toInt()==1)
            val selected=parts[2].takeIf {it.isNotEmpty()}?.let {String(Base64.getUrlDecoder().decode(it),StandardCharsets.UTF_8)}
            val tokens=parts[3].takeIf {it.isNotEmpty()}?.split(',')?.map {it.toInt()}?:emptyList()
            SessionSnapshot(1,parts[1].toInt(),selected,tokens,parts[4].toBooleanStrict(),parts[5].toBooleanStrict(),parts[6].toBooleanStrict())
        }.getOrNull()
    }
}

fun Exercise.snapshot()=SessionSnapshot(index=index,selected=selected,selectedTokens=selectedTokens,hintVisible=hintVisible,checked=checked,complete=complete)

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
    fun revealHint(s: Exercise,tasks: List<Task>): Exercise =
        if(s.complete || task(s,tasks)?.let {ActivityRegistry[it.kind].supportsHint}!=true)s else s.copy(hintVisible=true)
    fun response(s: Exercise,tasks: List<Task>): String? {
        val task=task(s,tasks)?:return null
        return if(interaction(task)==ActivityInteraction.TokenBuild && s.selectedTokens.size==task.tokens.size)
            s.selectedTokens.joinToString("") {task.tokens[it]} else s.selected
    }
    fun canCheck(s: Exercise,tasks: List<Task>)=!s.complete && response(s,tasks)!=null
    fun check(s: Exercise, tasks: List<Task>): Exercise =
        if(!canCheck(s,tasks))s else s.copy(checked=true,correct=response(s,tasks)==tasks[s.index].target)
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
        val base=Exercise(index=index,selected=selected,selectedTokens=tokens,hintVisible=snapshot.hintVisible&&ActivityRegistry[task.kind].supportsHint,complete=snapshot.complete&&index==tasks.lastIndex)
        if(!snapshot.checked)return base
        val response=response(base,tasks)?:return base
        return base.copy(checked=true,correct=response==task.target)
    }
}
