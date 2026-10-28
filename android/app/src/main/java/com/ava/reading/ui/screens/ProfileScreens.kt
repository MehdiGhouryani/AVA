package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

@Composable fun ProfileScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxWidth<360.dp || LocalDensity.current.fontScale>1.15f
        val lessons=knownLessonCount(s.learner.completed,s.lessons);val stories=knownStoryCount(s.learner.stories,s.stories)
        Page(txt("profile_title")) {
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                ArtImage(avatarArt(s.learner.avatar),Modifier.size(if(compact)88.dp else 116.dp).align(Alignment.CenterHorizontally))
                Title(s.learner.name.ifBlank {txt("friend")})
                Text(txt("profile_progress_summary","count" to fa(lessons),"total" to fa(s.lessons.size)),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
                LinearProgressIndicator(progress={lessons.toFloat()/s.lessons.size},modifier=Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
                Secondary(txt("edit_profile"),{a.navigate(Route.EditProfile)})
            }
            if(compact) Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                ProfileMetric(txt("lessons_done"),fa(lessons))
                ProfileMetric(txt("stories_done"),fa(stories))
                ProfileMetric(txt("reviews_done"),fa(s.learner.reviews.coerceAtLeast(0)))
            } else Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                ProfileMetric(txt("lessons_done"),fa(lessons),Modifier.weight(1f))
                ProfileMetric(txt("stories_done"),fa(stories),Modifier.weight(1f))
                ProfileMetric(txt("reviews_done"),fa(s.learner.reviews.coerceAtLeast(0)),Modifier.weight(1f))
            }
            Menu(txt("progress"),Icons.Outlined.StarOutline,{a.navigate(Route.Progress)},txt("profile_progress_body"))
            Menu(txt("settings"),Icons.Outlined.Settings,{a.navigate(Route.Settings)},txt("profile_settings_body"))
            Menu(txt("help"),Icons.AutoMirrored.Outlined.HelpOutline,{a.navigate(Route.Help)},txt("profile_help_body"))
            Menu(txt("about"),Icons.Outlined.Info,{a.navigate(Route.About)},txt("profile_about_body"))
        }
    }
}

@Composable private fun ProfileMetric(title: String,value: String,modifier: Modifier=Modifier) {
    Surface(modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp),color=AvaColors.surface,border=BorderStroke(1.dp,AvaColors.line)) {
        Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(2.dp)) {
            Text(value,style=MaterialTheme.typography.headlineSmall,color=AvaColors.deep)
            Text(title,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft,textAlign=TextAlign.Center)
        }
    }
}

@Composable fun EditProfileScreen(s: AvaState,a: AvaActions) {
    Page(txt("edit_profile"),a.back) {
        OutlinedTextField(s.profileName,a.name,Modifier.fillMaxWidth(),label={Text(txt("name"))},
            supportingText={Text(txt("name_counter","count" to fa(s.profileName.length)))},singleLine=true,
            shape=RoundedCornerShape(20.dp),keyboardOptions=KeyboardOptions(capitalization=KeyboardCapitalization.Words))
        Text(txt("choose_avatar"),Modifier.fillMaxWidth().semantics {heading()},style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
        val items=listOf("avatar_ava","avatar_bunny","avatar_fox","avatar_squirrel")
        Column(Modifier.fillMaxWidth().selectableGroup(),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEachIndexed {row,keys->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    keys.forEachIndexed {column,key->
                        val index=row*2+column
                        AvatarChoice(index,txt(key),s.profileAvatar==index,{a.avatar(index)},Modifier.weight(1f))
                    }
                }
            }
        }
        Primary(txt("save"),a.saveProfile)
    }
}

