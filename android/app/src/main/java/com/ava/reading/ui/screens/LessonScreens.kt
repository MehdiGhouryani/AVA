package com.ava.reading.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ava.reading.content.*
import com.ava.reading.ui.AvaActions
import com.ava.reading.ui.AvaState
import com.ava.reading.ui.Route
import com.ava.reading.ui.theme.AvaColors

private fun kindKey(kind: TaskKind)=when(ActivityRegistry[kind].family) {
    ActivityFamily.Visual->"activity_recognize"
    ActivityFamily.Listening->"activity_listen"
    ActivityFamily.WordConstruction->"activity_build"
}

@Composable fun LessonDetailsScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        val resuming=resumableLesson(s.learner.activeLesson,s.learner.completed,s.lessons)?.id==s.selectedLesson
        val recognize=s.lesson.tasks.count {ActivityRegistry[it.kind].family==ActivityFamily.Visual}
        val listen=s.lesson.tasks.count {ActivityRegistry[it.kind].family==ActivityFamily.Listening}
        val build=s.lesson.tasks.count {ActivityRegistry[it.kind].family==ActivityFamily.WordConstruction}
        Page(txt("lesson_details"),a.back) {
            ArtImage("char.ava.reading",Modifier.fillMaxWidth().height(if(compact)112.dp else 180.dp))
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                Title(s.lesson.title)
                Text(s.lesson.goal,color=AvaColors.soft)
                Text(txt("activities","count" to fa(s.lesson.tasks.size)),style=MaterialTheme.typography.bodySmall)
                Text(txt("lesson_variety","recognize" to fa(recognize),"listen" to fa(listen),"build" to fa(build)),color=AvaColors.deep,style=MaterialTheme.typography.bodySmall)
                Text(txt("lesson_preview"),color=AvaColors.deep,style=MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    s.lesson.tasks.forEach {task ->
                        Surface(Modifier.weight(1f),shape=RoundedCornerShape(18.dp),color=AvaColors.surface) {
                            Column(Modifier.padding(horizontal=8.dp,vertical=10.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
                                Text(task.target,Modifier.heightIn(min=32.dp),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep,textAlign=TextAlign.Center)
                                Text(txt(kindKey(task.kind)),style=MaterialTheme.typography.labelSmall,color=AvaColors.soft,textAlign=TextAlign.Center)
                            }
                        }
                    }
                }
                if(resuming) Text(txt("resume_step","current" to fa(s.learner.activeIndex.coerceIn(0,s.lesson.tasks.lastIndex)+1),"total" to fa(s.lesson.tasks.size)),color=AvaColors.deep)
                Primary(txt(if(resuming)"resume" else if(s.selectedLesson in s.learner.completed)"replay" else "start"),a.startLesson)
            }
        }
    }
}

@Composable fun LessonScreen(s: AvaState,a: AvaActions) {
    if(s.exercise.complete) LessonResultScreen(s,a) else LessonQuestionScreen(s,a)
}

