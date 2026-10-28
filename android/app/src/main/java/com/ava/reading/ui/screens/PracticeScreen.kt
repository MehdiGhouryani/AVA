package com.ava.reading.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ava.reading.content.*
import com.ava.reading.ui.AvaActions
import com.ava.reading.ui.AvaState
import com.ava.reading.ui.theme.AvaColors

@OptIn(ExperimentalLayoutApi::class)
@Composable fun PracticeScreen(s: AvaState,a: AvaActions) {
    val mistakes=orderedTaskKeys(s.learner.mistakes,s.lessons)
    val learned=learnedTaskKeys(s.learner.completed,s.lessons)
    val mix=plannedReviewKeys(ReviewMode.Mix,s.learner.completed,s.learner.mistakes,s.learner.reviews,lessons=s.lessons)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxHeight<640.dp || LocalDensity.current.fontScale>1.15f
        Page(txt("practice_title")) {
            Text(txt("practice_body"),color=AvaColors.soft)
            ArtImage("char.ava.reading",Modifier.fillMaxWidth().height(if(compact)104.dp else 168.dp))
            if(mix.isEmpty()) {
                SoftCard(Modifier.fillMaxWidth()) {Text(txt("empty_practice"));Primary(txt("start"),a.continueLearning)}
            } else {
                SoftCard(Modifier.fillMaxWidth(),color=AvaColors.line,padding=if(compact)16.dp else 20.dp) {
                    Title(txt("quick_review_title"))
                    Text(txt("quick_review_body"),Modifier.fillMaxWidth(),color=AvaColors.soft)
                    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        ActivityFamily.entries.forEach {family->
                            val count=mix.count {key->taskForKey(key,s.lessons)?.let {ActivityRegistry[it.kind].family}==family}
                            if(count>0)ReviewPill(fa(count),txt(when(family) {ActivityFamily.Visual->"activity_recognize";ActivityFamily.Listening->"activity_listen";ActivityFamily.WordConstruction->"activity_build"}))
                        }
                    }
                    Text(txt("quick_review_count","count" to fa(mix.size)),color=AvaColors.deep)
                    Primary(txt("quick_review_cta"),{a.review(ReviewMode.Mix)})
                }
                if(mistakes.isNotEmpty())SoftCard(Modifier.fillMaxWidth()) {
                    Title(txt("mistakes_title"))
                    Text(txt("mistakes_body"),color=AvaColors.soft)
                    Text(txt("review_count","count" to fa(mistakes.take(5).size)),color=AvaColors.deep)
                    Secondary(txt("review_mistakes_cta"),{a.review(ReviewMode.Mistakes)})
                }
                if(learned.isNotEmpty())SoftCard(Modifier.fillMaxWidth()) {
                    Title(txt("learned_title"))
                    Text(txt("learned_body"),color=AvaColors.soft)
                    Text(txt("learned_session_count","session" to fa(learned.take(5).size),"total" to fa(learned.size)),color=AvaColors.deep)
                    Secondary(txt("review_learned_cta"),{a.review(ReviewMode.Learned)})
                }
            }
        }
    }
}

@Composable private fun ReviewPill(count: String,label: String) {
    Surface(shape=RoundedCornerShape(100.dp),color=AvaColors.surface) {
        Text("$count $label",Modifier.padding(horizontal=12.dp,vertical=8.dp),style=MaterialTheme.typography.labelMedium,color=AvaColors.deep)
    }
}
