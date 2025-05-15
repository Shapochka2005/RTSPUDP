package com.example.rtspudp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.runtime.remember
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.example.rtspudp.database.AppDatabase
import com.example.rtspudp.repository.StreamRepository
import com.example.rtspudp.screens.video.VideoScreen
import kotlinx.coroutines.runBlocking

class VideoActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)

        setContent {
            val navController = rememberNavController()
            val repository = remember {
                StreamRepository(
                    database.userDao(),
                    database.userStreamDao()
                )
            }

            val streamId = intent.getIntExtra("streamId", -1)
            val stream = remember(streamId) {
                runBlocking {
                    repository.getAllStreams().find { it.id == streamId }
                }
            }

            VideoScreen(
                navController = navController,
                stream = stream
            )
        }
    }
}