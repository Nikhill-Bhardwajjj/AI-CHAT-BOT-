package com.lihkin16.geminichatbot

import android.graphics.Bitmap

sealed class ChataUIEvent {
    data class UpdatePrompt(val  newPrompt:String): ChataUIEvent()
    data class SendPrompt(
        val  prompt:String ,
        val bitmap : Bitmap?): ChataUIEvent()
}