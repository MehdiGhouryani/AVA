package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

private val roots=listOf(Route.Home,Route.Library,Route.Practice,Route.Profile)
/** Reduced motion is on when the system animator scale is off (0) or halved (0.5x): both mean the user asked animations to slow down or stop. */
private fun isSystemMotionOff(context: android.content.Context)=android.provider.Settings.Global.getFloat(context.contentResolver,android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1f)<=0.5f
@Composable fun AvaApp(s: AvaState,a: AvaActions) {
    val rootsState=key(s.learner.signedIn) {rememberSaveableStateHolder()}
    val context=androidx.compose.ui.platform.LocalContext.current
    var systemMotionOff by remember {mutableStateOf(isSystemMotionOff(context))}
    DisposableEffect(context) {
        val observer=object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {systemMotionOff=isSystemMotionOff(context)}
        }
        context.contentResolver.registerContentObserver(android.provider.Settings.Global.getUriFor(android.provider.Settings.Global.ANIMATOR_DURATION_SCALE),false,observer)
        onDispose {context.contentResolver.unregisterContentObserver(observer)}
    }
    CompositionLocalProvider(LocalReducedMotion provides (s.learner.reducedMotion || systemMotionOff)) {
        Scaffold(modifier=Modifier.fillMaxSize().imePadding(),containerColor=AvaColors.paper,contentWindowInsets=WindowInsets.safeDrawing,
            bottomBar={if(s.route in roots)NavigationBar(containerColor=AvaColors.cream,tonalElevation=0.dp) {
                val icons=listOf(Icons.Outlined.Home,Icons.Outlined.AutoStories,Icons.Outlined.PublishedWithChanges,Icons.Outlined.PersonOutline)
                listOf("garden","library","practice","me").forEachIndexed {i,key->NavigationBarItem(selected=s.route==roots[i],onClick={a.navigate(roots[i])},icon={Icon(icons[i],null)},label={Text(txt(key))},
                    colors=NavigationBarItemDefaults.colors(selectedIconColor=AvaColors.green,selectedTextColor=AvaColors.deep,indicatorColor=AvaColors.line,unselectedIconColor=AvaColors.green,unselectedTextColor=AvaColors.soft))}
            }}) {padding->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if(s.storageError)Surface(color=AvaColors.amberTint) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {Text(txt("storage_error"),color=AvaColors.amber);TextButton(a.retryStorage,Modifier.heightIn(min=64.dp)) {Text(txt("retry"))}}
                }
                Box(Modifier.weight(1f)) {
                    if(s.route in roots) rootsState.SaveableStateProvider(s.route.name) {RouteContent(s,a)}
                    else RouteContent(s,a)
                }
            }
        }
        s.legal?.let {privacy->AlertDialog(onDismissRequest={a.legal(null)},title={Text(txt(if(privacy)"privacy" else "terms"))},text={Text(txt(if(privacy)"privacy_body" else "terms_body"))},confirmButton={TextButton({a.legal(null)},Modifier.heightIn(min=64.dp)) {Text(txt("close"))}})}
        s.confirmation?.let {kind->
            val key=when(kind) {Confirmation.ExitLesson->"exit";Confirmation.ResetProgress->"reset";Confirmation.Logout->"logout"}
            AlertDialog(onDismissRequest={a.confirm(null)},title={Text(txt(key+"_title"))},text={Text(txt(key+"_body"))},
                // The confirm slot is the primary action (filled, 72 dp); the
                // dismiss slot is the secondary one. They were swapped: cancel
                // sat in the primary slot, so in RTL the left button read
                // "فعلاً نه" and a wrong tap could reset a child's progress.
                confirmButton={Button(a.confirmAction,Modifier.heightIn(min=72.dp)) {Text(txt("yes"))}},dismissButton={OutlinedButton({a.confirm(null)},Modifier.heightIn(min=64.dp)) {Text(txt("cancel"))}})
        }
        s.notice?.let {message->AlertDialog(onDismissRequest={a.notice(null)},text={Text(txt(message))},confirmButton={TextButton({a.notice(null)},Modifier.heightIn(min=64.dp)) {Text(txt("close"))}})}
    }
}

@Composable private fun RouteContent(s: AvaState,a: AvaActions) {
    when(s.route) {
        Route.Splash -> SplashScreen(s,a)
        Route.Onboarding1,Route.Onboarding2 -> OnboardingScreen(s,a)
        Route.Phone -> PhoneScreen(s,a)
        Route.Otp -> OtpScreen(s,a)
        Route.Home -> HomeScreen(s,a)
        Route.LessonDetails -> LessonDetailsScreen(s,a)
        Route.Lesson -> LessonScreen(s,a)
        Route.Library -> LibraryScreen(s,a)
        Route.StoryDetails -> StoryDetailsScreen(s,a)
        Route.StoryReader -> StoryReaderScreen(s,a)
        Route.StoryQuestion -> StoryQuestionScreen(s,a)
        Route.StoryComplete -> StoryCompleteScreen(s,a)
        Route.Practice -> PracticeScreen(s,a)
        Route.Profile -> ProfileScreen(s,a)
        Route.EditProfile -> EditProfileScreen(s,a)
        Route.Progress -> ProgressScreen(s,a)
        Route.Album -> AlbumScreen(s,a)
        Route.Settings -> SettingsScreen(s,a)
        Route.Help,Route.About,Route.Privacy,Route.Terms -> InformationScreen(s,a)
    }
}
