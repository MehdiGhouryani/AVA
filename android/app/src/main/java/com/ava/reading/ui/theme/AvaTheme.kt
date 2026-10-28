package com.ava.reading.ui.theme

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.sp
import com.ava.reading.R

object AvaColors {
    val paper=Color(DesignTokens.paper);val cream=Color(DesignTokens.cream);val surface=Color(DesignTokens.surface)
    val green=Color(DesignTokens.evergreen);val deep=Color(DesignTokens.evergreen_deep);val ink=Color(DesignTokens.ink)
    val soft=Color(DesignTokens.text_soft);val muted=Color(DesignTokens.muted);val line=Color(DesignTokens.line)
    val amber=Color(DesignTokens.amber_text);val amberTint=Color(DesignTokens.amber_tint)
    val disabled=Color(DesignTokens.disabled_bg);val disabledText=Color(DesignTokens.disabled_text)
}
val LocalReducedMotion=staticCompositionLocalOf { false }
private val font=FontFamily(Font(R.font.vazirmatn))
private fun type(size:Int,weight:FontWeight=FontWeight.Normal)=TextStyle(fontFamily=font,fontSize=size.sp,lineHeight=(size*1.7).sp,fontWeight=weight)
private val scheme=lightColorScheme(
    primary=AvaColors.green,onPrimary=Color.White,primaryContainer=AvaColors.line,onPrimaryContainer=AvaColors.deep,
    secondary=AvaColors.deep,onSecondary=Color.White,secondaryContainer=AvaColors.line,onSecondaryContainer=AvaColors.deep,
    tertiary=AvaColors.green,tertiaryContainer=AvaColors.cream,onTertiaryContainer=AvaColors.deep,
    background=AvaColors.paper,onBackground=AvaColors.ink,surface=AvaColors.surface,onSurface=AvaColors.ink,
    surfaceVariant=AvaColors.cream,onSurfaceVariant=AvaColors.soft,outline=AvaColors.line,
    outlineVariant=AvaColors.line,surfaceTint=AvaColors.green,
    error=AvaColors.amber,onError=Color.White,errorContainer=AvaColors.amberTint,onErrorContainer=AvaColors.amber,
    surfaceContainer=AvaColors.cream,surfaceContainerHigh=AvaColors.cream,surfaceContainerHighest=AvaColors.paper
)
@Composable fun AvaTheme(content: @Composable ()->Unit) {
    MaterialTheme(colorScheme=scheme,typography=Typography(
        headlineLarge=type(32,FontWeight.Bold),headlineMedium=type(28,FontWeight.Bold),headlineSmall=type(24,FontWeight.Bold),
        titleLarge=type(22,FontWeight.SemiBold),titleMedium=type(18,FontWeight.Medium),titleSmall=type(16,FontWeight.Medium),
        bodyLarge=type(17),bodyMedium=type(16),bodySmall=type(14),
        labelLarge=type(18,FontWeight.SemiBold),labelMedium=type(14,FontWeight.Medium),labelSmall=type(12)
    ),content=content)
}
