package com.ava.reading.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ava.reading.content.Art
import com.ava.reading.ui.theme.AvaColors
import com.ava.reading.ui.theme.AvaElevation
import com.ava.reading.ui.theme.AvaMotion
import com.ava.reading.ui.theme.AvaSemanticColors
import com.ava.reading.ui.theme.AvaShapes
import com.ava.reading.ui.theme.AvaSpacing
import com.ava.reading.ui.theme.AvaTheme
import com.ava.reading.ui.theme.LocalReducedMotion
import com.ava.reading.ui.theme.motionMs

/**
 * R01 base components. They complement `ui/screens/Components.kt` (which stays untouched
 * until each page migrates) and consume only the design tokens — no screen-specific
 * values are allowed here. Everything is RTL-safe (start/end-relative) and TalkBack-ready.
 */

enum class AvaButtonStyle { Primary, Secondary, Tertiary }

/** Primary CTA is 72dp (owner decision DG-3); secondary and tertiary are 64dp child targets. */
@Composable fun AvaButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, style: AvaButtonStyle = AvaButtonStyle.Primary, enabled: Boolean = true, loading: Boolean = false) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && !LocalReducedMotion.current) 0.98f else 1f, animationSpec = tween(motionMs(AvaMotion.press)), label = "avaButtonPress")
    val shape = RoundedCornerShape(if (style == AvaButtonStyle.Primary) AvaShapes.pill else AvaShapes.large)
    val base = modifier.fillMaxWidth().heightIn(min = if (style == AvaButtonStyle.Primary) 72.dp else 64.dp).graphicsLayer { scaleX = scale;scaleY = scale }
    val active = enabled && !loading
    when (style) {
        AvaButtonStyle.Primary -> Button(onClick, base, enabled = active, shape = shape, interactionSource = source,
            colors = ButtonDefaults.buttonColors(containerColor = AvaColors.green, contentColor = Color.White, disabledContainerColor = AvaColors.disabled, disabledContentColor = AvaColors.disabledText),
            contentPadding = PaddingValues(horizontal = AvaSpacing.lg, vertical = 14.dp)) { AvaBusy(loading);Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center) }
        AvaButtonStyle.Secondary -> OutlinedButton(onClick, base, enabled = active, shape = shape, interactionSource = source, border = BorderStroke(1.dp, AvaColors.green), contentPadding = PaddingValues(AvaSpacing.md)) { AvaBusy(loading);Text(text, textAlign = TextAlign.Center) }
        AvaButtonStyle.Tertiary -> TextButton(onClick, base, enabled = active, shape = shape, interactionSource = source, contentPadding = PaddingValues(AvaSpacing.sm)) { AvaBusy(loading);Text(text, textAlign = TextAlign.Center, color = AvaColors.deep) }
    }
}

/** Loading indicator follows the existing precedent: hidden under reduced motion, where waiting text carries the meaning. */
@Composable private fun RowScope.AvaBusy(loading: Boolean) {
    if (loading && !LocalReducedMotion.current) { CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = AvaColors.deep);Spacer(Modifier.width(AvaSpacing.sm)) }
}

/** Labelled icon-only button; a blank label is a programming error, not a runtime fallback. */
@Composable fun AvaIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, size: Dp = 64.dp) {
    require(contentDescription.isNotBlank()) { "AvaIconButton needs a content description" }
    IconButton(onClick, modifier.size(size), enabled = enabled) { Icon(icon, contentDescription, tint = AvaColors.green) }
}

/** Card with Default/Interactive/Selected/Disabled states. Interactive cards keep the 64dp child target. */
@Composable fun AvaCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, enabled: Boolean = true, selected: Boolean = false, shape: Shape = RoundedCornerShape(AvaShapes.xlarge), color: Color = if (selected) AvaColors.cream else AvaColors.surface, stateDescription: String? = null, content: @Composable () -> Unit) {
    val ring = if (selected) BorderStroke(3.dp, AvaSemanticColors.selectedRing) else BorderStroke(1.dp, AvaColors.line)
    val describe = if (stateDescription == null) modifier else modifier.semantics { this.stateDescription = stateDescription }
    if (onClick != null) {
        Surface(onClick, describe.defaultMinSize(minHeight = 64.dp).semantics { role = Role.Button }, enabled = enabled, shape = shape, color = color, tonalElevation = 0.dp, shadowElevation = AvaElevation.card, border = ring, content = content)
    } else {
        Surface(describe, shape = shape, color = color, tonalElevation = 0.dp, shadowElevation = AvaElevation.card, border = ring, content = content)
    }
}

