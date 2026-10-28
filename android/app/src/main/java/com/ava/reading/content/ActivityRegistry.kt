package com.ava.reading.content

enum class ActivityInteraction { Choice, TokenBuild }
enum class ActivityFamily { Visual, Listening, WordConstruction }

data class ActivityDefinition(
    val kind: TaskKind,
    val interaction: ActivityInteraction,
    val family: ActivityFamily,
    val supportsHint: Boolean,
    val supportsAudio: Boolean
)

object ActivityRegistry {
    private val definitions=TaskKind.entries.associateWith {kind->when(kind) {
        TaskKind.Recognize,TaskKind.DotFamily,TaskKind.PositionalForm -> ActivityDefinition(kind,ActivityInteraction.Choice,ActivityFamily.Visual,false,true)
        TaskKind.Listen,TaskKind.InitialSound,TaskKind.FinalSound -> ActivityDefinition(kind,ActivityInteraction.Choice,ActivityFamily.Listening,true,true)
        TaskKind.BuildWord,TaskKind.Segment,TaskKind.Blend -> ActivityDefinition(kind,ActivityInteraction.TokenBuild,ActivityFamily.WordConstruction,false,false)
    }}
    operator fun get(kind: TaskKind)=requireNotNull(definitions[kind])
    val all: List<ActivityDefinition> get()=TaskKind.entries.map {get(it)}
}
