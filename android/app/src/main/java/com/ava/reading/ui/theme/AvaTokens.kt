package com.ava.reading.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Design-system tokens for AVA (R01). Colors stay in the generated `DesignTokens.kt`
 * (produced by scripts/sync-android-resources.py from the design JSON) — this file only
 * derives semantic meanings from them and adds the non-color scales. It is the single
 * source of truth for spacing, shape, elevation and motion; screens and shared
 * components must not hard-code these values.
 */

/** Semantic color roles derived from the generated palette. No new raw hues are introduced. */
object AvaSemanticColors {
    /** Focus/selected ring: evergreen reads ≥4.77:1 on paper, surface and cream. */
    val focus = Color(DesignTokens.evergreen)

    /** Dialog backdrop: ink dimmed, keeps AVA's soft look instead of plain black. */
    val scrim = Color(DesignTokens.ink).copy(alpha = 0.45f)

    /** Success feedback (ticks, completed states) uses the deep green. */
    val success = Color(DesignTokens.evergreen_deep)

    /** Success container reuses the existing selected tint (line) so nothing looks new. */
    val successContainer = Color(DesignTokens.line)

    val selectedRing = Color(DesignTokens.evergreen)
    val progress = Color(DesignTokens.evergreen)
    val progressTrack = Color(DesignTokens.line)
}

/** 4dp-based spacing scale. `page` is a documented exception: the 20dp gutter every current screen uses, kept stable until the page-by-page migration. */
object AvaSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
    val xxxl = 48.dp
    val page = 20.dp
    /** Minimum gap between adjacent touch targets. */
    val touchGap = 8.dp
}

/** Five corner levels replace the ten scattered radii found in discovery (8/18/20/22/24/26/28/32/100/50%). */
object AvaShapes {
    val small = 12.dp   // icon chips, story facts
    val medium = 18.dp  // word tokens, in-flow tiles
    val large = 24.dp   // option rows, secondary cards, dialogs content
    val xlarge = 32.dp  // SoftCard-level containers, path nodes
    val pill = 100.dp   // buttons, bubbles, dots, progress bars
}

/** Three-step elevation (plus flat): heavy shadows are against AVA's soft clay look. */
object AvaElevation {
    val flat = 0.dp
    val card = 1.dp
    val raised = 3.dp
    val sheet = 6.dp
}

/** Motion durations in milliseconds. Reduced motion turns every value into 0 via [motionMs]. */
object AvaMotion {
    const val press = 150        // matches the existing button press scale
    const val feedback = 200     // feedback banner in/out
    const val progress = 300     // progress bar fills
    const val celebrate = 600    // discovery pop (existing)
    const val celebrationMax = 3000  // any completion moment must stay under ~3s
}

/** Zeroes a duration when the child asked for reduced motion (in-app setting or system animator scale ≤0.5). */
@Composable fun motionMs(base: Int): Int = if (LocalReducedMotion.current) 0 else base
