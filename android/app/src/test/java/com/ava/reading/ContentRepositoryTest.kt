package com.ava.reading

import com.ava.reading.content.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ContentRepositoryTest {
    private fun candidate(
        id: String="candidate-0.5.1",
        minApp: String="0.5.0",
        status: ContentReleaseStatus=ContentReleaseStatus.Staged,
        levels: List<Level> = Catalog.levels
    )=Catalog.snapshot.copy(
        release=Catalog.release.copy(releaseId=id,status=status,minAppVersion=minApp),
        levels=levels
    )

    @Test fun bundledSourceIsCanonicalFallback()=runBlocking {
        val repository=LocalContentRepository()
        assertEquals(ContentSourceKind.Bundled,BundledContentSource().kind)
        assertEquals(Catalog.release.releaseId,repository.active().release.releaseId)
    }

    @Test fun validCandidateStagesVerifiesAndActivatesAtomically()=runBlocking {
        val store=MemoryPublishedContentStore();val repository=LocalContentRepository(store=store)
        val next=candidate();val result=repository.activate(next)
        assertEquals(ContentActivationResult.Activated(next.release.releaseId),result)
        assertEquals(next.release.releaseId,repository.active().release.releaseId)
        assertEquals(next.release.releaseId,PublishedLocalSource(store).candidate()?.release?.releaseId)
    }

    @Test fun incompatibleOrEmptyCandidateIsRejectedWithoutChangingActive()=runBlocking {
        val repository=LocalContentRepository()
        val incompatible=repository.activate(candidate(id="future",minApp="9.0.0"))
        val empty=repository.activate(candidate(id="empty",levels=emptyList()))
        assertEquals(ContentActivationResult.Rejected("app_version_incompatible"),incompatible)
        assertEquals(ContentActivationResult.Rejected("content_empty"),empty)
        assertEquals(Catalog.release.releaseId,repository.active().release.releaseId)
    }

    @Test fun rollbackRestoresPreviousBundledSnapshot()=runBlocking {
        val repository=LocalContentRepository();val next=candidate()
        repository.activate(next);assertEquals(next.release.releaseId,repository.active().release.releaseId)
        assertEquals(ContentActivationResult.Activated(Catalog.release.releaseId),repository.rollback())
        assertEquals(Catalog.release.releaseId,repository.active().release.releaseId)
    }

    @Test fun publishedCandidateRequiresApprovalAndSignature()=runBlocking {
        val repository=LocalContentRepository()
        val result=repository.activate(candidate(status=ContentReleaseStatus.Published))
        assertEquals(ContentActivationResult.Rejected("published_release_unverified"),result)
    }
}
