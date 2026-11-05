package com.ava.reading

import com.ava.reading.content.*

/**
 * I8: one synthetic task of every activity kind, for UI tests only.
 *
 * Find, Bubble and Match have no content tasks yet, so the published
 * catalog cannot exercise their renderers. This fixture builds them
 * synthetically — it never enters published content (no content JSON,
 * no Catalog entry) — so a UI test can render every renderer path:
 * RecognizeTask, ListenTask, BuildWordTask, FindTask, BubbleTask and
 * MatchTask. PositionalForm and Segment are included too: they are
 * deliberately inactive in content, but their registry mapping
 * (Choice / TokenBuild) still has to render.
 */
object AllKindsFixture {
    fun task(kind: TaskKind): Task = when (kind) {
        TaskKind.Recognize -> Task(id="fixture_recognize",kind=kind,target="آ",choices=listOf("آ","ا","د"),skillIds=listOf("grapheme_recognition"))
        TaskKind.DotFamily -> Task(id="fixture_dot_family",kind=kind,target="پ",choices=listOf("ب","پ","ت"),skillIds=listOf("grapheme_recognition"))
        TaskKind.PositionalForm -> Task(id="fixture_positional_form",kind=kind,target="م",choices=listOf("ن","م","و"),skillIds=listOf("grapheme_recognition"))
        TaskKind.Find -> Task(id="fixture_find",kind=kind,target="بابا",choices=listOf("باد","آب","بابا"),skillIds=listOf("word_recognition"))
        TaskKind.Listen -> Task(id="fixture_listen",kind=kind,target="ب",choices=listOf("پ","ب","ت"),skillIds=listOf("phoneme_awareness"),audioRef=AudioRef("task.fixture.listen.prompt"),audioRequiredForPublication=false,writtenFallback="ب")
        TaskKind.InitialSound -> Task(id="fixture_initial_sound",kind=kind,target="ب",choices=listOf("ب","م","ن"),skillIds=listOf("phoneme_awareness"),audioRef=AudioRef("task.fixture.initial.prompt"),audioRequiredForPublication=false,writtenFallback="بابا")
        TaskKind.FinalSound -> Task(id="fixture_final_sound",kind=kind,target="ب",choices=listOf("ب","د","م"),skillIds=listOf("phoneme_awareness"),audioRef=AudioRef("task.fixture.final.prompt"),audioRequiredForPublication=false,writtenFallback="آب")
        TaskKind.Bubble -> Task(id="fixture_bubble",kind=kind,target="پ",choices=listOf("ب","ت","پ"),skillIds=listOf("phoneme_awareness"),audioRef=AudioRef("task.fixture.bubble.prompt"),audioRequiredForPublication=false,writtenFallback="پ")
        TaskKind.BuildWord -> Task(id="fixture_build_word",kind=kind,target="آب",tokens=listOf("ب","آ"),skillIds=listOf("word_building"))
        TaskKind.Segment -> Task(id="fixture_segment",kind=kind,target="باد",tokens=listOf("د","ب","ا"),skillIds=listOf("word_building"))
        TaskKind.Blend -> Task(id="fixture_blend",kind=kind,target="باب",tokens=listOf("ب","ا","ب"),skillIds=listOf("word_building"))
        TaskKind.Match -> Task(id="fixture_match",kind=kind,target="آب=آ|باد=ب|گل=گ",tokens=listOf("آب","آ","باد","ب","گل","گ"),skillIds=listOf("word_building"))
    }
    fun tasks(): List<Task> = TaskKind.entries.map(::task)
    fun lesson(): Lesson = Lesson(id="lesson_all_kinds",title="همهٔ نوع‌ها",goal="fixture — همهٔ نوع‌های فعالیت",order=1,unitId="unit_fixture",tasks=tasks())
    fun snapshot(): ContentSnapshot = ContentSnapshot(
        release=Catalog.release,skills=Catalog.skills,
        levels=listOf(Level("level_fixture","fixture",1,listOf(UnitNode("unit_fixture","fixture",1,listOf(lesson()))))),
        stories=Catalog.stories
    )
}
