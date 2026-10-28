package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

@Composable fun LibraryScreen(s: AvaState,a: AvaActions) {
    Page(txt("library_title")) {
        Text(txt("library_body"),color=AvaColors.soft)
        s.stories.forEach {story ->
            val completed=story.id in s.learner.stories
            val saved=s.learner.positions[story.id]
            val position=(saved?:0).coerceIn(0,story.pages.lastIndex)
            val status=txt(when {completed->"story_read";saved!=null->"story_in_progress";else->"story_unread"})
            Surface(onClick={a.openStory(story.id)},shape=RoundedCornerShape(28.dp),color=AvaColors.cream,shadowElevation=3.dp,
                border=BorderStroke(1.dp,AvaColors.line),modifier=Modifier.fillMaxWidth().semantics {stateDescription=status}) {
                BoxWithConstraints {
                    val compact=maxWidth<340.dp || LocalDensity.current.fontScale>1.15f
                    if(compact) Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        StoryCover(story.art,completed,Modifier.fillMaxWidth().height(104.dp))
                        StoryCardCopy(story,status,completed,saved,position)
                    } else Row(Modifier.padding(18.dp).heightIn(min=124.dp),horizontalArrangement=Arrangement.spacedBy(16.dp),verticalAlignment=Alignment.CenterVertically) {
                        StoryCover(story.art,completed,Modifier.size(96.dp))
                        StoryCardCopy(story,status,completed,saved,position,Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable private fun StoryCover(art: String,completed: Boolean,modifier: Modifier) {
    Box(modifier.background(AvaColors.surface,RoundedCornerShape(24.dp)),contentAlignment=Alignment.Center) {
        ArtImage(art,Modifier.fillMaxSize().padding(8.dp))
        if(completed) Surface(Modifier.align(Alignment.TopEnd).padding(8.dp),shape=RoundedCornerShape(50),color=AvaColors.green) {
            Icon(Icons.Outlined.Check,null,Modifier.padding(5.dp).size(18.dp),tint=MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable private fun StoryCardCopy(story: Story,status: String,completed: Boolean,saved: Int?,position: Int,modifier: Modifier=Modifier) {
    Column(modifier,verticalArrangement=Arrangement.spacedBy(7.dp)) {
        Text(story.title,style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
        Text(story.subtitle,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
        Surface(shape=RoundedCornerShape(50),color=if(completed)AvaColors.line else AvaColors.surface) {
            Text(status,Modifier.padding(horizontal=12.dp,vertical=5.dp),style=MaterialTheme.typography.labelMedium,color=AvaColors.deep)
        }
        when {
            completed -> Text(txt("story_pages","count" to fa(story.pages.size)),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
            saved!=null -> {
                LinearProgressIndicator(progress={(position+1f)/story.pages.size},modifier=Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
                Text(txt("story_resume_page","current" to fa(position+1),"total" to fa(story.pages.size)),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
            }
            else -> Text(txt("story_pages","count" to fa(story.pages.size)),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
        }
    }
}

@Composable fun StoryDetailsScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        val completed=s.selectedStory in s.learner.stories
        val saved=s.learner.positions[s.selectedStory]
        val position=(saved?:0).coerceIn(0,s.story.pages.lastIndex)
        Page(s.story.title,a.back) {
            ArtImage(s.story.art,Modifier.fillMaxWidth().height(if(compact)112.dp else 210.dp))
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                Title(s.story.subtitle)
                StoryFact(Icons.Outlined.AutoStories,txt("story_pages","count" to fa(s.story.pages.size)))
                StoryFact(Icons.Outlined.Quiz,txt("story_question_hint"))
                StoryFact(Icons.AutoMirrored.Outlined.VolumeUp,txt("story_audio_hint"))
                when {
                    completed -> Text(txt("story_read_body"),color=AvaColors.deep)
                    saved!=null -> {
                        LinearProgressIndicator(progress={(position+1f)/s.story.pages.size},modifier=Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
                        Text(txt("story_resume_page","current" to fa(position+1),"total" to fa(s.story.pages.size)),color=AvaColors.deep)
                    }
                }
                Primary(txt(when {completed->"reread_story";saved!=null->"continue_story";else->"read_story"}),a.startStory)
            }
        }
    }
}

@Composable private fun StoryFact(icon: androidx.compose.ui.graphics.vector.ImageVector,value: String) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        Icon(icon,null,Modifier.size(22.dp),tint=AvaColors.green)
        Text(value,Modifier.weight(1f),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
    }
}

@Composable fun StoryReaderScreen(s: AvaState,a: AvaActions) {
    key(s.selectedStory,s.storyPage) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
            val last=s.storyPage==s.story.pages.lastIndex
            Column(Modifier.fillMaxSize().padding(horizontal=if(compact)16.dp else 20.dp).padding(bottom=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Header(s.story.title,a.back)
                val progress=txt("story_page","current" to fa(s.storyPage+1),"total" to fa(s.story.pages.size))
                Text(progress,Modifier.fillMaxWidth().semantics {liveRegion=LiveRegionMode.Polite},textAlign=TextAlign.End,style=MaterialTheme.typography.bodySmall,color=AvaColors.deep)
                LinearProgressIndicator(progress={(s.storyPage+1f)/s.story.pages.size},modifier=Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    ArtImage("char.ava.reading",Modifier.fillMaxWidth().height(if(compact)108.dp else 196.dp))
                    SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                        Text(s.story.pages[s.storyPage],Modifier.semantics {heading()},style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
                    }
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        val page=s.story.pageItems[s.storyPage]
                        TextButton({a.listen(AudioRequest(page.audioRef,page.text,AudioPurpose.Story))},enabled=s.learner.narration,modifier=Modifier.heightIn(min=48.dp)) {
                            Icon(Icons.AutoMirrored.Outlined.VolumeUp,null);Spacer(Modifier.width(8.dp));Text(txt("listen"))
                        }
                        TextButton(a.stop,enabled=s.learner.narration,modifier=Modifier.heightIn(min=48.dp)) {Icon(Icons.Outlined.Stop,null);Spacer(Modifier.width(6.dp));Text(txt("stop"))}
                    }
                    if(!s.learner.narration) Text(txt("story_audio_off"),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                }
                Primary(txt(if(last)"story_to_question" else "next_page"),{a.page(true)})
                TextButton({a.page(false)},enabled=s.storyPage>0,modifier=Modifier.align(Alignment.CenterHorizontally).heightIn(min=48.dp)) {Text(txt("previous_page"))}
            }
        }
    }
}

@Composable fun StoryQuestionScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        val correct=s.storyChecked&&s.storyAnswer==s.story.answer
        Column(Modifier.fillMaxSize().padding(horizontal=if(compact)16.dp else 20.dp).padding(bottom=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Header(txt("story_question"),a.back)
            Text(txt("story_question_progress"),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.deep)
            LinearProgressIndicator(progress={if(correct)1f else .9f},modifier=Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                ArtImage("char.ava.head_happy",Modifier.fillMaxWidth().height(if(compact)92.dp else 150.dp))
                Title(s.story.question)
                Column(Modifier.fillMaxWidth().selectableGroup(),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    s.story.choices.forEach {value->StoryChoice(value,value==s.storyAnswer,!correct,{a.storyAnswer(value)})}
                }
                TextButton(a.startStory,Modifier.align(Alignment.CenterHorizontally).heightIn(min=48.dp)) {Text(txt("back_story"))}
                Spacer(Modifier.height(4.dp))
            }
            StoryQuestionFeedback(s.storyChecked,correct,s.storyAnswer!=null)
            if(correct)Primary(txt("finish_story"),a.finishStory) else Primary(txt("check"),a.checkStory,enabled=s.storyAnswer!=null)
        }
    }
}

@Composable private fun StoryChoice(value: String,selected: Boolean,enabled: Boolean,action: ()->Unit) {
    Surface(Modifier.fillMaxWidth().selectable(selected=selected,enabled=enabled,role=Role.RadioButton,onClick=action),shape=RoundedCornerShape(22.dp),color=if(selected)AvaColors.line else AvaColors.surface,
        border=BorderStroke(if(selected)2.dp else 1.dp,if(selected)AvaColors.green else AvaColors.line)) {
        Box(contentAlignment=Alignment.Center) {
            Text(value,Modifier.padding(horizontal=16.dp,vertical=12.dp).heightIn(min=40.dp),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep,textAlign=TextAlign.Center)
            if(selected)Icon(Icons.Outlined.RadioButtonChecked,null,tint=AvaColors.green,modifier=Modifier.align(Alignment.TopEnd).padding(6.dp).size(16.dp))
        }
    }
}

@Composable private fun StoryQuestionFeedback(checked: Boolean,correct: Boolean,selected: Boolean) {
    if(!checked) Text(txt(if(selected)"selected_hint" else "select_hint"),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
    else Surface(shape=RoundedCornerShape(20.dp),color=if(correct)AvaColors.line else AvaColors.amberTint) {
        Row(Modifier.fillMaxWidth().padding(12.dp).semantics(mergeDescendants=true) {liveRegion=LiveRegionMode.Polite},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Icon(if(correct)Icons.Outlined.Check else Icons.Outlined.Refresh,null,tint=if(correct)AvaColors.deep else AvaColors.amber)
            Text(txt(if(correct)"story_good" else "story_retry"),Modifier.weight(1f),style=MaterialTheme.typography.bodySmall,color=if(correct)AvaColors.deep else AvaColors.amber)
        }
    }
}

@Composable fun StoryCompleteScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        Page(txt("story_complete"),a.back) {
            ArtImage("char.ava.wave_book",Modifier.fillMaxWidth().height(if(compact)72.dp else 230.dp))
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)12.dp else 20.dp,gap=if(compact)8.dp else 12.dp) {
                Title(s.story.title)
                Text(txt("story_complete_body"),Modifier.fillMaxWidth(),color=AvaColors.soft,textAlign=TextAlign.Center)
                Text(txt("story_completion_summary","count" to fa(s.story.pages.size)),Modifier.fillMaxWidth(),color=AvaColors.deep,textAlign=TextAlign.Center)
                Primary(txt("back_library"),{a.navigate(Route.Library)})
                Secondary(txt("reread_story"),a.startStory)
            }
        }
    }
}
