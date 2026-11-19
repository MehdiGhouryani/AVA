package com.ava.reading.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ava.reading.ui.theme.AvaColors
import com.ava.reading.ui.theme.AvaSpacing
import com.ava.reading.ui.theme.AvaTheme

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