@Composable private fun LessonQuestionScreen(s: AvaState,a: AvaActions) {
    val e=s.exercise
    val task=s.tasks.getOrNull(e.index)?:return
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxWidth<360.dp || maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        Column(Modifier.fillMaxSize().padding(horizontal=if(compact)16.dp else 20.dp).padding(bottom=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Header(if(s.reviewing)txt("practice_title") else s.lesson.title,a.back)
            Text(txt("exercise_progress","current" to fa(e.index+1),"total" to fa(s.tasks.size)),color=AvaColors.deep,style=MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(progress={(e.index+if(e.checked&&e.correct)1f else 0f)/s.tasks.size},modifier=Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),trackColor=AvaColors.line,color=AvaColors.green)
            key(s.selectedLesson,s.reviewing,e.index) {
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    val definition=ActivityRegistry[task.kind]
                    when {
                        definition.interaction==ActivityInteraction.TokenBuild->BuildWordTask(task,e,compact,a)
                        definition.supportsHint->ListenTask(task,e,s.learner.narration,compact,a)
                        else->RecognizeTask(task,e,s.learner.narration,compact,a)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            LessonFeedback(task,e,ExerciseReducer.response(e,s.tasks)!=null)
            if(e.checked&&e.correct)Primary(txt(if(e.index==s.tasks.lastIndex)"finish_lesson" else "next"),a.advance)
            else Primary(txt("check"),a.check,enabled=ExerciseReducer.canCheck(e,s.tasks))
        }
    }
}

@Composable private fun RecognizeTask(task: Task,e: Exercise,narration: Boolean,compact: Boolean,a: AvaActions) {
    Title(txt(if(task.target.length>1)"word_choose" else "choose"))
    TargetCard(task.target,task.audioRef,narration,compact,a)
    ChoiceGroup(task,e,compact,a)
}

@Composable private fun TargetCard(target: String,audioRef: AudioRef?,narration: Boolean,compact: Boolean,a: AvaActions,showListen: Boolean=true) {
    SoftCard(Modifier.fillMaxWidth(),padding=if(compact)12.dp else 20.dp,gap=4.dp) {
        Text(txt("target"),Modifier.align(Alignment.CenterHorizontally),style=MaterialTheme.typography.bodySmall)
        Text(target,Modifier.align(Alignment.CenterHorizontally),fontSize=if(target.length>1)48.sp else 76.sp,color=AvaColors.deep,textAlign=TextAlign.Center)
        if(showListen) TextButton({a.listen(AudioRequest(audioRef,target,AudioPurpose.Prompt))},Modifier.align(Alignment.CenterHorizontally).heightIn(min=48.dp),enabled=narration) {
            Icon(Icons.AutoMirrored.Outlined.VolumeUp,null);Spacer(Modifier.width(8.dp));Text(txt("listen"))
        }
    }
}

@Composable private fun ListenTask(task: Task,e: Exercise,narration: Boolean,compact: Boolean,a: AvaActions) {
    Title(txt("listen_choose"))
    SoftCard(Modifier.fillMaxWidth(),padding=if(compact)14.dp else 20.dp,gap=8.dp) {
        Icon(Icons.AutoMirrored.Outlined.VolumeUp,null,Modifier.align(Alignment.CenterHorizontally).size(48.dp),tint=AvaColors.green)
        Text(txt("listen_instruction"),Modifier.fillMaxWidth(),color=AvaColors.deep,textAlign=TextAlign.Center)
        val fallback=task.writtenFallback?:task.target
        Secondary(txt(if(e.hintVisible)"listen_again" else "listen"),{a.listen(AudioRequest(task.audioRef,fallback,AudioPurpose.Prompt))},enabled=narration)
        if(!narration) Text(txt("audio_optional"),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
        if(e.hintVisible) {
            Text(txt("listen_hint_label"),Modifier.align(Alignment.CenterHorizontally),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
            Text(fallback,Modifier.align(Alignment.CenterHorizontally).semantics {heading()},fontSize=if(fallback.length>1)48.sp else 64.sp,color=AvaColors.deep,textAlign=TextAlign.Center)
        } else TextButton(a.revealHint,Modifier.align(Alignment.CenterHorizontally).heightIn(min=48.dp)) {Text(txt("show_hint"))}
    }
    ChoiceGroup(task,e,compact,a)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun BuildWordTask(task: Task,e: Exercise,compact: Boolean,a: AvaActions) {
    Title(txt("build_word"))
    TargetCard(task.target,null,false,compact,a,showListen=false)
    val answer=e.selectedTokens.joinToString("") {task.tokens[it]}
    val answerState=if(answer.isEmpty())txt("answer_empty") else txt("answer_value","value" to answer)
    Surface(Modifier.fillMaxWidth().semantics {stateDescription=answerState},shape=RoundedCornerShape(22.dp),color=AvaColors.surface,border=BorderStroke(2.dp,if(e.checked&&!e.correct)AvaColors.amber else AvaColors.line)) {
        Text(if(answer.isEmpty())"…" else answer,Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=18.dp).heightIn(min=48.dp),fontSize=40.sp,color=AvaColors.deep,textAlign=TextAlign.Center)
    }
    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp,Alignment.CenterHorizontally),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        task.tokens.forEachIndexed {index,value->
            val used=index in e.selectedTokens
            val tokenState=txt(if(used)"token_used" else "token_available")
            OutlinedButton({a.chooseToken(index)},Modifier.widthIn(min=64.dp).heightIn(min=56.dp).semantics {stateDescription=tokenState},enabled=!used&&!(e.checked&&e.correct),shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,if(used)AvaColors.line else AvaColors.green)) {
                Text(value,fontSize=30.sp,color=if(used)AvaColors.soft else AvaColors.deep)
            }
        }
    }
    TextButton(a.removeToken,Modifier.fillMaxWidth().heightIn(min=48.dp),enabled=e.selectedTokens.isNotEmpty()&&!(e.checked&&e.correct)) {
        Icon(Icons.Outlined.Refresh,null);Spacer(Modifier.width(8.dp));Text(txt("remove_last"))
    }
}

@Composable private fun ChoiceGroup(task: Task,e: Exercise,compact: Boolean,a: AvaActions) {
    val horizontal=!compact && task.choices.all {it.length==1}
    if(horizontal) {
        Row(Modifier.fillMaxWidth().selectableGroup(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            task.choices.forEach {value->LessonChoice(value,value==e.selected,!(e.checked&&e.correct),{a.choose(value)},Modifier.weight(1f))}
        }
    } else {
        Column(Modifier.fillMaxWidth().selectableGroup(),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            task.choices.forEach {value->LessonChoice(value,value==e.selected,!(e.checked&&e.correct),{a.choose(value)},Modifier.fillMaxWidth())}
        }
    }
}

@Composable private fun LessonChoice(value: String,selected: Boolean,enabled: Boolean,action: ()->Unit,modifier: Modifier) {
    Surface(modifier.selectable(selected=selected,enabled=enabled,role=Role.RadioButton,onClick=action),shape=RoundedCornerShape(22.dp),color=if(selected)AvaColors.line else AvaColors.surface,
        border=BorderStroke(if(selected)2.dp else 1.dp,if(selected)AvaColors.green else AvaColors.line)) {
        Box(contentAlignment=Alignment.Center) {
            Text(value,Modifier.padding(horizontal=16.dp,vertical=12.dp).heightIn(min=40.dp),fontSize=if(value.length>1)28.sp else 34.sp,color=AvaColors.deep,textAlign=TextAlign.Center)
            if(selected)Icon(Icons.Outlined.RadioButtonChecked,null,tint=AvaColors.green,modifier=Modifier.align(Alignment.TopEnd).padding(6.dp).size(16.dp))
        }
    }
}

@Composable private fun LessonFeedback(task: Task,e: Exercise,hasResponse: Boolean) {
    if(!e.checked) {
        val key=when {
            !hasResponse && ActivityRegistry[task.kind].interaction==ActivityInteraction.TokenBuild->"build_more_hint"
            hasResponse && ActivityRegistry[task.kind].interaction==ActivityInteraction.TokenBuild->"build_ready_hint"
            !hasResponse->"select_hint"
            else->"selected_hint"
        }
        Text(txt(key),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
    } else {
        val message=when {
            e.correct->"correct"
            ActivityRegistry[task.kind].family==ActivityFamily.Listening->"wrong_listen"
            ActivityRegistry[task.kind].interaction==ActivityInteraction.TokenBuild->"wrong_build"
            else->"wrong"
        }
        Surface(shape=RoundedCornerShape(20.dp),color=if(e.correct)AvaColors.line else AvaColors.amberTint) {
            Row(Modifier.fillMaxWidth().padding(12.dp).semantics(mergeDescendants=true) {liveRegion=LiveRegionMode.Polite},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Icon(if(e.correct)Icons.Outlined.Check else Icons.Outlined.Refresh,null,tint=if(e.correct)AvaColors.deep else AvaColors.amber)
                Text(txt(message),Modifier.weight(1f),style=MaterialTheme.typography.bodySmall,color=if(e.correct)AvaColors.deep else AvaColors.amber)
            }
        }
    }
}

@Composable private fun LessonResultScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        val next=nextLesson(s.learner.completed,s.lessons)
        val allDone=s.lessons.all {it.id in s.learner.completed}
        Page(if(s.reviewing)txt("practice_title") else s.lesson.title,a.back) {
            ArtImage("char.ava.wave_book",Modifier.fillMaxWidth().height(if(compact)130.dp else 210.dp))
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                Title(txt(if(s.reviewing)"review_done" else "lesson_complete"))
                Text(txt(if(s.reviewing)"review_body" else "lesson_complete_body"),color=AvaColors.soft)
                Text(txt("completion_summary","count" to fa(s.tasks.size)),color=AvaColors.deep)
                if(!s.reviewing && next!=null)Text(txt("up_next","title" to next.title),color=AvaColors.deep)
                when {
                    s.reviewing -> Primary(txt("practice"),{a.navigate(Route.Practice)})
                    allDone -> Primary(txt("library"),{a.navigate(Route.Library)})
                    next!=null -> Primary(txt("next_lesson"),{a.openLesson(next.id)})
                    else -> Primary(txt("home"),{a.navigate(Route.Home)})
                }
                Secondary(txt("home"),{a.navigate(Route.Home)})
                if(!s.reviewing) TextButton(a.startLesson,Modifier.align(Alignment.CenterHorizontally).heightIn(min=48.dp)) {Text(txt("replay"))}
            }
        }
    }
}
