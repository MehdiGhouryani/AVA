package com.ava.reading.content

import java.time.DayOfWeek
import java.time.LocalDate

/** One discovery card per lesson. It is derived from the completed lessons, so a card is granted exactly once by construction. */
data class Discovery(val id: String,val title: String,val goal: String,val order: Int,val unlocked: Boolean)

fun discoveries(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons): List<Discovery> =
    lessons.mapIndexed {index,lesson->Discovery(lesson.id,lesson.title,lesson.goal,index+1,lesson.id in completed)}

fun newDiscoveryIds(before: Set<String>,after: Set<String>,lessons: List<Lesson> = Catalog.lessons): List<String> =
    lessons.map {it.id}.filter {it in after && it !in before}

/** Overall path state for the path-end card and the home hero. Unknown completed ids never count. */
data class PathStatus(val done: Int,val total: Int,val complete: Boolean,val next: Lesson?)

fun pathStatus(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons) = PathStatus(
    done=knownLessonCount(completed,lessons),total=lessons.size,
    complete=lessons.isNotEmpty() && lessons.all {it.id in completed},next=nextLesson(completed,lessons)
)

/** State of a node on the learning path: done lessons can be replayed, exactly one lesson is current (the next one), the rest stay locked until it is done. */
enum class PathNodeState { Done, Current, Locked }
data class PathNode(val lesson: Lesson,val order: Int,val state: PathNodeState)

fun pathNodes(completed: Set<String>,lessons: List<Lesson> = Catalog.lessons): List<PathNode> {
    val current=nextLesson(completed,lessons)?.id
    return lessons.mapIndexed {index,lesson->
        PathNode(lesson,index+1,when {
            lesson.id in completed->PathNodeState.Done
            lesson.id==current->PathNodeState.Current
            else->PathNodeState.Locked
        })
    }
}

/** Weeks run Saturday to Friday. A day is done when the child completed a lesson that day; an empty day carries no penalty. */
data class FlowerDay(val date: LocalDate,val done: Boolean,val isToday: Boolean,val isFuture: Boolean)
data class FlowerWeek(val days: List<FlowerDay>) {
    val doneCount: Int get()=days.count {it.done}
}

fun weekStart(today: LocalDate): LocalDate =
    today.minusDays(Math.floorMod(today.dayOfWeek.value-DayOfWeek.SATURDAY.value,7).toLong())

fun flowerWeek(today: LocalDate,active: Set<LocalDate>): FlowerWeek {
    val start=weekStart(today)
    return FlowerWeek((0..6).map {offset->
        val date=start.plusDays(offset.toLong())
        FlowerDay(date,done=date in active && !date.isAfter(today),isToday=date==today,isFuture=date.isAfter(today))
    })
}

private const val ACTIVE_DAYS_LIMIT=60

fun parseActiveDays(values: Set<String>): Set<LocalDate> = values.mapNotNull {runCatching {LocalDate.parse(it)}.getOrNull()}.toSet()

/** Adds today to the stored ISO dates, drops corrupt entries and keeps only the newest 60 days. */
fun recordActiveDay(values: Set<String>,today: LocalDate): Set<String> =
    (parseActiveDays(values)+today).sortedDescending().take(ACTIVE_DAYS_LIMIT).map {it.toString()}.toSet()