/** Page header with a 64dp back target. Back navigation must carry a spoken label. */
@Composable fun AvaHeader(title: String, modifier: Modifier = Modifier, back: (() -> Unit)? = null, backContentDescription: String? = null) {
    require(back == null || backContentDescription != null) { "AvaHeader back needs a content description" }
    Row(modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically) {
        if (back != null) IconButton(back, Modifier.size(64.dp)) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, backContentDescription, tint = AvaColors.green) }
        Text(title, Modifier.weight(1f).semantics { heading() }, style = MaterialTheme.typography.titleLarge, color = AvaColors.deep)
    }
}

/** The one progress bar: 8dp pill, evergreen on line. Optional label is spoken as the value. */
@Composable fun AvaProgress(progress: Float, modifier: Modifier = Modifier, label: String? = null) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AvaSpacing.xs)) {
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(AvaShapes.pill)), color = AvaSemanticColors.progress, trackColor = AvaSemanticColors.progressTrack)
        if (label != null) Text(label, Modifier.fillMaxWidth().semantics { contentDescription = label }, style = MaterialTheme.typography.bodySmall, color = AvaColors.deep)
    }
}

enum class AvaFeedbackTone { Success, Info, Attention }

/** Feedback banner. Success stays green family; problems stay amber — AVA never uses red. Announced politely. */
@Composable fun AvaFeedback(message: String, modifier: Modifier = Modifier, tone: AvaFeedbackTone = AvaFeedbackTone.Info, leading: (@Composable () -> Unit)? = null) {
    val (bg, fg, icon) = when (tone) {
        AvaFeedbackTone.Success -> Triple(AvaSemanticColors.successContainer, AvaColors.deep, Icons.Outlined.Check)
        AvaFeedbackTone.Info -> Triple(AvaColors.amberTint, AvaColors.amber, Icons.Outlined.Info)
        AvaFeedbackTone.Attention -> Triple(AvaColors.amberTint, AvaColors.amber, Icons.Outlined.Refresh)
    }
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(AvaShapes.large), color = bg) {
        Row(Modifier.fillMaxWidth().padding(AvaSpacing.md).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AvaSpacing.sm)) {
            Icon(icon, null, tint = fg)
            Text(message, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = fg)
            leading?.invoke()
        }
    }
}

/** One unified answer tile (recognize, scene, bubble and story choices converge here in later slices). */
@Composable fun AvaAnswerOption(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, sublabel: String? = null) {
    Surface(modifier.fillMaxWidth().heightIn(min = 64.dp).selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(AvaShapes.large), color = if (selected) AvaColors.line else AvaColors.surface,
        border = BorderStroke(if (selected) 3.dp else 1.dp, if (selected) AvaSemanticColors.selectedRing else AvaColors.line)) {
        Row(Modifier.padding(horizontal = AvaSpacing.md, vertical = AvaSpacing.sm), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AvaSpacing.sm)) {
            Column(Modifier.weight(1f), horizontalAlignment = if (sublabel == null) Alignment.CenterHorizontally else Alignment.Start) {
                Text(label, style = MaterialTheme.typography.titleMedium, color = AvaColors.deep, textAlign = TextAlign.Center)
                if (sublabel != null) Text(sublabel, style = MaterialTheme.typography.bodySmall, color = AvaColors.soft, textAlign = TextAlign.Center)
            }
            if (selected) Icon(Icons.Outlined.RadioButtonChecked, null, tint = AvaColors.green, modifier = Modifier.size(18.dp))
        }
    }
}

/** Ava (or a friend) speaking in a soft bubble. The art is decorative; the message carries the semantics. */
@Composable fun AvaCharacterMessage(artId: String, message: String, modifier: Modifier = Modifier, artDescription: String? = null) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AvaSpacing.sm), verticalAlignment = Alignment.Bottom) {
        Image(painterResource(Art.ids.getValue(artId)), artDescription, Modifier.size(44.dp))
        Surface(shape = RoundedCornerShape(AvaShapes.xlarge), color = AvaColors.cream, shadowElevation = AvaElevation.card) {
            Text(message, Modifier.padding(horizontal = AvaSpacing.md, vertical = AvaSpacing.sm), style = MaterialTheme.typography.bodyMedium, color = AvaColors.deep)
        }
    }
}

