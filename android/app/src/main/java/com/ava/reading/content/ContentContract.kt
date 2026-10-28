package com.ava.reading.content

enum class ContentReleaseStatus { Development, Staged, Published, Archived, RolledBack }
enum class ContentSourceKind { Bundled, PublishedLocal, Remote }

data class ContentRelease(
    val releaseId: String,
    val status: ContentReleaseStatus,
    val schemaVersion: Int,
    val contentVersion: String,
    val locale: String,
    val minAppVersion: String,
    val maxAppVersion: String?,
    val bundleSha256: String,
    val audioInventorySha256: String,
    val approvedForRelease: Boolean,
    val signature: String?
) {
    fun supports(appVersion: String): Boolean =
        compareVersions(appVersion,minAppVersion)>=0 && (maxAppVersion==null || compareVersions(appVersion,maxAppVersion)<=0)
    fun publishable(): Boolean = status==ContentReleaseStatus.Published && approvedForRelease && !signature.isNullOrBlank()
}

data class ContentSnapshot(
    val release: ContentRelease,
    val skills: List<Skill>,
    val levels: List<Level>,
    val stories: List<Story>
) {
    val lessons: List<Lesson> get()=levels.flatMap {level->level.units.flatMap {it.lessons}}
}

sealed interface ContentActivationResult {
    data class Activated(val releaseId: String): ContentActivationResult
    data class Rejected(val reason: String): ContentActivationResult
}

/** Remote is a contract only. Runtime implementations remain local/offline through 0.6.1. */
interface ContentSource {
    val kind: ContentSourceKind
    suspend fun candidate(): ContentSnapshot?
}

interface ContentRepository {
    suspend fun active(): ContentSnapshot
    suspend fun activate(candidate: ContentSnapshot): ContentActivationResult
    suspend fun rollback(): ContentActivationResult
}

internal fun compareVersions(left: String,right: String): Int {
    val l=left.substringBefore('-').split('.').map {it.toIntOrNull()?:0}
    val r=right.substringBefore('-').split('.').map {it.toIntOrNull()?:0}
    repeat(maxOf(l.size,r.size)) {index->
        val compared=(l.getOrElse(index){0}).compareTo(r.getOrElse(index){0})
        if(compared!=0)return compared
    }
    return 0
}
