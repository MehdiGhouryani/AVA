package com.ava.reading.content

enum class TaskKind { Recognize, Listen, BuildWord, DotFamily, PositionalForm, InitialSound, FinalSound, Segment, Blend }
enum class ReviewMode { Mix, Mistakes, Learned }
private val stableRefPattern=Regex("^[a-z0-9._-]+$")
data class AssetRef(val id: String) {init {require(stableRefPattern.matches(id))}}
data class AudioRef(val id: String) {init {require(stableRefPattern.matches(id))}}
data class Skill(val id: String,val title: String,val description: String)
data class Task(
    val id: String,
    val kind: TaskKind,
    val target: String,
    val choices: List<String> = emptyList(),
    val tokens: List<String> = emptyList(),
    val skillIds: List<String> = emptyList(),
    val assetRefs: List<AssetRef> = emptyList(),
    val audioRef: AudioRef? = null,
    val audioRequiredForPublication: Boolean = false,
    val writtenFallback: String? = null
)
data class Lesson(val id: String,val title: String,val goal: String,val order: Int,val unitId: String,val tasks: List<Task>)
data class UnitNode(val id: String,val title: String,val order: Int,val lessons: List<Lesson>)
data class Level(val id: String,val title: String,val order: Int,val units: List<UnitNode>)
data class StoryPage(val id: String,val text: String,val audioRef: AudioRef)
data class Story(
    val id: String,val unitId: String,val title: String,val subtitle: String,val order: Int,
    val artRef: AssetRef,val pageItems: List<StoryPage>,val question: String,val choices: List<String>,val answer: String
) {
    val art: String get()=artRef.id
    val pages: List<String> get()=pageItems.map {it.text}
}
fun unlocked(id: String,completed: Set<String>,lessons: List<Lesson> = Catalog.lessons): Boolean {
    val index = lessons.indexOfFirst { it.id == id }
    return index >= 0 && (id in completed || lessons.take(index).all { it.id in completed })
}
fun nextLesson(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons) = lessons.firstOrNull { it.id !in completed }
fun resumableLesson(active: String,completed: Set<String>,lessons: List<Lesson> = Catalog.lessons) = lessons.firstOrNull {
    it.id == active && it.id !in completed && unlocked(it.id,completed,lessons)
}
fun knownLessonCount(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons) = lessons.count { it.id in completed }
fun knownStoryCount(completed: Set<String>,stories: List<Story> = Catalog.stories) = stories.count { it.id in completed }
private fun legacyTaskKey(lesson: String, index: Int) = "$lesson:$index"
fun taskKey(lesson: String,index: Int,lessons: List<Lesson> = Catalog.lessons): String {
    val id=lessons.firstOrNull {it.id==lesson}?.tasks?.getOrNull(index)?.id
    return if(id==null)legacyTaskKey(lesson,index) else "$lesson:$id"
}
fun taskForKey(key: String,lessons: List<Lesson> = Catalog.lessons): Task? {
    val lesson=lessons.firstOrNull {it.id==key.substringBeforeLast(':')}?:return null
    val part=key.substringAfterLast(':',"")
    return lesson.tasks.firstOrNull {it.id==part} ?: part.toIntOrNull()?.let {lesson.tasks.getOrNull(it)}
}
fun taskAliases(key: String,lessons: List<Lesson> = Catalog.lessons): Set<String> {
    val lesson=lessons.firstOrNull {it.id==key.substringBeforeLast(':')}?:return setOf(key)
    val task=taskForKey(key,lessons)?:return setOf(key)
    val index=lesson.tasks.indexOf(task)
    return setOf(taskKey(lesson.id,index,lessons),legacyTaskKey(lesson.id,index))
}
fun orderedTaskKeys(keys: Set<String>,lessons: List<Lesson> = Catalog.lessons) = lessons.flatMap {lesson->lesson.tasks.indices.mapNotNull {index->
    val stable=taskKey(lesson.id,index,lessons);val legacy=legacyTaskKey(lesson.id,index)
    when {stable in keys->stable;legacy in keys->legacy;else->null}
}}
fun learnedTaskKeys(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons) = lessons.filter {it.id in completed}.flatMap {lesson->
    lesson.tasks.indices.map {taskKey(lesson.id,it,lessons)}
}
private fun <T> rotated(values: List<T>, sequence: Int): List<T> {
    if(values.isEmpty())return values
    val start=Math.floorMod(sequence,values.size)
    return values.drop(start)+values.take(start)
}
fun plannedReviewKeys(mode: ReviewMode,completed: Set<String>,mistakes: Set<String>,sequence: Int,limit: Int=5,lessons: List<Lesson> = Catalog.lessons): List<String> {
    if(limit<=0)return emptyList()
    val validMistakes=orderedTaskKeys(mistakes,lessons)
    val learned=rotated(learnedTaskKeys(completed,lessons),sequence)
    if(mode==ReviewMode.Mistakes)return validMistakes.take(limit)
    if(mode==ReviewMode.Learned)return learned.take(limit)
    val pool=(validMistakes+learned).distinctBy {taskForKey(it,lessons)?.id}
    val result=mutableListOf<String>()
    validMistakes.take(2).forEach {if(it !in result)result+=it}
    ActivityFamily.entries.forEach {family->pool.firstOrNull {key->
        key !in result && taskForKey(key,lessons)?.let {ActivityRegistry[it.kind].family}==family
    }?.let(result::add)}
    pool.forEach {if(result.size<limit && it !in result)result+=it}
    return result.take(limit)
}
fun fa(value: Any): String = value.toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")
fun normalizeDigits(value: String) = value.mapNotNull { c -> when(c) { in '۰'..'۹' -> '0' + (c-'۰'); in '٠'..'٩' -> '0' + (c-'٠'); in '0'..'9' -> c; else -> null } }.joinToString("")
fun normalizePhone(value: String): String {
    val d = normalizeDigits(value)
    return when { d.startsWith("0098") -> "0" + d.drop(4); d.startsWith("98") && d.length >= 12 -> "0" + d.drop(2); d.startsWith("9") && d.length == 10 -> "0$d"; else -> d }.take(11)
}
fun validPhone(value: String) = Regex("09[0-9]{9}").matches(value)