@Composable private fun AvatarChoice(index: Int,label: String,selected: Boolean,action: ()->Unit,modifier: Modifier=Modifier) {
    val state=txt(if(selected)"state_selected" else "state_not_selected")
    Surface(modifier.selectable(selected=selected,role=Role.RadioButton,onClick=action).semantics {stateDescription=state},
        shape=RoundedCornerShape(24.dp),color=if(selected)AvaColors.line else AvaColors.surface,
        border=BorderStroke(if(selected)2.dp else 1.dp,if(selected)AvaColors.green else AvaColors.line)) {
        Column(Modifier.padding(10.dp).heightIn(min=112.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
            ArtImage(avatarArt(index),Modifier.size(70.dp))
            Text(label,style=MaterialTheme.typography.bodySmall,color=AvaColors.deep,textAlign=TextAlign.Center)
        }
    }
}

@Composable fun ProgressScreen(s: AvaState,a: AvaActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxWidth<360.dp || LocalDensity.current.fontScale>1.15f
        val lessons=knownLessonCount(s.learner.completed,s.lessons);val stories=knownStoryCount(s.learner.stories,s.stories);val reviews=s.learner.reviews.coerceAtLeast(0)
        val allLessons=lessons==s.lessons.size
        val badges=listOf(
            Triple("badge_first",lessons>0,Icons.Outlined.Spa),
            Triple("badge_all",allLessons,Icons.Outlined.EmojiEvents),
            Triple("badge_story",stories>0,Icons.Outlined.AutoStories),
            Triple("badge_review",reviews>0,Icons.Outlined.Refresh)
        )
        val earned=badges.count {it.second}
        Page(txt("progress"),a.back) {
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                ArtImage("char.ava.wave_book",Modifier.height(if(compact)88.dp else 130.dp).fillMaxWidth())
                Title(txt("progress_path_title"))
                Text(txt("completed_count","count" to fa(lessons),"total" to fa(s.lessons.size)),Modifier.fillMaxWidth(),color=AvaColors.soft,textAlign=TextAlign.Center)
                LinearProgressIndicator(progress={lessons.toFloat()/s.lessons.size},modifier=Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(8.dp)),color=AvaColors.green,trackColor=AvaColors.line)
                Text(txt(if(allLessons)"progress_all_done" else "progress_next_step"),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodySmall,color=AvaColors.deep,textAlign=TextAlign.Center)
                if(allLessons)Primary(txt("library"),{a.navigate(Route.Library)}) else Primary(txt("continue_path"),a.continueLearning)
            }
            if(compact) Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                ProgressMetric(txt("lessons_done"),lessons,s.lessons.size,Icons.Outlined.Flag)
                ProgressMetric(txt("stories_done"),stories,s.stories.size,Icons.Outlined.AutoStories)
                ProgressMetric(txt("reviews_done"),reviews,null,Icons.Outlined.Refresh)
            } else Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                ProgressMetric(txt("lessons_done"),lessons,s.lessons.size,Icons.Outlined.Flag,Modifier.weight(1f))
                ProgressMetric(txt("stories_done"),stories,s.stories.size,Icons.Outlined.AutoStories,Modifier.weight(1f))
                ProgressMetric(txt("reviews_done"),reviews,null,Icons.Outlined.Refresh,Modifier.weight(1f))
            }
            Text(txt("badges"),Modifier.fillMaxWidth().semantics {heading()},style=MaterialTheme.typography.titleLarge,color=AvaColors.deep)
            Text(txt("badges_summary","count" to fa(earned)),color=AvaColors.soft)
            badges.forEach {(key,earnedBadge,icon)->BadgeCard(key,earnedBadge,icon)}
        }
    }
}

@Composable private fun ProgressMetric(title: String,count: Int,total: Int?,icon: androidx.compose.ui.graphics.vector.ImageVector,modifier: Modifier=Modifier) {
    Surface(modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp),color=AvaColors.surface,border=BorderStroke(1.dp,AvaColors.line)) {
        Row(Modifier.padding(12.dp).heightIn(min=64.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Icon(icon,null,Modifier.size(24.dp),tint=AvaColors.green)
            Column(Modifier.weight(1f)) {
                Text(if(total==null)fa(count) else txt("metric_fraction","count" to fa(count),"total" to fa(total)),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
                Text(title,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
            }
        }
    }
}

@Composable private fun BadgeCard(key: String,earned: Boolean,icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val status=txt(if(earned)"badge_earned" else "badge_wait")
    SoftCard(Modifier.fillMaxWidth().semantics {stateDescription=status},color=if(earned)AvaColors.cream else AvaColors.surface,padding=16.dp,gap=8.dp) {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
            Surface(shape=RoundedCornerShape(18.dp),color=if(earned)AvaColors.line else AvaColors.cream) {
                Icon(if(earned)icon else Icons.Outlined.Lock,null,Modifier.padding(10.dp).size(26.dp),tint=AvaColors.green)
            }
            Column(Modifier.weight(1f)) {
                Text(txt(key),style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
                Text(status,style=MaterialTheme.typography.bodySmall,color=if(earned)AvaColors.deep else AvaColors.soft)
            }
        }
        Text(txt(key+"_body"),color=AvaColors.soft)
    }
}
