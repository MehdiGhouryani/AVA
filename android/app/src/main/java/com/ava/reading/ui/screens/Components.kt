package com.ava.reading.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ava.reading.content.*
import com.ava.reading.ui.components.*
import com.ava.reading.ui.theme.*

@Composable fun txt(key: String, vararg variables: Pair<String,Any>): String {
    val full=if(key in TextIds.ids)key else "app_$key"
    var value=stringResource(TextIds.ids.getValue(full))
    variables.forEach { (k,v)->value=value.replace("{$k}",v.toString()) }
    return value
}

@Composable fun ArtImage(
    id: String,
    modifier: Modifier=Modifier,
    label: String?=null,
    scale: ContentScale=ContentScale.Fit,
    alignment: Alignment=Alignment.Center
) {
    Image(painterResource(Art.ids.getValue(id)),label,modifier,contentScale=scale,alignment=alignment)
}

@Composable fun Scene(id: String, content: @Composable BoxScope.()->Unit) {
    Box(Modifier.fillMaxSize().background(AvaColors.paper)) {
        ArtImage(id,Modifier.matchParentSize(),scale=ContentScale.Crop,alignment=if(id=="bg.onboarding1")Alignment.Center else Alignment.BottomCenter)
        content()
    }
}

@Composable fun Title(value: String, modifier: Modifier=Modifier) {
    Text(value,modifier.fillMaxWidth().semantics {heading()},style=MaterialTheme.typography.headlineSmall,color=AvaColors.deep,textAlign=TextAlign.Center)
}

/** Backward-compatible page card; tokens and surface treatment come from AvaCard. */
@Composable fun SoftCard(
    modifier: Modifier=Modifier,
    color: Color=AvaColors.cream,
    padding: Dp=20.dp,
    gap: Dp=12.dp,
    content: @Composable ColumnScope.()->Unit
) {
    AvaCard(
        modifier=modifier,
        color=color,
        shape=RoundedCornerShape(AvaShapes.xlarge),
        bordered=false
    ) {
        Column(Modifier.padding(padding),verticalArrangement=Arrangement.spacedBy(gap)) {
            content()
        }
    }
}

/** Legacy CTA API retained while all screens move to the shared R01 implementation. */
@Composable fun Primary(value: String, action: ()->Unit, modifier: Modifier=Modifier, enabled: Boolean=true, busy: Boolean=false) {
    AvaButton(value,action,modifier,AvaButtonStyle.Primary,enabled,busy)
}

@Composable fun Secondary(value: String, action: ()->Unit, modifier: Modifier=Modifier, enabled: Boolean=true) {
    AvaButton(value,action,modifier,AvaButtonStyle.Secondary,enabled)
}

@Composable fun Header(value: String, back: (() -> Unit)?=null) {
    AvaHeader(value,back=back,backContentDescription=if(back!=null)txt("back") else null)
}

@Composable fun Page(title: String, back: (() -> Unit)?=null, content: @Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Header(title,back)
        content()
    }
}

@Composable fun Menu(value: String, icon: ImageVector, action: ()->Unit, body: String?=null) {
    AvaMenuRow(title=value,icon=icon,onClick=action,subtitle=body)
}

@Composable fun Feedback(message: String) {
    AvaFeedback(message,tone=AvaFeedbackTone.Attention)
}

fun avatarArt(index: Int)=listOf("char.ava.head_happy","char.friend.bunny","char.friend.fox","char.friend.squirrel")[index.coerceIn(0,3)]
