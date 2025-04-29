package com.example.rtspudp

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class VideoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                VideoScreen(
                    position = intent.getIntExtra("position", -1),
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(position: Int) {
    val context = LocalContext.current
    val stream = remember(position) { StreamData.getByPosition(position) }

    var isLoading by remember { mutableStateOf(true) }
    var scale by remember { mutableFloatStateOf(1f) }
    var translationX by remember { mutableFloatStateOf(0f) }
    var translationY by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stream?.name ?: "Stream") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        translationX += pan.x
                        translationY += pan.y
                    }
                }
        ) {
            when (stream?.protocol) {
                "UDP" -> {
                    val player = remember { UdpStreamHandler.createPlayer(context) }
                    val surfaceView = remember { SurfaceView(context) }

                    DisposableEffect(stream) {
                        val handler = UdpStreamHandler(context, player, surfaceView)
                        handler.playStream(stream.url)

                        onDispose {
                            player.release()
                        }
                    }

                    DisposableEffect(player) {
                        val listener = object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                when (playbackState) {
                                    Player.STATE_BUFFERING -> {
                                        isLoading = true
                                        Log.d("Media3", "Buffering...")
                                    }
                                    Player.STATE_READY -> {
                                        isLoading = false
                                        Log.d("Media3", "Playback ready")
                                    }
                                    Player.STATE_ENDED -> Log.d("Media3", "Playback ended")
                                    Player.STATE_IDLE -> Log.d("Media3", "Player idle")
                                }
                            }

                            override fun onPlayerError(error: PlaybackException) {
                                isLoading = false
                                Log.e("Media3", "Playback error", error)
                            }
                        }

                        player.addListener(listener)

                        onDispose {
                            player.removeListener(listener)
                        }
                    }

                    AndroidView(
                        factory = { surfaceView },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = translationX,
                                translationY = translationY
                            )
                    )
                }
                "RTSP" -> {
                    val (libVlc, mediaPlayer) = remember {
                        val libVLC = RtspStreamHandler.createLibVLC(context)
                        val player = MediaPlayer(libVLC)
                        libVLC to player
                    }

                    DisposableEffect(stream) {
                        val handler = RtspStreamHandler(context, mediaPlayer, libVlc)
                        handler.playStream(stream.url)

                        onDispose {
                            mediaPlayer.stop()
                            mediaPlayer.release()
                            libVlc.release()
                        }
                    }

                    DisposableEffect(mediaPlayer) {
                        mediaPlayer.setEventListener { event ->
                            when (event.type) {
                                MediaPlayer.Event.Buffering -> {
                                    if (event.buffering == 100f) {
                                        isLoading = false
                                        Log.d("VLC", "Buffering complete")
                                    }
                                }
                                MediaPlayer.Event.Opening -> Log.d("VLC", "Connecting...")
                                MediaPlayer.Event.Playing -> {
                                    isLoading = false
                                    Log.d("VLC", "Stream started!")
                                }
                                MediaPlayer.Event.Paused -> Log.d("VLC", "Paused")
                                MediaPlayer.Event.Stopped -> Log.d("VLC", "Stopped")
                                MediaPlayer.Event.EndReached -> Log.d("VLC", "Stream ended")
                                MediaPlayer.Event.EncounteredError -> {
                                    isLoading = false
                                    Log.e("VLC", "Error: ${event.type}")
                                }
                                else -> {
                                    Log.d("VLC", "Unhandled event: ${event.type}")
                                }
                            }
                        }

                        onDispose {
                            mediaPlayer.setEventListener(null)
                        }
                    }

                    AndroidView(
                        factory = { ctx ->
                            VLCVideoLayout(ctx).apply {
                                mediaPlayer.attachViews(this, null, false, false)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = translationX,
                                translationY = translationY
                            )
                    )
                }
                else -> {
                    Text(
                        text = "Unsupported protocol: ${stream?.protocol}",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}