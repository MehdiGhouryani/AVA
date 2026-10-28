package com.ava.reading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.*
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ava.reading.platform.AndroidNarrator
import com.ava.reading.content.AudioPlaybackResult
import com.ava.reading.storage.*
import com.ava.reading.ui.*
import com.ava.reading.ui.screens.AvaApp
import com.ava.reading.ui.theme.AvaTheme

class MainActivity : ComponentActivity() {
    private val model: AvaViewModel by viewModels {viewModelFactory {initializer {
        AvaViewModel(createSavedStateHandle(),repository=DataStoreLearnerRepository(applicationContext.learnerStore))
    }}}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val s by model.state.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                if(BuildConfig.DEBUG) {
                    intent.getStringExtra("qa_route")?.let { raw -> runCatching {Route.valueOf(raw)}.getOrNull()?.let(model::navigate) }
                    if(intent.getBooleanExtra("qa_offline",false))model.offline(true)
                }
            }
            val narrator=remember {AndroidNarrator(applicationContext)}
            val owner=LocalLifecycleOwner.current
            DisposableEffect(owner,narrator) {
                val observer=LifecycleEventObserver {_,event->if(event==Lifecycle.Event.ON_STOP)narrator.stop()}
                owner.lifecycle.addObserver(observer)
                onDispose {owner.lifecycle.removeObserver(observer);narrator.close()}
            }
            LaunchedEffect(s.route,s.storyPage,s.learner.narration,s.exercise.index,s.exercise.complete) {narrator.stop()}
            val base=model.actions()
            val actions=base.copy(listen={request->if(s.learner.narration && narrator.play(request)==AudioPlaybackResult.Missing)model.notice("app_no_voice")},stop=narrator::stop,
                check={val before=model.state.value.exercise;model.check();val after=model.state.value.exercise;if(!before.checked && after.checked && after.correct && s.learner.sounds)narrator.celebrate()},
                checkStory={val before=model.state.value;model.checkStory();val after=model.state.value
                    if(!before.storyChecked && after.storyChecked && after.storyAnswer==after.story.answer && s.learner.sounds)narrator.celebrate()})
            val handlesBack=s.legal!=null || s.confirmation!=null || s.route !in listOf(Route.Splash,Route.Home,Route.Onboarding1,Route.Phone)
            BackHandler(handlesBack) {model.back()}
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {AvaTheme {AvaApp(s,actions)}}
        }
    }
}
