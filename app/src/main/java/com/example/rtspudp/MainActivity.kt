package com.example.rtspudp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StreamData.init(listOf(
            StreamDataModel(
                name = "RTSP protocol",
                url = "rtsp://192.168.19.225:4554/test",
                protocol = "RTSP"
            ),
            StreamDataModel(
                name = "UDP protocol",
                url = "udp://@239.1.3.4:5000", // Regular UDP (replace with your server IP)
                protocol = "UDP"
            )
        ))

        setContent {
            MaterialTheme {
                StreamListScreen(
                    streams = StreamData.getAll(),
                    onStreamClick = { position ->
                        val intent = Intent(this, VideoActivity::class.java)
                        intent.putExtra("position", position)
                        startActivity(intent)
                    }
                )

            }
        }
    }
}