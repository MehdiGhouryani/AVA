package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.theme.*

@Composable fun txt(key: String, vararg variables: Pair<String,Any>): String {
    val full=if(key in TextIds.ids)key else "app_$key"
    var value=stringResource(TextIds.ids.getValue(full))
    variables.forEach { (k,v)->value=value.replace("{$k}",v.toString()) }
    return value
}
@Composable fun ArtImage(id: String, modifier: Modifier=Modifier, label: String?=null, scale: ContentScale=ContentScale.Fit, alignment: Alignment=Alignment.Center) {
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
@Composable fun SoftCard(modifier: Modifier=Modifier, color: Color=AvaColors.cream, padding: Dp=20.dp, gap: Dp=12.dp, content: @Composable ColumnScope.()->Unit) {
    Surface(modifier,shape=RoundedCornerShape(32.dp),color=color,tonalElevation=0.dp,shadowElevation=1.dp) {
        Column(Modifier.padding(padding),verticalArrangement=Arrangement.spacedBy(gap),content=content)
    }
}
@Composable fun Primary(value: String, action: ()->Unit, modifier: Modifier=Modifier, enabled: Boolean=true, busy: Boolean=false) {
    val source=remember {MutableInteractionSource()};val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if(pressed && !LocalReducedMotion.current).98f else 1f,animationSpec=tween(if(LocalReducedMotion.current)0 else 150),label="press")
    Button(action,modifier.fillMaxWidth().heightIn(min=64.dp).graphicsLayer {scaleX=scale;scaleY=scale},enabled=enabled&&!busy,
        shape=RoundedCornerShape(100.dp),interactionSource=source,
        colors=ButtonDefaults.buttonColors(containerColor=AvaColors.green,contentColor=Color.White,disabledContainerColor=AvaColors.disabled,disabledContentColor=AvaColors.disabledText),
        contentPadding=PaddingValues(horizontal=20.dp,vertical=14.dp)) {
        if(busy && !LocalReducedMotion.current) {CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=2.dp,color=AvaColors.deep);Spacer(Modifier.width(8.dp))}
        Text(value,style=MaterialTheme.typography.labelLarge,textAlign=TextAlign.Center)
    }
}
@Composable fun Secondary(value: String, action: ()->Unit, modifier: Modifier=Modifier, enabled: Boolean=true) {
    OutlinedButton(action,modifier.fillMaxWidth().heightIn(min=48.dp),enabled=enabled,shape=RoundedCornerShape(24.dp),border=BorderStroke(1.dp,AvaColors.green),contentPadding=PaddingValues(12.dp)) {
        Text(value,textAlign=TextAlign.Center)
    }
}
@Composable fun Header(value: String, back: (() -> Unit)?=null) {
    Row(Modifier.fillMaxWidth().heightIn(min=56.dp),verticalAlignment=Alignment.CenterVertically) {
        if(back!=null)IconButton(back) {Icon(Icons.AutoMirrored.Outlined.ArrowBack,txt("back"),tint=AvaColors.green)}
        Text(value,Modifier.weight(1f).semantics {heading()},style=MaterialTheme.typography.titleLarge,color=AvaColors.deep)
    }
}
@Composable fun Page(title: String, back: (() -> Unit)?=null, content: @Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Header(title,back);content()
    }
}
@Composable fun Menu(value: String, icon: ImageVector, action: ()->Unit, body: String?=null) {
    Surface(action,shape=RoundedCornerShape(22.dp),color=AvaColors.surface,modifier=Modifier.fillMaxWidth(),tonalElevation=0.dp) {
        Row(Modifier.padding(16.dp).heightIn(min=32.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Icon(icon,null,tint=AvaColors.green,modifier=Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {Text(value,color=AvaColors.deep);if(body!=null)Text(body,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)}
        }
    }
}
@Composable fun Feedback(message: String) {
    Text(message,Modifier.fillMaxWidth().background(AvaColors.amberTint,RoundedCornerShape(20.dp)).padding(16.dp).semantics {liveRegion=LiveRegionMode.Polite},color=AvaColors.amber,textAlign=TextAlign.Center)
}
fun avatarArt(index: Int)=listOf("char.ava.head_happy","char.friend.bunny","char.friend.fox","char.friend.squirrel")[index.coerceIn(0,3)]
