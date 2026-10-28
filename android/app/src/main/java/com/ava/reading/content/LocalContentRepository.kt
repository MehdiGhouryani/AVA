package com.ava.reading.content

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BundledContentSource(private val snapshot: ContentSnapshot=Catalog.snapshot): ContentSource {
    override val kind=ContentSourceKind.Bundled
    override suspend fun candidate()=snapshot
}

class MemoryPublishedContentStore {
    @Volatile private var active: ContentSnapshot?=null
    @Volatile private var previous: ContentSnapshot?=null
    private var staged: ContentSnapshot?=null
    private var stagedPrevious: ContentSnapshot?=null
    internal fun active()=active
    internal fun previous()=previous
    internal fun stage(value: ContentSnapshot,current: ContentSnapshot?) {staged=value;stagedPrevious=current}
    internal fun staged()=staged
    internal fun discardStage() {staged=null;stagedPrevious=null}
    internal fun commitStage(): ContentSnapshot? {
        val value=staged?:return null
        previous=stagedPrevious;active=value;staged=null;stagedPrevious=null
        return value
    }
    internal fun rollback(): ContentSnapshot? {
        val value=previous?:return null
        val replaced=active;active=value;previous=replaced;staged=null;stagedPrevious=null
        return value
    }
}

class PublishedLocalSource(private val store: MemoryPublishedContentStore): ContentSource {
    override val kind=ContentSourceKind.PublishedLocal
    override suspend fun candidate()=store.active()
}

class LocalContentRepository(
    private val bundled: ContentSource=BundledContentSource(),
    private val store: MemoryPublishedContentStore=MemoryPublishedContentStore(),
    private val appVersion: String="0.6.1"
): ContentRepository {
    private val mutex=Mutex()
    private val published=PublishedLocalSource(store)

    override suspend fun active(): ContentSnapshot=mutex.withLock {
        published.candidate()?.takeIf {validationError(it)==null}
            ?: requireNotNull(bundled.candidate()) {"Bundled content is required"}.also {
                require(validationError(it)==null) {"Bundled content is invalid: ${validationError(it)}"}
            }
    }

    override suspend fun activate(candidate: ContentSnapshot): ContentActivationResult=mutex.withLock {
        validationError(candidate)?.let {return@withLock ContentActivationResult.Rejected(it)}
        val current=published.candidate()?:bundled.candidate()
        if(current?.release?.releaseId==candidate.release.releaseId)
            return@withLock ContentActivationResult.Rejected("release_already_active")
        store.stage(candidate,current)
        val staged=store.staged()?:return@withLock ContentActivationResult.Rejected("staging_missing")
        val stagedError=validationError(staged)
        if(stagedError!=null) {
            store.discardStage()
            return@withLock ContentActivationResult.Rejected(stagedError)
        }
        val activated=store.commitStage()?:return@withLock ContentActivationResult.Rejected("activation_failed")
        ContentActivationResult.Activated(activated.release.releaseId)
    }

    override suspend fun rollback(): ContentActivationResult=mutex.withLock {
        val previous=store.previous()?:return@withLock ContentActivationResult.Rejected("no_previous_release")
        validationError(previous)?.let {return@withLock ContentActivationResult.Rejected("previous_$it")}
        val activated=store.rollback()?:return@withLock ContentActivationResult.Rejected("rollback_failed")
        ContentActivationResult.Activated(activated.release.releaseId)
    }

    private fun validationError(snapshot: ContentSnapshot): String? {
        val release=snapshot.release
        if(release.schemaVersion!=3)return "schema_incompatible"
        if(release.locale!="fa-IR")return "locale_incompatible"
        if(!release.supports(appVersion))return "app_version_incompatible"
        if(!Regex("^[a-f0-9]{64}$").matches(release.bundleSha256) || !Regex("^[a-f0-9]{64}$").matches(release.audioInventorySha256))return "hash_invalid"
        if(release.status==ContentReleaseStatus.Published && !release.publishable())return "published_release_unverified"
        if(snapshot.levels.isEmpty() || snapshot.skills.isEmpty())return "content_empty"
        if(snapshot.levels.map {it.id}.distinct().size!=snapshot.levels.size)return "level_ids_invalid"
        val units=snapshot.levels.flatMap {it.units}
        if(units.isEmpty() || units.map {it.id}.distinct().size!=units.size)return "unit_ids_invalid"
        val skillIds=snapshot.skills.map {it.id}.toSet()
        if(skillIds.size!=snapshot.skills.size)return "skill_ids_invalid"
        val lessons=snapshot.lessons
        if(lessons.isEmpty() || lessons.map {it.id}.distinct().size!=lessons.size)return "lesson_ids_invalid"
        val tasks=lessons.flatMap {it.tasks}
        if(tasks.isEmpty() || tasks.map {it.id}.distinct().size!=tasks.size)return "task_ids_invalid"
        if(tasks.any {it.skillIds.isEmpty() || !skillIds.containsAll(it.skillIds)})return "task_skill_refs_invalid"
        if(snapshot.stories.map {it.id}.distinct().size!=snapshot.stories.size)return "story_ids_invalid"
        if(snapshot.stories.any {story->units.none {it.id==story.unitId}})return "story_unit_refs_invalid"
        val pages=snapshot.stories.flatMap {it.pageItems}
        if(pages.map {it.id}.distinct().size!=pages.size)return "story_page_ids_invalid"
        return null
    }
}
