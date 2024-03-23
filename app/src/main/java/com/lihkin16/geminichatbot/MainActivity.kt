package com.lihkin16.geminichatbot

import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.darkColors

import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lihkin16.geminichatbot.ui.theme.GeminiChatBotTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult<PickVisualMediaRequest , Uri>(
            ActivityResultContracts.PickVisualMedia()

        ){uri ->

           uri.let {
            uriState.update { uri.toString() }
           }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiChatBotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            Box(modifier = Modifier
                                .fillMaxWidth()

                                .background(MaterialTheme.colorScheme.primary)
                                .height(55.dp)
                                .padding(horizontal = 16.dp)

                            ) {

                                Text(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    text = "Your Personal Assistant" ,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimary


                                )

                                Text(
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                        .padding(bottom = 2.dp, end = 10.dp),
                                    text = "By Nikhil" ,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimary


                                )
                            }

                        }
                    ) {
                    
                        ChatScreen(paddingValues = it)
                    }
                }
            }
        }
    }


@Composable
fun ChatScreen(paddingValues: PaddingValues) {

    val chatViewModel =viewModel<ChatViewModel>()
    val chatState = chatViewModel.chatState.collectAsState().value


    val bitmap = getBitmapFromUri()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.Bottom

    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = true

        ) {
            itemsIndexed(chatState.chatList){
                index, chat ->


                if(chat.isFromUser){
                    UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap) // from user
                }else{
                    ModelChatItem(response = chat.prompt) // from model

                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            Column{

                bitmap?.let {
                    Image(

                        modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentDescription = "Picked image",
                        contentScale = ContentScale.Crop,
                        bitmap = it.asImageBitmap()
                    )
                }

                val customIcon2 = ImageVector.vectorResource(R.drawable.send_email_svgrepo_com)

              Icon(modifier = Modifier
                  .size(40.dp)
                  /*.clip(RoundedCornerShape(6.dp))*/
                  /*.background(MaterialTheme.colorScheme.primary)*/
                  .clickable {
                      imagePicker.launch(
                          PickVisualMediaRequest
                              .Builder()
                              .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                              .build()
                      )
                  },
                  imageVector = Icons.Rounded.AddPhotoAlternate,
                  contentDescription = "Add photo",
                  tint = MaterialTheme.colorScheme.primary


                      )
            }



            Spacer(modifier = Modifier.width(8.dp))



            TextField(

                modifier = Modifier
                    .weight(1f),
                value = chatState.prompt,
                onValueChange = {
                    chatViewModel.onEvent(ChataUIEvent.UpdatePrompt(it))
                },
                placeholder = {
                Text(text = "Ask Me Anything...")
                })

            Spacer(modifier = Modifier.width(8.dp))
            val customIcon2 = ImageVector.vectorResource(R.drawable.send_email_svgrepo_com)

            Icon(modifier = Modifier
                .size(40.dp)
                /*.clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary)*/
                .clickable {
                    chatViewModel.onEvent(ChataUIEvent.SendPrompt(chatState.prompt, bitmap))
                    uriState.update { "" }

                },
                imageVector = customIcon2,
                contentDescription = "Send prompt",
                tint = MaterialTheme.colorScheme.primary


            )



        }
    }



}


    @Composable
    fun UserChatItem(prompt: String, bitmap: Bitmap?) {

        Column (
            modifier = Modifier.padding(start = 100.dp , bottom = 22.dp)
        ){

            bitmap?.let {
                Image(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }
            
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                text = prompt,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }


    @Composable
    fun ModelChatItem(response: String) {

        Column (
            modifier = Modifier.padding(end = 100.dp , bottom = 22.dp)
        ){



            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(16.dp),
                text = response,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }


 @Composable
 private fun getBitmapFromUri(): Bitmap? {
     val uri = uriState.collectAsState().value
     val imagestate : AsyncImagePainter.State = rememberAsyncImagePainter(
     model = ImageRequest.Builder(LocalContext.current)
         .data(uri)
         .size(coil.size.Size.ORIGINAL)
         .build()
     ).state

     if (imagestate is AsyncImagePainter.State.Success) {
         return imagestate.result.drawable.toBitmap()
     }
     return null



 }



}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )


   /* @Composable
    fun GeminiChatBotTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colorScheme = darkColors(
                primary = Color(0xFFBB86FC),
                primaryVariant = Color(0xFF3700B3),
                secondary = Color(0xFF03DAC6),
                background = Color(0xFF121212),
                surface = Color(0xFF121212),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
        ) {
            content()
        }
    }*/

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeminiChatBotTheme {
        Greeting("Android")
    }
}