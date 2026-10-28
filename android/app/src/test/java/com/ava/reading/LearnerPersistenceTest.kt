package com.ava.reading

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.ava.reading.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class LearnerPersistenceTest {
    @Test fun diskSurvivesStoreCloseAndReopen()=runBlocking {
        val directory=Files.createTempDirectory("ava-store").toFile();val file=directory.resolve("learner.preferences_pb")
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        val repo=DataStoreLearnerRepository(PreferenceDataStoreFactory.create(scope=scope,produceFile={file}))
        val expected=Learner(true,true,"مینا",2,setOf("visual_shapes_01"),setOf("little_seed"),setOf("dots_02:0"),2,mapOf("little_seed" to 3),"dots_02",1,false,false,true,mapOf("visual_shapes_01:shape_aa" to TaskProgress(2,1,1,1234)))
        repo.update {expected};scope.coroutineContext.job.cancelAndJoin()
        val nextScope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {val reopened=DataStoreLearnerRepository(PreferenceDataStoreFactory.create(scope=nextScope,produceFile={file}));assertEquals(expected,reopened.data.first())}
        finally {nextScope.coroutineContext.job.cancelAndJoin();directory.deleteRecursively()}
    }
    @Test fun concurrentEditsDoNotLoseIncrements()=runBlocking {
        val directory=Files.createTempDirectory("ava-parallel").toFile();val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val repo=DataStoreLearnerRepository(PreferenceDataStoreFactory.create(scope=scope,produceFile={directory.resolve("learner.preferences_pb")}))
            coroutineScope {repeat(20) {launch {repo.update {it.copy(reviews=it.reviews+1)}}}}
            assertEquals(20,repo.data.first().reviews)
        } finally {scope.coroutineContext.job.cancelAndJoin();directory.deleteRecursively()}
    }
    @Test fun taskProgressClampsCorruptCountersAndRecordsDeterministically() {
        val value=TaskProgress(attempts=1,correct=1,hints=0,lastSeenEpochMillis=10).record(wasCorrect=false,hintUsed=true,seenAt=25)
        assertEquals(TaskProgress(2,1,1,25),value)
    }
    @Test fun unknownFutureProgressSchemaFailsInsteadOfSilentlyDroppingData()=runBlocking {
        val directory=Files.createTempDirectory("ava-future-schema").toFile();val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val store=PreferenceDataStoreFactory.create(scope=scope,produceFile={directory.resolve("learner.preferences_pb")})
            store.edit {it[intPreferencesKey("task_progress_schema")]=99}
            val failure=runCatching {DataStoreLearnerRepository(store).data.first()}.exceptionOrNull()
            assertNotNull(failure)
        } finally {scope.coroutineContext.job.cancelAndJoin();directory.deleteRecursively()}
    }
}
