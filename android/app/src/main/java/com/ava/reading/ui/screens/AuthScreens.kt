package com.ava.reading.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.*
import com.ava.reading.content.*
import com.ava.reading.ui.*
import com.ava.reading.ui.theme.*

@Composable fun SplashScreen(s: AvaState,a: AvaActions) {
    Scene("bg.splash") {
        Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Spacer(Modifier.fillMaxHeight(.1f));ArtImage("brand.logo",Modifier.height(120.dp),txt("brand_name"))
            Spacer(Modifier.weight(1f))
            Text(txt("splash_loading"),color=AvaColors.muted,textAlign=TextAlign.Center)
            Spacer(Modifier.height(72.dp))
        }
    }
}
@Composable fun OnboardingScreen(s: AvaState,a: AvaActions) {
    val first=s.route==Route.Onboarding1
    Scene(if(first)"bg.onboarding1" else "bg.garden") {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact=maxHeight<650.dp
            val large=LocalDensity.current.fontScale>1.15f
            val artHeight=(maxHeight.value*.28f).coerceIn(90f,250f).dp
            val m=if(large)Modifier.verticalScroll(rememberScrollState()) else Modifier
            Column(Modifier.fillMaxSize().then(m).padding(horizontal=24.dp,vertical=12.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(if(compact)8.dp else 12.dp)) {
                ArtImage("brand.logo",Modifier.height(if(compact)48.dp else 72.dp),txt("brand_name"))
                SoftCard(Modifier.fillMaxWidth()) {
                    Title(txt(if(first)"onboarding_step1_title" else "onboarding_step2_title"))
                    Text(txt(if(first)"onboarding_step1_body" else "onboarding_step2_body"),textAlign=TextAlign.Center,color=AvaColors.soft)
                }
                Box(if(large)Modifier.height(artHeight).fillMaxWidth() else Modifier.weight(1f).fillMaxWidth(),contentAlignment=Alignment.BottomCenter) {
                    if(first) {
                        ArtImage("char.ava.reading",Modifier.fillMaxHeight().fillMaxWidth(.76f).align(AbsoluteAlignment.BottomLeft))
                        ArtImage("decor.tree",Modifier.fillMaxHeight(.48f).fillMaxWidth(.32f).align(AbsoluteAlignment.TopRight))
                    } else {
                        ArtImage("char.friend.squirrel",Modifier.fillMaxHeight(.85f).fillMaxWidth(.38f).align(Alignment.TopCenter))
                        ArtImage("char.friend.bunny",Modifier.fillMaxHeight(.8f).fillMaxWidth(.33f).align(AbsoluteAlignment.BottomLeft))
                        ArtImage("char.friend.fox",Modifier.fillMaxHeight(.84f).fillMaxWidth(.35f).align(AbsoluteAlignment.BottomRight))
                        ArtImage("char.ava.wave_book",Modifier.fillMaxHeight().fillMaxWidth(.5f).align(Alignment.BottomCenter))
                    }
                }
                Primary(txt("onboarding_next"),{a.navigate(if(first)Route.Onboarding2 else Route.Phone)})
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    repeat(2) {i->Box(Modifier.size(width=if(i==if(first)0 else 1)24.dp else 10.dp,height=10.dp).background(if(i==if(first)0 else 1)AvaColors.green else AvaColors.line,RoundedCornerShape(100.dp)))}
                }
                TextButton({a.navigate(Route.Phone)},Modifier.align(Alignment.End).heightIn(min=48.dp).background(AvaColors.cream.copy(alpha=.94f),RoundedCornerShape(100.dp))) {Text(txt("onboarding_skip"),color=AvaColors.muted)}
            }
        }
    }
}
@Composable fun PhoneScreen(s: AvaState,a: AvaActions) {
    val focus=remember {FocusRequester()};var hadFocus by remember {mutableStateOf(false)}
    val invalid=s.message=="phone_error_invalid"
    val errorText=if(invalid)txt("phone_error_invalid") else null
    Scene("bg.login") {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)) {
            ArtImage("brand.logo",Modifier.height(104.dp),txt("brand_name"))
            Text(txt("brand_tagline"),color=AvaColors.deep,textAlign=TextAlign.Center)
            Title(txt("phone_title"))
            val label=txt("phone_label")
            OutlinedTextField(s.phone,a.phone,modifier=Modifier.fillMaxWidth().focusRequester(focus).onFocusChanged {if(hadFocus&&!it.isFocused)a.blurPhone();hadFocus=it.isFocused}.semantics {contentType=ContentType.PhoneNumberNational;if(errorText!=null)error(errorText)},
                enabled=!s.busy,label={Text(label)},placeholder={Text(txt("phone_placeholder"))},singleLine=true,isError=invalid,shape=RoundedCornerShape(20.dp),
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Phone,imeAction=ImeAction.Done),keyboardActions=KeyboardActions(onDone={a.send(false)}),
                visualTransformation=PersianDigits,textStyle=MaterialTheme.typography.titleMedium.copy(textDirection=TextDirection.Ltr,textAlign=TextAlign.Right))
            s.message?.let {Feedback(txt(it))}
            val consentState=txt(if(s.consent)"consent_accepted" else "consent_not_accepted")
            Row(Modifier.fillMaxWidth().heightIn(min=56.dp).semantics(mergeDescendants=true) {stateDescription=consentState}
                .toggleable(value=s.consent,enabled=!s.busy,role=Role.Checkbox,onValueChange=a.consent),verticalAlignment=Alignment.CenterVertically) {
                Checkbox(s.consent,onCheckedChange=null,modifier=Modifier.clearAndSetSemantics {});Spacer(Modifier.width(8.dp));Text(txt("phone_consent"),color=AvaColors.deep)
            }
            HorizontalDivider(color=AvaColors.line)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically) {
                TextButton({a.legal(true)},Modifier.weight(1f)) {Text(txt("phone_link_privacy"),textAlign=TextAlign.Center)}
                TextButton({a.legal(false)},Modifier.weight(1f)) {Text(txt("phone_link_terms"),textAlign=TextAlign.Center)}
            }
            Primary(txt(if(s.busy)"phone_sending" else "phone_cta"),{a.send(false)},enabled=validPhone(s.phone)&&s.consent,busy=s.busy)
            Text(txt("mock_hint"),style=MaterialTheme.typography.bodySmall,color=AvaColors.muted,textAlign=TextAlign.Center)
        }
    }
}
private val PersianDigits=VisualTransformation { value -> TransformedText(AnnotatedString(fa(value.text)),OffsetMapping.Identity) }
@Composable fun OtpScreen(s: AvaState,a: AvaActions) {
    Scene("bg.garden") {
        Column(Modifier.fillMaxSize()) {
            BoxWithConstraints(Modifier.fillMaxWidth().weight(.28f)) {
                ArtImage("brand.logo",Modifier.height(maxHeight.coerceAtMost(88.dp)).align(Alignment.Center),txt("brand_name"))
            }
            Surface(Modifier.fillMaxWidth().weight(.72f),shape=RoundedCornerShape(topStart=40.dp,topEnd=40.dp),color=AvaColors.cream,shadowElevation=6.dp) {
                Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Title(txt("otp_title"))
                    Text(txt("otp_subtitle","phone" to fa(s.phone)),textAlign=TextAlign.Center,color=AvaColors.soft)
                    Secondary(txt("otp_edit_phone"),{a.navigate(Route.Phone)},enabled=!s.busy)
                    OtpField(s,a)
                    s.message?.let {Feedback(txt(it))}
                    val seconds=if(s.locked)s.lockSeconds else s.resendSeconds
                    val time=fa("${seconds/60}:${(seconds%60).toString().padStart(2,'0')}")
                    if(s.locked || seconds>0)Text(txt(if(s.locked)"lock_timer" else "otp_resend_in","time" to time),color=AvaColors.deep)
                    else TextButton({a.send(true)},enabled=!s.busy) {Text(txt("otp_resend"))}
                    Primary(txt(if(s.busy)"otp_verifying" else "otp_cta"),a.verify,enabled=s.code.length==6&&!s.locked,busy=s.busy)
                    Text(txt("mock_hint"),style=MaterialTheme.typography.bodySmall,color=AvaColors.muted,textAlign=TextAlign.Center)
                }
            }
        }
    }
}
@Composable private fun OtpField(s: AvaState,a: AvaActions) {
    val focus=remember {FocusRequester()};var focused by remember {mutableStateOf(false)}
    val label=txt("otp_label")
    val hasError=s.message in listOf("otp_wrong","otp_expired","otp_locked")
    val errorText=if(hasError)s.message?.let {txt(it)} else null
    // One native editing node supports paste/selection and IME. Boxes are decorative projections.
    BasicTextField(s.code,a.code,Modifier.fillMaxWidth().heightIn(min=64.dp).focusRequester(focus).onFocusChanged {focused=it.isFocused}.semantics {contentDescription=label;contentType=ContentType.SmsOtpCode;if(errorText!=null)error(errorText)},
        enabled=!s.busy&&!s.locked,singleLine=true,textStyle=TextStyle(color=Color.Transparent,fontSize=1.sp),cursorBrush=SolidColor(Color.Transparent),
        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword,imeAction=ImeAction.Done),keyboardActions=KeyboardActions(onDone={a.verify()}),
        decorationBox={inner ->
            Box {
                Box(Modifier.size(1.dp)) {inner()}
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    repeat(6) {i->
                        val active=focused && i==s.code.length.coerceAtMost(5)
                        Box(Modifier.weight(1f).height(64.dp).background(if(hasError)AvaColors.amberTint else AvaColors.surface,RoundedCornerShape(18.dp))
                            .border(if(active)2.dp else 1.dp,if(hasError)AvaColors.amber else if(active)AvaColors.green else AvaColors.line,RoundedCornerShape(18.dp)),contentAlignment=Alignment.Center) {
                            Text(s.code.getOrNull(i)?.let {fa(it)}.orEmpty(),style=MaterialTheme.typography.headlineMedium,color=AvaColors.deep,modifier=Modifier.clearAndSetSemantics {})
                        }
                    }
                }
            }
        })
    // Request only after the field's focus target is composed and attached.
    LaunchedEffect(Unit) {focus.requestFocus()}
}
