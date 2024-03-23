package com.lihkin16.geminichatbot

import android.graphics.Bitmap
import com.lihkin16.geminichatbot.data.Chat

data class ChatState (

 val chatList: MutableList<Chat> =  mutableListOf() ,
    val prompt : String = "",
    val bitmap: Bitmap? = null
)