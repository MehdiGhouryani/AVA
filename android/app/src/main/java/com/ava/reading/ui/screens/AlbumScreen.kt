package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.semantics.*
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
