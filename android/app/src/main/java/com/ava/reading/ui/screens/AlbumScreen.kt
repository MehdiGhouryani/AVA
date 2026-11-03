package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

/** One discovery card: the lesson title and goal, or a lock with the reason in text. Cards are derived from completed lessons. */
@Composable fun DiscoveryRow(card: Discovery) {
    Row(Modifier.fillMaxWidth().heightIn(min=64.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
        Surface(shape=RoundedCornerShape(18.dp),color=if(card.unlocked)AvaColors.line else AvaColors.cream) {
            Icon(if(card.unlocked)Icons.Outlined.Spa else Icons.Outlined.Lock,null,Modifier.padding(10.dp).size(26.dp),tint=AvaColors.green)
        }
        Column(Modifier.weight(1f)) {
            Text(card.title,style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
            Text(if(card.unlocked)card.goal else txt("album_locked"),style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
        }
    }
}

@Composable private fun DiscoveryTile(card: Discovery) {
    val status=txt(if(card.unlocked)"badge_earned" else "badge_wait")
    SoftCard(Modifier.fillMaxWidth().semantics {stateDescription=status},color=if(card.unlocked)AvaColors.cream else AvaColors.surface,padding=16.dp,gap=8.dp) {
        DiscoveryRow(card)
    }
}

@Composable fun AlbumScreen(s: AvaState,a: AvaActions) {
    val cards=discoveries(s.learner.completed,s.lessons)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact=maxWidth<360.dp || LocalDensity.current.fontScale>1.15f
        Page(txt("album_title"),a.back) {
            SoftCard(Modifier.fillMaxWidth(),padding=if(compact)16.dp else 20.dp) {
                ArtImage("char.ava.wave_book",Modifier.fillMaxWidth().height(if(compact)88.dp else 130.dp))
                Title(txt("album_body"))
                Text(txt("album_count","count" to fa(cards.count {it.unlocked}),"total" to fa(cards.size)),Modifier.fillMaxWidth(),color=AvaColors.soft,textAlign=TextAlign.Center)
            }
            cards.forEach {DiscoveryTile(it)}
        }
    }
}
