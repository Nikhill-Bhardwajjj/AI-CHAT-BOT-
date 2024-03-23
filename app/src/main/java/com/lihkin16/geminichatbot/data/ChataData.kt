package com.lihkin16.geminichatbot.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChataData {

    val api_key = "AIzaSyDnm8Lo5B1rLie2BKXeyVPQkrjp3NC5Efc"



    suspend fun getResponse(prompt: String): Chat
    {

        val generativeModel = GenerativeModel(

            modelName = "gemini-pro",  api_key
        )
        /*val generativeModel = GenerativeModel(
            modelName = "gemini-pro" , apikey = api_key
        )*/
        
        try{
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            return Chat(
                prompt = response.text?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e: Exception)
        {
            return Chat(
                prompt = e.message?:"error",
                bitmap = null,
                isFromUser = false
            )

        }


    }


    suspend fun getResponseWithImage(prompt: String , bitmap:Bitmap): Chat
    {

        val generativeModel = GenerativeModel(

            modelName = "gemini-pro-vision",  api_key
        )
        /*val generativeModel = GenerativeModel(
            modelName = "gemini-pro" , apikey = api_key
        )*/

        try{



            val inputContent = content {

                image(bitmap)
                text(prompt)
            }
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return Chat(
                prompt = response.text?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e: Exception)
        {
            return Chat(
                prompt = e.message?:"error",
                bitmap = null,
                isFromUser = false
            )

        }


    }


}