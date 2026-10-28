package com.ava.reading.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ava.reading.content.*
import com.ava.reading.data.*
import com.ava.reading.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

enum class Route { Splash, Onboarding1, Onboarding2, Phone, Otp, Home, LessonDetails, Lesson, Library, StoryDetails, StoryReader, StoryQuestion, StoryComplete, Practice, Progress, Profile, EditProfile, Settings, Help, About, Privacy, Terms }
enum class Confirmation { ExitLesson, ResetProgress, Logout }
data class AvaState(
    val route: Route = Route.Splash, val learner: Learner = Learner(),
    val restoring: Boolean = true, val storageError: Boolean = false,
    val content: ContentSnapshot = Catalog.snapshot, val contentReady: Boolean = false,
    val phone: String = "", val consent: Boolean = false, val code: String = "", val busy: Boolean = false,
    val message: String? = null, val legal: Boolean? = null, val now: Long = 0,
    val resendUntil: Long = 0, val lockedUntil: Long = 0, val offline: Boolean = false,
    val selectedLesson: String = Catalog.lessons.first().id, val exercise: Exercise = Exercise(),
    val reviewing: Boolean = false, val reviewKeys: List<String> = emptyList(),
    val selectedStory: String = Catalog.stories.first().id, val storyPage: Int = 0,
    val storyAnswer: String? = null, val storyChecked: Boolean = false,
    val profileName: String = "", val profileAvatar: Int = 0,
    val confirmation: Confirmation? = null, val notice: String? = null
) {
    val lessons get()=content.lessons
    val stories get()=content.stories
    val lesson get() = lessons.firstOrNull { it.id==selectedLesson } ?: lessons.first()
    val tasks get() = if(reviewing) reviewKeys.mapNotNull {taskForKey(it,lessons)} else lesson.tasks
    val story get() = stories.firstOrNull { it.id==selectedStory } ?: stories.first()
    val resendSeconds get() = ((resendUntil-now+999)/1000).coerceAtLeast(0).toInt()
    val lockSeconds get() = ((lockedUntil-now+999)/1000).coerceAtLeast(0).toInt()
    val locked get() = lockedUntil>now
}
class AvaViewModel(
    private val saved: SavedStateHandle = SavedStateHandle(),
    private val auth: AuthRepository = MockAuthRepository(),
    private val repository: LearnerRepository = MemoryLearnerRepository(),
    private val clock: () -> Long = { System.nanoTime()/1_000_000 },
    private val progressClock: () -> Long = System::currentTimeMillis,
    private val contentRepository: ContentRepository = LocalContentRepository()
) : ViewModel() {
    private val mutable=MutableStateFlow(AvaState(phone=saved["phone"]?:"",consent=saved["consent"]?:false))
    val state=mutable.asStateFlow()
    private var request: Job?=null
    private var ticker: Job?=null
    private var loading: Job?=null
    private var splashReady=false
    private var loaded=false
    private var pending=0
    private val writes=Channel<Learner>(Channel.UNLIMITED)
    init {
        viewModelScope.launch {
            val active=contentRepository.active()
            mutable.update {state->state.copy(
                content=active,contentReady=true,
                selectedLesson=state.selectedLesson.takeIf {id->active.lessons.any {it.id==id}}?:active.lessons.first().id,
                selectedStory=state.selectedStory.takeIf {id->active.stories.any {it.id==id}}?:active.stories.first().id
            )}
            finishStartup()
        }
        viewModelScope.launch {
            // Single writer preserves user intent order. Each snapshot includes all preceding UI edits,
            // so a later successful write also recovers a preceding failed write without losing progress.
            for(snapshot in writes) {
                try { repository.update { snapshot }; mutable.update { it.copy(storageError=false) }; finishStartup() }
                catch(e: CancellationException) { throw e }
                catch(_: Exception) { mutable.update { it.copy(storageError=true) } }
                finally { pending-- }
            }
        }
        readData()
        viewModelScope.launch { delay(1800); splashReady=true; finishStartup() }
    }
    private fun readData() {
        loading?.cancel()
        loading=viewModelScope.launch {
            repository.data.catch { e ->
                if(e is CancellationException) throw e
                mutable.update { it.copy(restoring=false,storageError=true) }
            }.collect { data ->
                loaded=true
                mutable.update { s -> s.copy(learner=if(pending==0 && !s.storageError)data else s.learner,restoring=false) }
                finishStartup()
            }
        }
    }
    private fun finishStartup() {
        val s=mutable.value
        if(splashReady && s.contentReady && s.route==Route.Splash && !s.restoring && !s.storageError)
            navigate(if(s.learner.signedIn)Route.Home else if(s.learner.onboarded)Route.Phone else Route.Onboarding1)
    }
    private fun save(change: (Learner)->Learner) {
        val value=change(mutable.value.learner)
        pending++;mutable.update { it.copy(learner=value) };writes.trySend(value)
    }
    private fun sessionOwner(s: AvaState)=if(s.reviewing)"review:${s.reviewKeys.joinToString(",")}" else "lesson:${s.selectedLesson}"
    private fun restoreSession(owner: String,tasks: List<Task>,fallbackIndex: Int): Exercise =
        if(saved.get<String>("session_owner")==owner)ExerciseReducer.restore(saved["session_state"],tasks) else Exercise(fallbackIndex)
    private fun persistSession() {
        val s=mutable.value;if(s.route!=Route.Lesson)return
        saved["session_owner"]=sessionOwner(s);saved["session_state"]=s.exercise.snapshot().encode()
    }
    private fun clearSession() {saved.remove<String>("session_owner");saved.remove<String>("session_state")}
    fun retryStorage() {
        if(!loaded) { mutable.update { it.copy(storageError=false,restoring=true) };readData() }
        else { save { mutable.value.learner };readData() }
    }
    fun navigate(route: Route) {
        request?.cancel();ticker?.cancel()
        if(route==Route.Phone && !mutable.value.learner.onboarded) save { it.copy(onboarded=true) }
        mutable.update { it.copy(route=route,busy=false,code="",message=null,legal=null,confirmation=null,
            profileName=if(route==Route.EditProfile)it.learner.name else it.profileName,
            profileAvatar=if(route==Route.EditProfile)it.learner.avatar else it.profileAvatar) }
        if(route==Route.Otp) startTicker()
    }
    fun back(): Boolean {
        val s=mutable.value
        if(s.confirmation!=null) { confirm(null);return true }
        if(s.legal!=null) { legal(null);return true }
        when(s.route) {
            Route.Lesson -> if(s.exercise.complete)navigate(if(s.reviewing)Route.Practice else Route.Home) else confirm(Confirmation.ExitLesson)
            Route.StoryQuestion -> { mutable.update {it.copy(storyAnswer=null,storyChecked=false)};navigate(Route.StoryReader) }
            Route.StoryReader -> navigate(Route.StoryDetails)
            Route.StoryDetails,Route.StoryComplete -> navigate(Route.Library)
            Route.EditProfile,Route.Settings,Route.Help,Route.About,Route.Progress -> navigate(Route.Profile)
            Route.Privacy,Route.Terms -> navigate(Route.Settings)
            Route.Library,Route.Practice,Route.Profile,Route.LessonDetails -> navigate(Route.Home)
            Route.Otp -> navigate(Route.Phone)
            Route.Onboarding2 -> navigate(Route.Onboarding1)
            else -> return false
        }
        return true
    }
    fun phone(value: String) { val v=normalizePhone(value);saved["phone"]=v;mutable.update { it.copy(phone=v,message=null) } }
    fun consent(value: Boolean) { saved["consent"]=value;mutable.update { it.copy(consent=value,message=null) } }
    fun validatePhone() { if(mutable.value.phone.isNotEmpty() && !validPhone(mutable.value.phone)) mutable.update { it.copy(message="phone_error_invalid") } }
    fun legal(value: Boolean?) { mutable.update { it.copy(legal=value) } }
    fun offline(value: Boolean) { mutable.update { it.copy(offline=value) } }
    private suspend fun call(block: suspend ()->AuthResult) = try { block() } catch(e: CancellationException) { throw e } catch(_: Exception) { AuthResult.Network }
    private fun startTicker() { ticker?.cancel();ticker=viewModelScope.launch { while(mutable.value.route==Route.Otp) { mutable.update { it.copy(now=clock()) };delay(1000) } } }
    fun send(resend: Boolean=false) {
        val s=mutable.value
        if(s.busy || (resend && (s.resendSeconds>0 || s.locked))) return
        val error=when {s.offline->"phone_offline";!validPhone(s.phone)->"phone_error_invalid";!s.consent->"phone_consent_hint";else->null}
        if(error!=null) { mutable.update { it.copy(message=error) };return }
        mutable.update { it.copy(busy=true,message=null) }
        request=viewModelScope.launch {
            when(call { auth.request(s.phone) }) {
                AuthResult.Success -> { mutable.update { it.copy(route=Route.Otp,code="",busy=false,now=clock(),resendUntil=clock()+90_000,message=if(resend)"otp_resent" else null) };startTicker() }
                AuthResult.Locked -> mutable.update { it.copy(busy=false,now=clock(),lockedUntil=clock()+120_000,message="otp_locked") }
                else -> mutable.update { it.copy(busy=false,message="phone_provider_error") }
            }
        }
    }
    fun code(value: String) {
        val s=mutable.value;if(s.busy || s.locked || s.route!=Route.Otp)return
        val v=normalizeDigits(value).take(6);mutable.update { it.copy(code=v,message=null) }
        if(v.length==6)verify()
    }
    fun verify() {
        val s=mutable.value
        if(s.busy || s.locked || s.route!=Route.Otp || s.code.length!=6)return
        if(s.offline) { mutable.update { it.copy(message="app_otp_network") };return }
        mutable.update { it.copy(busy=true,message=null) }
        request=viewModelScope.launch {
            when(call {auth.verify(s.phone,s.code)}) {
                AuthResult.Success -> {mutable.update { it.copy(message="otp_success") };delay(600);save {it.copy(onboarded=true,signedIn=true)};navigate(Route.Home)}
                AuthResult.Wrong -> mutable.update {it.copy(busy=false,message="otp_wrong")}
                AuthResult.Expired -> mutable.update {it.copy(busy=false,message="otp_expired",resendUntil=0)}
                AuthResult.Locked -> mutable.update {it.copy(busy=false,message="otp_locked",now=clock(),lockedUntil=clock()+120_000)}
                AuthResult.Network -> mutable.update {it.copy(busy=false,message="app_otp_network")}
            }
        }
    }
    fun openLesson(id: String) { val s=mutable.value;if(!unlocked(id,s.learner.completed,s.lessons))return;mutable.update {it.copy(selectedLesson=id,reviewing=false)};navigate(Route.LessonDetails) }
    fun continueLearning() {
        val s=mutable.value;val d=s.learner
        val lesson=resumableLesson(d.activeLesson,d.completed,s.lessons) ?: nextLesson(d.completed,s.lessons)
        if(lesson==null) {navigate(Route.Library);return}
        mutable.update {it.copy(selectedLesson=lesson.id)};startLesson()
    }
    fun startLesson() {
        val s=mutable.value;if(!unlocked(s.selectedLesson,s.learner.completed,s.lessons))return
        val index=if(resumableLesson(s.learner.activeLesson,s.learner.completed,s.lessons)?.id==s.selectedLesson)
            s.learner.activeIndex.coerceIn(0,s.lesson.tasks.lastIndex) else 0
        val exercise=restoreSession("lesson:${s.selectedLesson}",s.lesson.tasks,index)
        navigate(Route.Lesson);mutable.update {it.copy(exercise=exercise,reviewing=false,reviewKeys=emptyList())};persistSession()
        save {it.copy(activeLesson=s.selectedLesson,activeIndex=exercise.index)}
    }
    fun choose(value: String) { mutable.update {if(it.route==Route.Lesson)it.copy(exercise=ExerciseReducer.choose(it.exercise,value,it.tasks)) else it};persistSession() }
    fun chooseToken(index: Int) { mutable.update {if(it.route==Route.Lesson)it.copy(exercise=ExerciseReducer.chooseToken(it.exercise,index,it.tasks)) else it};persistSession() }
    fun removeToken() { mutable.update {if(it.route==Route.Lesson)it.copy(exercise=ExerciseReducer.removeToken(it.exercise,it.tasks)) else it};persistSession() }
    fun revealHint() { mutable.update {if(it.route==Route.Lesson)it.copy(exercise=ExerciseReducer.revealHint(it.exercise,it.tasks)) else it};persistSession() }
    fun check() {
        val s=mutable.value;if(s.route!=Route.Lesson)return
        val result=ExerciseReducer.check(s.exercise,s.tasks);mutable.update {it.copy(exercise=result)};persistSession()
        if(result!=s.exercise && result.checked) {
            val key=if(s.reviewing)s.reviewKeys[s.exercise.index] else taskKey(s.selectedLesson,s.exercise.index,s.lessons)
            val seenAt=progressClock()
            save {learner->
                val progress=(learner.taskProgress[key]?:TaskProgress()).record(result.correct,s.exercise.hintVisible,seenAt)
                learner.copy(
                    mistakes=if(result.correct)learner.mistakes else learner.mistakes+key,
                    taskProgress=learner.taskProgress+(key to progress)
                )
            }
        }
    }
    fun advance() {
        val s=mutable.value;if(s.route!=Route.Lesson || s.exercise.complete)return
        val result=ExerciseReducer.advance(s.exercise,s.tasks);if(result==s.exercise)return
        mutable.update {it.copy(exercise=result)}
        if(s.reviewing && s.exercise.correct)save {it.copy(mistakes=it.mistakes-taskAliases(s.reviewKeys[s.exercise.index],s.lessons))}
        if(result.complete) {
            if(s.reviewing)save {it.copy(reviews=it.reviews+1)}
            else save {it.copy(completed=it.completed+s.selectedLesson,activeLesson="",activeIndex=0)}
            clearSession()
        } else {
            persistSession()
            if(!s.reviewing)save {it.copy(activeLesson=s.selectedLesson,activeIndex=result.index)}
        }
    }
    fun review(mode: ReviewMode) {
        val d=mutable.value.learner
        val keys=plannedReviewKeys(mode,d.completed,d.mistakes,d.reviews,lessons=mutable.value.lessons)
        if(keys.isEmpty())return
        val owner="review:${keys.joinToString(",")}";val exercise=restoreSession(owner,keys.mapNotNull {taskForKey(it,mutable.value.lessons)},0)
        navigate(Route.Lesson);mutable.update {it.copy(reviewing=true,reviewKeys=keys,exercise=exercise)};persistSession()
    }
    fun openStory(id: String) { if(mutable.value.stories.none {it.id==id})return;mutable.update {it.copy(selectedStory=id,storyAnswer=null,storyChecked=false)};navigate(Route.StoryDetails) }
    fun startStory() {
        val s=mutable.value
        val page=if(s.selectedStory in s.learner.stories)0 else (s.learner.positions[s.selectedStory]?:0).coerceIn(0,s.story.pages.lastIndex)
        mutable.update {it.copy(storyPage=page,storyAnswer=null,storyChecked=false)}
        // Presence in positions distinguishes an opened story from an untouched one in the library.
        if(s.selectedStory !in s.learner.positions)save {it.copy(positions=it.positions+(s.selectedStory to page))}
        navigate(Route.StoryReader)
    }
    fun page(next: Boolean) {
        val s=mutable.value;if(s.route!=Route.StoryReader)return
        if(next && s.storyPage==s.story.pages.lastIndex) {navigate(Route.StoryQuestion);return}
        val i=(s.storyPage+if(next)1 else -1).coerceIn(0,s.story.pages.lastIndex)
        mutable.update {it.copy(storyPage=i)};save {it.copy(positions=it.positions+(s.selectedStory to i))}
    }
    fun storyAnswer(value: String) { mutable.update {
        if(it.route==Route.StoryQuestion && !(it.storyChecked && it.storyAnswer==it.story.answer) && value in it.story.choices)it.copy(storyAnswer=value,storyChecked=false) else it
    } }
    fun checkStory() {
        val s=mutable.value;if(s.route!=Route.StoryQuestion || s.storyAnswer==null)return
        mutable.update {it.copy(storyChecked=true)}
    }
    fun finishStory() {
        val s=mutable.value
        if(s.route==Route.StoryQuestion && s.storyChecked && s.storyAnswer==s.story.answer) {
            save {it.copy(stories=it.stories+s.selectedStory,positions=it.positions+(s.selectedStory to 0))}
            navigate(Route.StoryComplete)
        }
    }
    fun editName(value: String) {mutable.update {it.copy(profileName=value.filterNot(Char::isISOControl).take(24))}}
    fun avatar(value: Int) {mutable.update {it.copy(profileAvatar=value.coerceIn(0,3))}}
    fun saveProfile() {val s=mutable.value;save {it.copy(name=s.profileName.trim(),avatar=s.profileAvatar)};navigate(Route.Profile)}
    fun setting(key: String, value: Boolean) {save {when(key) {"narration"->it.copy(narration=value);"sounds"->it.copy(sounds=value);"motion"->it.copy(reducedMotion=value);else->it}}}
    fun confirm(value: Confirmation?) {mutable.update {it.copy(confirmation=value)}}
    fun confirmAction() {
        when(mutable.value.confirmation) {
            Confirmation.ExitLesson -> navigate(if(mutable.value.reviewing)Route.Practice else Route.Home)
            Confirmation.ResetProgress -> {clearSession();save {it.copy(completed=emptySet(),stories=emptySet(),mistakes=emptySet(),reviews=0,positions=emptyMap(),activeLesson="",activeIndex=0,taskProgress=emptyMap())};confirm(null)}
            Confirmation.Logout -> {auth.reset();save {it.copy(signedIn=false)};navigate(Route.Phone)}
            null -> Unit
        }
    }
    fun notice(value: String?) {mutable.update {it.copy(notice=value)}}
}
