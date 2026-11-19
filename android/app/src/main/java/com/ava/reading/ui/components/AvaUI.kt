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
import androidx.compose.material.icons.outlined.ChevronLeft
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ava.reading.content.Art
import com.ava.reading.ui.theme.AvaColors
import com.ava.reading.ui.theme.AvaElevation
import com.ava.reading.ui.theme.AvaMotion
import com.ava.reading.ui.theme.AvaSemanticColors
import com.ava.reading.ui.theme.AvaShapes
import com.ava.reading.ui.theme.AvaSpacing
import com.ava.reading.ui.theme.LocalReducedMotion
import com.ava.reading.ui.theme.motionMs

/**
 * R01 production components. Legacy screen primitives delegate here during gradual
 * migration; screen-specific layout remains in the screen package. Values come from tokens.
 * Everything is RTL-safe and exposes the semantics required by TalkBack.
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
@Composable fun AvaCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, enabled: Boolean = true, selected: Boolean = false, shape: Shape = RoundedCornerShape(AvaShapes.xlarge), color: Color = if (selected) AvaColors.cream else AvaColors.surface, stateDescription: String? = null, bordered: Boolean = true, content: @Composable () -> Unit) {
    val ring = if (selected) BorderStroke(3.dp, AvaSemanticColors.selectedRing) else BorderStroke(1.dp, AvaColors.line)
    val describe = if (stateDescription == null) modifier else modifier.semantics { this.stateDescription = stateDescription }
    if (onClick != null) {
        Surface(onClick, describe.defaultMinSize(minHeight = 64.dp).semantics { role = Role.Button }, enabled = enabled, shape = shape, color = color, tonalElevation = 0.dp, shadowElevation = AvaElevation.card, border = if (selected || bordered) ring else null, content = content)
    } else {
        Surface(describe, shape = shape, color = color, tonalElevation = 0.dp, shadowElevation = AvaElevation.card, border = if (selected || bordered) ring else null, content = content)
    }
}


/**
 * Navigation row used by profile/settings and secondary information lists.
 * The whole 72dp+ row is one button target; icon, title and optional description
 * are merged into one RTL reading order.
 */
@Composable fun AvaMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    stateDescription: String? = null
) {
    val rowColor = if (enabled) AvaColors.surface else AvaColors.disabled
    val titleColor = if (enabled) AvaColors.deep else AvaColors.disabledText
    val supportingColor = if (enabled) AvaColors.soft else AvaColors.disabledText
    val iconColor = if (enabled) AvaColors.green else AvaColors.disabledText
    val accessible = modifier.semantics(mergeDescendants = true) {
        role = Role.Button
        if (stateDescription != null) this.stateDescription = stateDescription
    }
    Surface(
        onClick = onClick,
        modifier = accessible.fillMaxWidth().heightIn(min = 72.dp),
        enabled = enabled,
        shape = RoundedCornerShape(AvaShapes.large),
        color = rowColor,
        tonalElevation = 0.dp,
        shadowElevation = AvaElevation.flat,
        border = BorderStroke(1.dp, AvaColors.line)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = AvaSpacing.md, vertical = AvaSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AvaSpacing.sm)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(AvaShapes.medium),
                color = AvaColors.cream
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
            }
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AvaSpacing.xxs)
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = titleColor)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = supportingColor)
                }
            }
            Icon(Icons.Outlined.ChevronLeft, null, tint = supportingColor, modifier = Modifier.size(22.dp))
        }
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
