package com.example.rtspudp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StreamData.init(this)

        setContent {
            MaterialTheme {
                StreamListScreen(
                    streams = StreamData.streams,
                    onStreamClick = { position ->
                        val intent = Intent(this, VideoActivity::class.java)
                        intent.putExtra("position", position)
                        startActivity(intent)
                    },
                    onAddStream = { name, url ->
                        val protocol = determineProtocol(url)
                        StreamData.addStream(this, StreamDataModel(
                            name = name.ifBlank { "Custom Stream ($protocol)" },
                            url = url,
                            protocol = protocol
                        ))
                    },
                    onDeleteStream = { index ->
                        StreamData.deleteStream(this, index)
                    }
                )
            }
        }
    }

    private fun determineProtocol(url: String): String {
        return when {
            url.startsWith("rtsp://", ignoreCase = true) -> "RTSP"
            url.startsWith("rtmp://", ignoreCase = true) -> "RTMP"
            else -> "Unknown"
        }
    }
}