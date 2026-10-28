package com.ava.reading.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class TaskProgress(
    val attempts: Int = 0,
    val correct: Int = 0,
    val hints: Int = 0,
    val lastSeenEpochMillis: Long? = null
) {
    fun record(wasCorrect: Boolean,hintUsed: Boolean,seenAt: Long)=copy(
        attempts=attempts+1,
        correct=correct+if(wasCorrect)1 else 0,
        hints=hints+if(hintUsed)1 else 0,
        lastSeenEpochMillis=seenAt.coerceAtLeast(0)
    )
}

data class Learner(
    val onboarded: Boolean = false, val signedIn: Boolean = false,
    val name: String = "", val avatar: Int = 0,
    val completed: Set<String> = emptySet(), val stories: Set<String> = emptySet(),
    val mistakes: Set<String> = emptySet(), val reviews: Int = 0,
    val positions: Map<String, Int> = emptyMap(), val activeLesson: String = "", val activeIndex: Int = 0,
    val narration: Boolean = true, val sounds: Boolean = true, val reducedMotion: Boolean = false,
    val taskProgress: Map<String, TaskProgress> = emptyMap()
)
interface LearnerRepository {
    val data: Flow<Learner>
    suspend fun update(transform: (Learner) -> Learner)
}
class MemoryLearnerRepository(initial: Learner = Learner()) : LearnerRepository {
    private val state = MutableStateFlow(initial)
    private val mutex = Mutex()
    override val data = state.asStateFlow()
    override suspend fun update(transform: (Learner) -> Learner) { mutex.withLock { state.value = transform(state.value) } }
}
val Context.learnerStore: DataStore<Preferences> by preferencesDataStore("ava_learner_v1")
class DataStoreLearnerRepository(private val store: DataStore<Preferences>) : LearnerRepository {
    private object K {
        val onboarded=booleanPreferencesKey("onboarded"); val signedIn=booleanPreferencesKey("mock_signed_in")
        val name=stringPreferencesKey("name"); val avatar=intPreferencesKey("avatar")
        val completed=stringSetPreferencesKey("completed"); val stories=stringSetPreferencesKey("stories")
        val mistakes=stringSetPreferencesKey("mistakes"); val reviews=intPreferencesKey("reviews")
        val positions=stringSetPreferencesKey("positions"); val active=stringPreferencesKey("active_lesson"); val index=intPreferencesKey("active_index")
        val narration=booleanPreferencesKey("narration"); val sounds=booleanPreferencesKey("sounds"); val motion=booleanPreferencesKey("reduced_motion")
        val progressSchema=intPreferencesKey("task_progress_schema");val taskProgress=stringSetPreferencesKey("task_progress_v1")
    }
    private fun decodeProgress(values: Set<String>)=values.mapNotNull {encoded->
        val parts=encoded.split('|');if(parts.size!=5)return@mapNotNull null
        val attempts=parts[1].toIntOrNull()?.coerceAtLeast(0)?:return@mapNotNull null
        val correct=parts[2].toIntOrNull()?.coerceIn(0,attempts)?:return@mapNotNull null
        val hints=parts[3].toIntOrNull()?.coerceIn(0,attempts)?:return@mapNotNull null
        val seen=parts[4].toLongOrNull()?.coerceAtLeast(0)
        parts[0].takeIf {it.isNotBlank() && '|' !in it}?.let {it to TaskProgress(attempts,correct,hints,seen)}
    }.toMap()
    private fun encodeProgress(values: Map<String,TaskProgress>)=values.mapNotNull {(key,value)->
        key.takeIf {it.isNotBlank() && '|' !in it}?.let {"$it|${value.attempts.coerceAtLeast(0)}|${value.correct.coerceIn(0,value.attempts.coerceAtLeast(0))}|${value.hints.coerceIn(0,value.attempts.coerceAtLeast(0))}|${value.lastSeenEpochMillis?.coerceAtLeast(0)?:""}"}
    }.toSet()
    private fun decode(p: Preferences): Learner {
        val progressSchema=p[K.progressSchema]?:0
        require(progressSchema in 0..1) {"Unsupported task progress schema: $progressSchema"}
        return Learner(
        p[K.onboarded]?:false, p[K.signedIn]?:false, p[K.name].orEmpty().take(24), (p[K.avatar]?:0).coerceIn(0,3),
        p[K.completed].orEmpty(),p[K.stories].orEmpty(),p[K.mistakes].orEmpty(),(p[K.reviews]?:0).coerceAtLeast(0),
        p[K.positions].orEmpty().mapNotNull { v -> v.substringAfterLast(':').toIntOrNull()?.let { v.substringBeforeLast(':') to it.coerceAtLeast(0) } }.toMap(),
        p[K.active].orEmpty(),(p[K.index]?:0).coerceAtLeast(0),p[K.narration]?:true,p[K.sounds]?:true,p[K.motion]?:false,
        decodeProgress(p[K.taskProgress].orEmpty())
        )
    }
    override val data = store.data.map(::decode)
    override suspend fun update(transform: (Learner) -> Learner) {
        store.edit { p ->
            val v = transform(decode(p))
            p[K.onboarded]=v.onboarded;p[K.signedIn]=v.signedIn;p[K.name]=v.name;p[K.avatar]=v.avatar
            p[K.completed]=v.completed;p[K.stories]=v.stories;p[K.mistakes]=v.mistakes;p[K.reviews]=v.reviews
            p[K.positions]=v.positions.map { "${it.key}:${it.value}" }.toSet();p[K.active]=v.activeLesson;p[K.index]=v.activeIndex
            p[K.narration]=v.narration;p[K.sounds]=v.sounds;p[K.motion]=v.reducedMotion
            p[K.progressSchema]=1;p[K.taskProgress]=encodeProgress(v.taskProgress)
        }
    }
}