/** Empty or blocked state: art, title, optional body and one safe action. */
@Composable fun AvaStateMessage(title: String, modifier: Modifier = Modifier, body: String? = null, artId: String? = null, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    require(actionLabel == null || onAction != null) { "AvaStateMessage action needs a handler" }
    Column(modifier.fillMaxWidth().padding(AvaSpacing.lg), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AvaSpacing.sm)) {
        if (artId != null) Image(painterResource(Art.ids.getValue(artId)), null, Modifier.height(96.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = AvaColors.deep, textAlign = TextAlign.Center)
        if (body != null) Text(body, style = MaterialTheme.typography.bodySmall, color = AvaColors.soft, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) AvaButton(actionLabel, onAction, style = AvaButtonStyle.Secondary)
    }
}

// --- Component gallery. Rendered by AvaComponentsTest snapshot (build/reports/native-ui) and by any
// IDE host. @Preview annotations are intentionally absent: androidx.compose.ui:ui-tooling-preview
// cannot be downloaded on this machine (mirror TLS handshake blocked, see docs/PROGRESS_FA.md);
// adding them later is a one-import change per function.

@Composable private fun GalleryScaffold(fontScale: Float = 1f, width: Dp = 390.dp, content: @Composable ColumnScope.() -> Unit) {
    val density = LocalDensity.current
    AvaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, LocalDensity provides Density(density.density, fontScale)) {
            Surface(color = AvaColors.paper) {
                Column(Modifier.width(width).padding(AvaSpacing.page), verticalArrangement = Arrangement.spacedBy(AvaSpacing.md), content = content)
            }
        }
    }
}

/** Every base component in every state, RTL, Persian — the renderable contract of this file. */
@Composable internal fun AvaComponentGallery(fontScale: Float = 1f, width: Dp = 390.dp) {
    GalleryScaffold(fontScale, width) {
        Text("دکمه‌ها", color = AvaColors.deep, style = MaterialTheme.typography.titleMedium)
        AvaButton("شروع تمرین", {})
        AvaButton("ادامه", {}, style = AvaButtonStyle.Secondary)
        AvaButton("بیشتر بدانید", {}, style = AvaButtonStyle.Tertiary)
        AvaButton("غیرفعال", {}, enabled = false)
        AvaButton("در حال بررسی…", {}, loading = true)
        Text("کارت‌ها و گزینه‌ها", color = AvaColors.deep, style = MaterialTheme.typography.titleMedium)
        AvaCard { Column(Modifier.padding(AvaSpacing.md)) { Text("کارت ساده", color = AvaColors.deep) } }
        AvaCard(onClick = {}, selected = true, stateDescription = "انتخاب‌شده") { Column(Modifier.padding(AvaSpacing.md)) { Text("کارت انتخاب‌شده", color = AvaColors.deep) } }
        AvaAnswerOption("آ", selected = false, onClick = {})
        AvaAnswerOption("ب", selected = true, onClick = {})
        AvaAnswerOption("واژهٔ چندحرفی", selected = false, onClick = {}, enabled = false, sublabel = "غیرفعال")
        Text("بازخورد و وضعیت‌ها", color = AvaColors.deep, style = MaterialTheme.typography.titleMedium)
        AvaFeedback("پیدایش کردی!", tone = AvaFeedbackTone.Success)
        AvaFeedback("یک گزینه را انتخاب کن.", tone = AvaFeedbackTone.Info)
        AvaFeedback("اشکالی ندارد؛ دوباره تلاش کن.", tone = AvaFeedbackTone.Attention)
        AvaCharacterMessage("char.ava.head_happy", "سلام! امروز یک قدم تازه برمی‌داریم.")
        AvaStateMessage("هنوز چیزی برای مرور نداری", body = "بعد از اولین درس اینجا تمرین می‌آید.", artId = "char.ava.reading", actionLabel = "شروع تمرین", onAction = {})
        Text("هدر و پیشرفت", color = AvaColors.deep, style = MaterialTheme.typography.titleMedium)
        AvaHeader("دوستی با نشانه‌ها", back = {}, backContentDescription = "بازگشت")
        AvaProgress(0.34f, label = "۲ از ۶ مرحله")
        AvaProgress(1f)
    }
}
