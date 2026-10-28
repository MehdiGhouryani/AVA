package com.ava.reading.ui

import com.ava.reading.content.AudioRequest
import com.ava.reading.content.ReviewMode

/** UI event contract. Screens never depend on a ViewModel or Android storage. */
data class AvaActions(
    val navigate: (Route)->Unit = {}, val back: ()->Unit = {},
    val phone: (String)->Unit = {}, val consent: (Boolean)->Unit = {}, val blurPhone: ()->Unit = {},
    val legal: (Boolean?)->Unit = {}, val send: (Boolean)->Unit = {}, val code: (String)->Unit = {}, val verify: ()->Unit = {},
    val openLesson: (String)->Unit = {}, val continueLearning: ()->Unit = {}, val startLesson: ()->Unit = {},
    val choose: (String)->Unit = {}, val chooseToken: (Int)->Unit = {}, val removeToken: ()->Unit = {}, val revealHint: ()->Unit = {},
    val check: ()->Unit = {}, val advance: ()->Unit = {}, val review: (ReviewMode)->Unit = {},
    val openStory: (String)->Unit = {}, val startStory: ()->Unit = {}, val page: (Boolean)->Unit = {},
    val storyAnswer: (String)->Unit = {}, val checkStory: ()->Unit = {}, val finishStory: ()->Unit = {},
    val name: (String)->Unit = {}, val avatar: (Int)->Unit = {}, val saveProfile: ()->Unit = {}, val setting: (String,Boolean)->Unit = {_,_->},
    val confirm: (Confirmation?)->Unit = {}, val confirmAction: ()->Unit = {}, val retryStorage: ()->Unit = {}, val notice: (String?)->Unit = {},
    val listen: (AudioRequest)->Unit = {}, val stop: ()->Unit = {}
)
fun AvaViewModel.actions() = AvaActions(
    navigate=::navigate,back={back();Unit},phone=::phone,consent=::consent,blurPhone=::validatePhone,
    legal=::legal,send=::send,code=::code,verify=::verify,openLesson=::openLesson,continueLearning=::continueLearning,startLesson=::startLesson,
    choose=::choose,chooseToken=::chooseToken,removeToken=::removeToken,revealHint=::revealHint,
    check=::check,advance=::advance,review=::review,openStory=::openStory,startStory=::startStory,page=::page,
    storyAnswer=::storyAnswer,checkStory=::checkStory,finishStory=::finishStory,name=::editName,avatar=::avatar,saveProfile=::saveProfile,
    setting=::setting,confirm=::confirm,confirmAction=::confirmAction,retryStorage=::retryStorage,notice=::notice
)
