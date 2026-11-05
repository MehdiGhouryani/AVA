package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

@Composable fun HomeScreen(s: AvaState,a: AvaActions) {
    val resumable=resumableLesson(s.learner.activeLesson,s.learner.completed,s.lessons)
    val next=resumable ?: nextLesson(s.learner.completed,s.lessons)
    val completed=knownLessonCount(s.learner.completed,s.lessons)
    Scene("bg.garden") {
        BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxWidth<360.dp || LocalDensity.current.fontScale>1.15f
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(if(compact)16.dp else 20.dp),verticalArrangement=Arrangement.spacedBy(if(compact)12.dp else 16.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(if(compact)8.dp else 16.dp),verticalAlignment=Alignment.CenterVertically) {
                ArtImage("brand.logo",Modifier.height(if(compact)48.dp else 64.dp).width(if(compact)32.dp else 44.dp),txt("brand_name"))
                SoftCard(Modifier.weight(1f),padding=12.dp,gap=0.dp) {
                    Text(txt("greeting","name" to s.learner.name.ifBlank {txt("friend")}),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep,maxLines=if(compact)2 else Int.MAX_VALUE,overflow=TextOverflow.Ellipsis)
                    Text(txt("completed_count","count" to fa(completed),"total" to fa(s.lessons.size)),style=MaterialTheme.typography.bodySmall,color=AvaColors.deep)
                }
                IconButton({a.navigate(Route.Profile)},Modifier.size(64.dp)) {ArtImage(avatarArt(s.learner.avatar),Modifier.fillMaxSize(),txt("profile_title"))}
            }
            SoftCard(padding=if(compact)16.dp else 20.dp) {
                // Keep illustration on physical left, matching the supplied reference in RTL.
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text(txt(if(next==null)"all_done" else "hero_title"),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
                        Text(next?.title?:txt("all_done_body"),color=AvaColors.deep)
                    }
                    ArtImage("char.ava.wave_book",Modifier.width(if(compact)72.dp else 104.dp).height(if(compact)96.dp else 112.dp))
                }
                Primary(txt(if(next==null)"library" else if(resumable!=null)"resume" else "start"),{if(next==null)a.navigate(Route.Library) else a.continueLearning()})
                if(next==null)s.lessons.firstOrNull()?.let {first->Secondary(txt("explore_again"),{a.openLesson(first.id)})}
            }
            SoftCard(padding=16.dp,gap=8.dp) {
                Text(txt("path_title"),Modifier.fillMaxWidth().semantics {heading()},style=MaterialTheme.typography.headlineMedium.copy(fontWeight=FontWeight.Normal),color=AvaColors.deep,textAlign=TextAlign.Center);Text(txt("path_body"),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
                LinearProgressIndicator(progress={completed.toFloat()/s.lessons.size},modifier=Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
            }
            pathNodes(s.learner.completed,s.lessons).forEach {node->
                val l=node.lesson;val index=node.order-1
                val current=node.state==PathNodeState.Current;val done=node.state==PathNodeState.Done;val enabled=node.state!=PathNodeState.Locked
                val status=txt(if(done)"done" else if(current)"next_step" else "locked_step")
                val desc=txt("path_status","index" to fa(node.order),"status" to status)
                Surface(onClick={a.openLesson(l.id)},enabled=enabled,shape=RoundedCornerShape(32.dp),color=if(current)AvaColors.cream else AvaColors.surface.copy(alpha=if(enabled)1f else .94f),
                    border=if(current)BorderStroke(3.dp,AvaColors.green) else null,
                    modifier=Modifier.fillMaxWidth().padding(start=if(index%2==0)0.dp else 20.dp,end=if(index%2==0)20.dp else 0.dp).semantics {contentDescription=desc}) {
                    Row(Modifier.padding(if(current)24.dp else 16.dp).heightIn(min=if(current)72.dp else 64.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(if(current)72.dp else 48.dp).background(if(enabled)AvaColors.green else AvaColors.disabled,RoundedCornerShape(100.dp)),contentAlignment=Alignment.Center) {
                            if(!enabled)Icon(Icons.Outlined.Lock,null,tint=AvaColors.muted) else if(done)Icon(Icons.Outlined.Check,null,tint=androidx.compose.ui.graphics.Color.White) else Text(fa(node.order),style=MaterialTheme.typography.headlineMedium,color=androidx.compose.ui.graphics.Color.White)
                        }
                        Column(Modifier.weight(1f)) {Text(l.title,style=if(current)MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,color=AvaColors.deep);Text(status,style=MaterialTheme.typography.bodySmall,color=AvaColors.deep)}
                    }
                }
            }
            Menu(txt("story_link"),Icons.Outlined.AutoStories,{a.navigate(Route.Library)},txt("story_link_body"))
        }
        }
    }
}
