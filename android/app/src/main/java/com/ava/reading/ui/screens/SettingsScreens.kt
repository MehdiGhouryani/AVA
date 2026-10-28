package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

@Composable private fun SectionTitle(value: String) {
    Text(value,Modifier.fillMaxWidth().padding(top=4.dp).semantics {heading()},style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
}

@Composable private fun Preference(title: String,body: String,checked: Boolean,action: (Boolean)->Unit) {
    val state=txt(if(checked)"state_on" else "state_off")
    Surface(shape=RoundedCornerShape(24.dp),color=AvaColors.surface,border=BorderStroke(1.dp,AvaColors.line)) {
        Row(Modifier.fillMaxWidth().heightIn(min=76.dp).semantics(mergeDescendants=true) {stateDescription=state}
            .toggleable(checked,role=Role.Switch,onValueChange=action).padding(16.dp),
            horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(title,style=MaterialTheme.typography.titleSmall,color=AvaColors.deep)
                Text(body,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
            }
            Switch(checked,onCheckedChange=null,modifier=Modifier.clearAndSetSemantics {})
        }
    }
}

@Composable fun SettingsScreen(s: AvaState,a: AvaActions) {
    Page(txt("settings"),a.back) {
        SectionTitle(txt("settings_experience"))
        Preference(txt("narration"),txt("narration_body"),s.learner.narration) {a.setting("narration",it)}
        Preference(txt("sounds"),txt("sounds_body"),s.learner.sounds) {a.setting("sounds",it)}
        Preference(txt("motion"),txt("motion_body"),s.learner.reducedMotion) {a.setting("motion",it)}
        SectionTitle(txt("settings_information"))
        Menu(txt("privacy"),Icons.Outlined.Lock,{a.navigate(Route.Privacy)},txt("privacy_menu_body"))
        Menu(txt("terms"),Icons.Outlined.AutoStories,{a.navigate(Route.Terms)},txt("terms_menu_body"))
        SectionTitle(txt("settings_data"))
        Menu(txt("reset"),Icons.Outlined.RestartAlt,{a.confirm(Confirmation.ResetProgress)},txt("reset_menu_body"))
        Menu(txt("logout"),Icons.AutoMirrored.Outlined.Logout,{a.confirm(Confirmation.Logout)},txt("logout_menu_body"))
    }
}

@Composable fun InformationScreen(s: AvaState,a: AvaActions) {
    val title=when(s.route) {Route.Privacy->"privacy";Route.Terms->"terms";Route.Help->"help";else->"about"}
    Page(txt(title),a.back) {
        when(s.route) {
            Route.Help -> HelpContent()
            Route.Privacy -> LegalContent(
                listOf("privacy_local_title" to "privacy_local_body","privacy_not_title" to "privacy_not_body"),
                txt("privacy_body")
            )
            Route.Terms -> LegalContent(
                listOf("terms_scope_title" to "terms_scope_body","terms_review_title" to "terms_review_body"),
                txt("terms_body")
            )
            else -> AboutContent(s)
        }
    }
}

@Composable private fun HelpContent() {
    ArtImage("char.ava.wave_book",Modifier.fillMaxWidth().height(120.dp))
    Text(txt("help_intro"),color=AvaColors.soft)
    listOf("faq_start","faq_wrong","faq_audio","faq_save").forEach {key->FaqItem(key)}
}

@Composable private fun FaqItem(key: String) {
    var expanded by rememberSaveable(key) {mutableStateOf(false)}
    val status=txt(if(expanded)"faq_expanded" else "faq_collapsed")
    Surface(onClick={expanded=!expanded},shape=RoundedCornerShape(24.dp),color=AvaColors.cream,
        modifier=Modifier.fillMaxWidth().heightIn(min=64.dp).semantics {stateDescription=status}) {
        Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Text(txt(key),Modifier.weight(1f),style=MaterialTheme.typography.titleSmall,color=AvaColors.deep)
                Icon(if(expanded)Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,null,tint=AvaColors.green)
            }
            if(expanded)Text(txt(key+"_body"),color=AvaColors.soft)
        }
    }
}

@Composable private fun LegalContent(sections: List<Pair<String,String>>,summary: String) {
    Surface(shape=RoundedCornerShape(24.dp),color=AvaColors.amberTint) {
        Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.Info,null,tint=AvaColors.amber)
            Text(txt("legal_development"),Modifier.weight(1f),style=MaterialTheme.typography.bodySmall,color=AvaColors.amber)
        }
    }
    Text(summary,color=AvaColors.deep)
    sections.forEach {(title,body)->SoftCard(Modifier.fillMaxWidth(),padding=18.dp,gap=8.dp) {
        Text(txt(title),Modifier.fillMaxWidth().semantics {heading()},style=MaterialTheme.typography.titleMedium,color=AvaColors.deep)
        Text(txt(body),color=AvaColors.soft)
    }}
}

@Composable private fun AboutContent(s: AvaState) {
    ArtImage("brand.logo",Modifier.height(110.dp).fillMaxWidth(),txt("brand_name"))
    SoftCard(Modifier.fillMaxWidth()) {
        Text(txt("about_body"),color=AvaColors.deep)
        Text(txt("about_native"),color=AvaColors.soft)
        Text(txt("about_content","lessons" to fa(s.lessons.size),"stories" to fa(s.stories.size)),color=AvaColors.soft)
    }
    Text(txt("version"),Modifier.fillMaxWidth(),textAlign=TextAlign.Center,style=MaterialTheme.typography.bodySmall,color=AvaColors.soft)
}
