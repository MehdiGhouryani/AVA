package com.ava.reading

import com.ava.reading.content.*
import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class ProgressionTest {
    private fun lesson(id: String)=Lesson(id,"درس $id","هدف $id",1,"unit",listOf(Task("t_$id",TaskKind.Recognize,"ب",choices=listOf("آ","ب","پ"))))
    private val lessons=listOf(lesson("a"),lesson("b"),lesson("c"))

    @Test fun discoveriesAreDerivedInLessonOrderAndIgnoreUnknownIds() {
        val cards=discoveries(setOf("b","removed"),lessons)
        assertEquals(listOf("a","b","c"),cards.map {it.id});assertEquals(listOf(1,2,3),cards.map {it.order})
        assertEquals(listOf(false,true,false),cards.map {it.unlocked})
        assertEquals(cards,discoveries(setOf("b","b","removed"),lessons))
    }

    @Test fun aDiscoveryIsNewOnlyTheFirstTime() {
        assertEquals(listOf("a"),newDiscoveryIds(emptySet(),setOf("a"),lessons))
        assertTrue(newDiscoveryIds(setOf("a"),setOf("a"),lessons).isEmpty())
        assertEquals(listOf("b","c"),newDiscoveryIds(setOf("a"),setOf("a","b","c","unknown"),lessons))
    }

    @Test fun pathStatusKnowsNextAndEnd() {
        val start=pathStatus(emptySet(),lessons);assertEquals(0,start.done);assertEquals(3,start.total);assertFalse(start.complete);assertEquals("a",start.next?.id)
        val middle=pathStatus(setOf("a","x"),lessons);assertEquals(1,middle.done);assertEquals("b",middle.next?.id);assertFalse(middle.complete)
        val end=pathStatus(setOf("a","b","c"),lessons);assertEquals(3,end.done);assertTrue(end.complete);assertNull(end.next)
        assertFalse(pathStatus(setOf("a"),emptyList()).complete)
    }

    @Test fun pathHasExactlyOneCurrentNodeAndLocksTheRest() {
        assertEquals(listOf(PathNodeState.Current,PathNodeState.Locked,PathNodeState.Locked),pathNodes(emptySet(),lessons).map {it.state})
        assertEquals(listOf(PathNodeState.Done,PathNodeState.Done,PathNodeState.Current),pathNodes(setOf("a","b"),lessons).map {it.state})
        assertEquals(listOf(1,2,3),pathNodes(setOf("a"),lessons).map {it.order})
        assertTrue(pathNodes(setOf("a","b","c","x"),lessons).all {it.state==PathNodeState.Done})
        assertTrue(pathNodes(emptySet(),emptyList()).isEmpty())
    }

    @Test fun anOutOfOrderCompletionStaysDoneWhileTheFirstGapIsCurrent() {
        assertEquals(listOf(PathNodeState.Current,PathNodeState.Locked,PathNodeState.Done),pathNodes(setOf("c"),lessons).map {it.state})
        assertEquals(1,pathNodes(setOf("c"),lessons).count {it.state==PathNodeState.Current})
    }

    @Test fun weeksRunSaturdayToFriday() {
        assertEquals(DayOfWeek.TUESDAY,LocalDate.of(2026,11,3).dayOfWeek)
        val saturday=LocalDate.of(2026,10,31)
        assertEquals(DayOfWeek.SATURDAY,saturday.dayOfWeek)
        (0L..6L).forEach {offset->assertEquals(saturday,weekStart(saturday.plusDays(offset)))}
        assertEquals(LocalDate.of(2026,11,7),weekStart(LocalDate.of(2026,11,7)))
    }

    @Test fun flowerWeekMarksDoneDaysTodayAndNeverPenalises() {
        val today=LocalDate.of(2026,11,3)
        val week=flowerWeek(today,setOf(LocalDate.of(2026,10,31),today,LocalDate.of(2026,11,5)))
        assertEquals(7,week.days.size);assertEquals(LocalDate.of(2026,10,31),week.days.first().date);assertEquals(LocalDate.of(2026,11,6),week.days.last().date)
        assertEquals(listOf(true,false,false,true,false,false,false),week.days.map {it.done})
        assertEquals(listOf(false,false,false,true,false,false,false),week.days.map {it.isToday})
        assertEquals(listOf(false,false,false,false,true,true,true),week.days.map {it.isFuture})
        assertEquals(2,week.doneCount)
        assertEquals(0,flowerWeek(today,emptySet()).doneCount)
    }

    @Test fun activeDaysAreRecordedOnceCleanedAndBounded() {
        val today=LocalDate.of(2026,11,3)
        val once=recordActiveDay(setOf("2026-11-01","broken","","2026-13-40"),today)
        assertEquals(setOf("2026-11-01","2026-11-03"),once);assertEquals(once,recordActiveDay(once,today))
        val many=(1L..80L).map {today.minusDays(it).toString()}.toSet()
        val bounded=recordActiveDay(many,today)
        assertEquals(60,bounded.size);assertTrue(today.toString() in bounded);assertFalse(today.minusDays(80).toString() in bounded)
    }
}
